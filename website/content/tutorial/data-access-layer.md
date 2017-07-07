---
title: Using data access library
summary: "The data access library provides a relatively rich API to mzML/mzXML files (MS level, polarity, precursor isolation window, instrument data, etc.) and a few other file formats common to the proteomics field, such as PepXML, ProtXML and MzIdentML. In this tutorial will step through parsing some data, using the library as a jar in a simple console window application."
menu:
  main:
    parent: Tutorials
    identifier: "Using data access library"
    weight: 53
---

In this guide we will quickly go through using the standalone java library for accessing some common mass spectrometry data formats. This is the same library that powers _BatMass_.

All the classes responsible for parsing files live in `umich.ms.fileio.filetypes` package, each in its own subpackage, e.g. `umich.ms.fileio.filetypes.pepxml` for PepXML files. Most of those sub-packages contain a separate package `example` with working examples.  

The source code for the library lives in [MSFTBX repository on GitHub](https://github.com/chhh/msftbx). Start by cloning:  
`git clone https://github.com/chhh/MSFTBX.git`
and explore

## Parsing LC/MS data (mzML/mzXML files)
Unfortunately, it's near impossible to easily access raw mass spec data from the original vendor file formats using java. You can convert most data from proprietary formats (_.RAW_ files for Thermo, _.d_ directories for Agilent, etc.) using `msconvert` program from [ProteoWizard](http://proteowizard.sourceforge.net/).

The API is separated into two parts. First you create a data source from your file. The data source can be used by itself, if you just want to iterate over spectra by yourself. It can also be attached to a special data structure, which handles data loading, management, indexing and garbage collection.

mzML and mzXML share the same common base interface `umich.ms.fileio.filetypes.LCMSDataSource`, you can use that if you want to write code that can work seamlessly with both file formats.
We will start by creating a data source and just reading spectra one by one, which is very ineffective.
```java
public static void main(String[] args) throws FileParsingException {

  // Creating data source
  Path path = Paths.get("some-path-to.mzXML");
  path = Paths.get(args[0]);
  MZXMLFile source = new MZXMLFile(path.toString());

  // Notice that we use fetchIndex() instead of getIndex().
  // fetchIndex() will either get a cached copy or parse it from
  // disk, if no cache is available. The index will be cached after parsing.
  MZXMLIndex index = source.fetchIndex();

  // The index gives you the scan numbers, on the lowest level you can parse
  // the file using those numbers. We need the raw scan numbers (the numbers
  // as they're used in the file). The internal scan numbering scheme always
  // renumbers all scans starting from 1 and increasing by 1 consecutively.

  for (Integer scanNumRaw : index.getMapByRawNum().keySet()) {
      // The second parameter asks the parser to parse the spectrum along
      // with meta-info about the scan itself
      IScan scan = source.parseScan(scanNumRaw, true);

      // Do something with the scan.
      // Note that some features, like scan.getChildScans() will not work in
      // this case, as there is not enough information to build those
      // relationships.

      System.out.println(scan.toString());
  }
}
```
Notice how much faster the parsing has been and that only MS1 scans reported the number of points in the spectrum.

It is a lot more effective to load data in chunks, rather than manually keeping track of what's been loadad and what needs unloading, we will instead use the `ScanCollectionDefault` data structure to that for us.
```java
public static void main(String[] args) throws FileParsingException {

  // Creating data source
  Path path = Paths.get("some-path-to.mzXML");
  path = Paths.get(args[0]);
  MZXMLFile source = new MZXMLFile(path.toString());

  // This is a data structure used to store scans and to navigate around the run
  ScanCollectionDefault scans = new ScanCollectionDefault();
  // Softly reference spectral data, make it reclaimable by GC
  scans.setDefaultStorageStrategy(StorageStrategy.SOFT);
  // Set it to automatically re-parse spectra from the file if spectra were not
  // yet parsed or were reclaimed to make auto-loading work you'll need to use
  // IScan#fetchSpectrum() method instead of IScan#getSpectrum()
  scans.isAutoloadSpectra(true);

  // Set our mzXML file as the data source for this scan collection
  scans.setDataSource(source);
  // Set number of threads for multi-threaded parsing.
  // null means use as many cores as reported by Runtime.getRuntime().availableProcessors()
  source.setNumThreadsForParsing(null);
  // load the meta-data about the whole run, with forced parsing of MS1 spectra
  // as we have enabled auto-loading, then if we ever invoke IScan#fetchSpectrum()
  // on an MS2 spectrum, for which the spectrum has not been parsed, it will be
  // obtained from disk automatically. And because of Soft referencing, the GC
  // will be able to reclaim it.
  scans.loadData(LCMSDataSubset.MS1_WITH_SPECTRA);

  // let's traverse the data-structure
  TreeMap<Integer, IScan> num2scanMap = scans.getMapNum2scan();
  for (IScan scan : num2scanMap.values()) {
    ISpectrum spectrum = scan.getSpectrum();
    if (spectrum == null) {
      System.out.printf("%s does NOT have a parsed spectrum\n", scan.toString());
    } else {
      System.out.printf("%s has a parsed spectrum, it contains %d data points\n",
                        scan.toString(), spectrum.getMZs().length);
    }
  }

}
```

Here's a more complex example, loading spectra for custom ranges (scan number range and MS level combination) and adding better memory management for long-running applications.
```java
public static void main(String[] args) throws FileParsingException {
  // Creating data source
  Path path = Paths.get("some-path-to.mzXML");
  path = Paths.get(args[0]);
  MZXMLFile source = new MZXMLFile(path.toString());

  // Get the index (fetchXXX() methods will parse data from the file if it has not
  // yet been parsed) and cache it in the object for reuse.
  // You'll only need the index if you want to convert between internal scan numbers
  // and raw scan numbers in the file. Some files might have non-consecutive scan
  // numbers, for example, but internally they'll be renumbered to start from
  // 1 and increment by one for each next scan.
  MZXMLIndex idx = source.fetchIndex();
  // info about the run
  LCMSRunInfo runInfo = source.fetchRunInfo();


  // To parse a single scan from the file (or a range of scans) we first create a
  // predicate matching the scan to be parsed.
  // For example, parse scans from 1 to 3 at MS level 2.
  Set<Integer> msLevel = Collections.singleton(2);
  LCMSDataSubset subset = new LCMSDataSubset(1, 3, msLevel, null);
  List<IScan> parsedScans = source.parse(subset);

  // If you want higher level access to data, create an LCMSData object
  LCMSData data = new LCMSData(source);
  // load the whole structure of the run, and parse all spectra for MS1 scans
  data.load(LCMSDataSubset.WHOLE_RUN);
  data.releaseMemory();

  // or load the whole structure, but only get m/z-intensity info at MS level 2
  data.load(new LCMSDataSubset(null, null, msLevel, null));
  data.releaseMemory();
  // alternatively, use this shortcut
  data.load(LCMSDataSubset.MS2_WITH_SPECTRA);
  data.releaseMemory();

  // If you need memory management, you can also pass an instance of an object,
  // which will be considered the owner of prased data. When this object is
  // garbage collected, this will be detected automatically  and corresponding
  // spectra released.
  Object dataUser = new Object();
  data.load(LCMSDataSubset.WHOLE_RUN, dataUser);
  System.out.printf("The data is loaded and used by [%s] object.\n",
                    System.identityHashCode(dataUser));
  // at this point dataUser might be garbage collected as it's not referenced anymore,
  // and the data might get unloaded automatically
  dataUser = null; // just to be sure that we don't have a strong reference

  // If you don't want to fiddle around with memory management at all, but still
  // want it to play nicely there's one more feature - auto-loading of spectra.
  // You can parse the whole structure of the file and keep it in memory (it's
  // rather small), and just magically get the spectra whenever you need them.
  // Also set referenceing type to soft, so that garbage collector could reclaim
  // unused spectra.
  data.load(LCMSDataSubset.STRUCTURE_ONLY);

  IScanCollection scans = data.getScans();
  scans.isAutoloadSpectra(true); // set automatic spectra loading
  scans.setDefaultStorageStrategy(StorageStrategy.SOFT); // mz-intensity data will be softly referenced
  TreeMap<Integer, ScanIndex> msLevel2index = scans.getMapMsLevel2index();
  ScanIndex ms2idx = msLevel2index.get(2); // get the index at MS level 2

  // we'll iterate by scan numbers
  TreeMap<Integer, IScan> num2scan = ms2idx.getNum2scan();
  Set<Map.Entry<Integer, IScan>> scanEntries = num2scan.entrySet();
  for (Map.Entry<Integer, IScan> scanEntry : scanEntries) {
    Integer scanNum = scanEntry.getKey();
    IScan scan = scanEntry.getValue();

    // note that we use fetchXXX() method here, because we've only parsed the structure
    // of the file, which includes scan meta-data, but not the spectra themselves
    ISpectrum spectrum = scan.fetchSpectrum();
    int scanNumInternal = scan.getNum(); // internal scan number (1 based)
    IndexElement idxElem = idx.getByNum(scanNumInternal);
    int scanNumRaw = idxElem.getRawNumber();
    int numPoints = spectrum.getMZs().length;
    System.out.printf("Scan #%d (raw #%d) contained %d data points\n",
                      scanNumInternal, scanNumRaw, numPoints);
  }

  // You can use the ScanCollection API to navigate around the LCMS run.
  // E.g., get the number fo the first scan at ms lelvel 2
  Integer firstMS2ScanNum = scans.getMapMsLevel2index().get(2).getNum2scan().firstKey();
  IScan scan = scans.getScanByNum(firstMS2ScanNum);
  // Now get the next scan at the same MS level
  scan = scans.getNextScanAtSameMsLevel(scan);

  // Because we did parsing of the whole structure, an important method was called
  // automagically for us: ScanCollectionHelper.finalizeScanCollection(scans),
  // which sets up parent child relations between scans even if that information was
  //  not in the scan meta-data. You can also call this method yourself if it you
  // only parse a portion of the file
  String parentScanRef = scan.getPrecursor().getParentScanRefRaw();
  System.out.printf("Scan #%d (MS%d) is a child scan of {%s}\n",
                    scan.getNum(), scan.getMsLevel(), parentScanRef);

  data.releaseMemory();
}
```
I hope the comments in the code are enough to get you started.


## Parsing identification files (PepXML, ProtXML, MzIdentML)
The library gives low level access to those file formats. There is no unifying API here, as the formats are very different. These parsers are not hand optimized for efficiency, so they might consume quite a bit more memory than they should, but they also are error resilient.

Working with these files is simpler, you call the parser and get a single data-structure, that follows the schemas of corresponding XMLs.

```java

Path path = Paths.get("some-path-to.pep.xml");

// a single call to parse the whole file
MsmsPipelineAnalysis msmsPipelineAnalysis = PepXmlParser.parse(path);


List<MsmsRunSummary> msmsRunSummaries = msmsPipelineAnalysis.getMsmsRunSummary();
for (MsmsRunSummary msmsRunSummary : msmsRunSummaries) {
    List<SpectrumQuery> spectrumQueries = msmsRunSummary.getSpectrumQuery();
    System.out.printf("Spectrum queries from MS/MS run summary: %s\n",
                      msmsRunSummary.getBaseName());
    for (SpectrumQuery sq : spectrumQueries) {
        System.out.printf("Spec ID: [%s], RT: [%.2f], precursor neutral mass: [%.3f]\n",
                          sq.getSpectrum(), sq.getRetentionTimeSec(), sq.getPrecursorNeutralMass());
    }
    System.out.printf("Done with MS/MS run summary: %s\n", msmsRunSummary.getBaseName());
}
```

## Parsing huge identification files more efficiently
Sometimes you might have PepXML files that are many gigabytes in size, this happens when you combine search results from multiple experiments and store them in a single output file. In that case, using `XMLStreamReader` class it is possible to first rewind the input stream to some large structural element of the underlying file, such as `<msms_run_summary>` in PepXML files.  
You will need to have an idea of how the files are organized for this to work in general though, explore the corresponding XML schemas. The schemas can also be found in the sources of the library in file-specific sub-packages of `umich.ms.fileio.filetypes` in `resources` directories.

```java
try {
  // we'll manually iterate over msmsRunSummaries - won't need so much memory
  // at once for processing large files.
  JAXBContext ctx = JAXBContext.newInstance(MsmsRunSummary.class);
  Unmarshaller unmarshaller = ctx.createUnmarshaller();

  XMLInputFactory xif = XMLInputFactory.newFactory();
  StreamSource ss = new StreamSource(is);
  XMLStreamReader xsr = xif.createXMLStreamReader(ss);


  while (advanceReaderToNextRunSummary(xsr)) {
    // we've advanced to the next MsmsRunSummary in the file
    long timeLo = System.nanoTime();
    JAXBElement<MsmsRunSummary> unmarshalled = unmarshaller
                                          .unmarshal(xsr, MsmsRunSummary.class);
    long timeHi = System.nanoTime();
    System.out.printf("Unmarshalling took %.4fms (%.2fs)\n",
                      (timeHi-timeLo)/1e6, (timeHi-timeLo)/1e9);
    MsmsRunSummary runSummary = unmarshalled.getValue();
    if (runSummary.getSpectrumQuery().isEmpty()) {
      String msg = String.format("Parsed msms_run_summary was empty for file " +
          "'%s', summary base_name '%'", uri.toString(), runSummary.getBaseName());
      System.out.println(msg);
    }
  }
} catch (JAXBException | XMLStreamException e) {
  // do something with the exception
}

```
and here is the meat of it, the code to rewind the `XMLStreamReader` - `advanceReaderToNextRunSummary(XMLStreamReader)`.
In this case the example assumes we try to parse multiple msms_run_summary tags one by one from the file.
```java

private static final String TAG_RUN_SUMMARY = "msms_run_summary";

private static boolean advanceReaderToNextRunSummary(XMLStreamReader xsr)
    throws XMLStreamException {
  long timeLo = System.nanoTime();
  do {
      if (xsr.next() == XMLStreamConstants.END_DOCUMENT)
          return false;
  } while (!(xsr.isStartElement() && xsr.getLocalName().equals(TAG_RUN_SUMMARY)));

  long timeHi = System.nanoTime();
  System.out.printf("Advancing reader took: %.4fms\n", (timeHi-timeLo)/1e6d);

  return true;
}
```
