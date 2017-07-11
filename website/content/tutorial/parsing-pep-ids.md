---
weight: 52
title: Parsing pep.xml files
summary: "The data access library provides parsers for file formats common to the proteomics field, such as PepXML, ProtXML and MzIdentML. In this tutorial I'll show you how to parse a PepXML file."
menu:
  main:
    parent: Tutorials
    identifier: "Parsing pep xml files"
---

All the classes responsible for parsing files live in `umich.ms.fileio.filetypes` package, each in its own subpackage, e.g. `umich.ms.fileio.filetypes.pepxml` for PepXML files. Most of those sub-packages contain a separate package `example` with working examples.  

## Parsing identification files (PepXML, ProtXML, MzIdentML)
The library gives low level access file formats storing peptide identifications.
There is no unifying API here, as the formats are very different. These parsers are not hand optimized for efficiency, so they might consume quite a bit more memory than they should, but they also are error resilient.

Working with these files is as simple as making a single call to `parse(Path)` method
of a corresponding parser. You get a single data-structure that follows the respective
XML schemas for the format. Here's a quick PepXML example:

```java
Path path = Paths.get("some-path-to.pep.xml");
// a single call to parse the whole file
MsmsPipelineAnalysis analysis = PepXmlParser.parse(path);
```

And that's it. The whole file is parsed and stored in memory. Let's explore
the contents of the file:

```java
// iterate over the parsed search results
List<MsmsRunSummary> runSummaries = analysis.getMsmsRunSummary();
for (MsmsRunSummary runSummary : runSummaries) {
    List<SpectrumQuery> spectrumQueries = runSummary.getSpectrumQuery();
    System.out.printf("Spectrum queries from MS/MS run summary: %s\n",
                      runSummary.getBaseName());
    for (SpectrumQuery sq : spectrumQueries) {
        System.out.printf("Spec ID: [%s], RT: [%.2f], precursor neutral mass: [%.3f]\n",
                          sq.getSpectrum(), sq.getRetentionTimeSec(), sq.getPrecursorNeutralMass());
    }
    System.out.printf("Done with MS/MS run summary: %s\n", runSummary.getBaseName());
}
```

## Parsing huge identification files more efficiently
Sometimes you might have PepXML files that are many gigabytes in size. This happens when you combine search results from multiple experiments and store them in a single output file. In that case, using `XMLStreamReader` class it is possible to first rewind the input stream to some large structural element of the underlying file, such as `<msms_run_summary>` in PepXML files.  
You will need to have an idea of how the files are organized for this to work in general though, explore the corresponding XML schemas for insights. The schemas can also be found in the sources of the library in file-specific sub-packages of `umich.ms.fileio.filetypes` in `resources` directories.

```java
String file = "/path/to/some.pep.xml";
Path path = Paths.get(file);

try (FileInputStream fis = new FileInputStream(file)) {
    // we'll manually iterate over msmsRunSummaries - won't need so much memory
    // at once for processing large files.
    JAXBContext ctx = JAXBContext.newInstance(MsmsRunSummary.class);
    Unmarshaller unmarshaller = ctx.createUnmarshaller();

    XMLInputFactory xif = XMLInputFactory.newFactory();

    StreamSource ss = new StreamSource(fis);
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
            String msg = String.format("Parsed msms_run_summary was empty for " +
                        "'%s', summary base_name '%s'",
                        path.toUri().toString(), runSummary.getBaseName());
            System.out.println(msg);
        }
    }
}
```

The secret ingredient here is the code to rewind the `XMLStreamReader`, the `advanceReaderToNextRunSummary(XMLStreamReader)` method.
In this case the example assumes we try to parse multiple msms_run_summary tags one by one from the file.
```java
private static boolean advanceReaderToNextRunSummary(XMLStreamReader xsr)
    throws XMLStreamException {
  do {
      if (xsr.next() == XMLStreamConstants.END_DOCUMENT)
          return false;
  } while (!(xsr.isStartElement() && xsr.getLocalName().equals("msms_run_summary")));

  return true;
}
```

And here are all the import statements for the last example:
```java
import umich.ms.fileio.filetypes.pepxml.PepXmlParser;
import umich.ms.fileio.filetypes.pepxml.jaxb.standard.MsmsPipelineAnalysis;
import umich.ms.fileio.filetypes.pepxml.jaxb.standard.MsmsRunSummary;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
```
