---
weight: -30
date: 2016-04-14T00:33:00-04:00
title: Developing the first plugin
summary: "We will step through developing one complete plugin, which will add support for a new type of files holding LC/MS feature information, which will be viewable as a table and can be overlaid on top of Map 2D view."
menu:
  main:
    parent: Tutorials
    identifier: "Developing the first plugin"
    weight: 30
---

In this guide we will develop a new module for BatMass that will add support for a new file format for detected LC/MS features.  
We will need to:  

- [Create a new NetBeans module](#create-a-new-netbeans-module)
- [Create a parser for the file](#create-a-parser-for-the-file)
- [Add recognition support for the new file type](#add-recognition-support-for-the-new-file-type)
- [Add support for importing the file into a project](#add-support-for-importing-the-file-into-a-project)
- [Add basic support for viewing the data in tabular viewer](#add-basic-support-for-viewing-the-data-in-tabular-viewer)
- [Add support for overlay of data over Map2D](#add-support-for-overlay-of-data-over-Map2D)
- [Add the feature that will allow us to double click a row in the table and automatically zoom into the region of interest in Map2D](#add-double-click-navigation-between-viewers)

The prerequisite for this tutorial is that you have the development environment set up. If you don't make sure to follow

## Create a new NetBeans module
Open `BatMass` module suite in NetBeans, expand its node in Project Explorer, right click the `Modules` node, click `Add New`. Follow the instructions in the wizard to create a new module. Give it a descriptive, unique name and _Code Name Base_, the code name base will be used as the base package of all the source files. All _BatMass_ modules use `umich.ms.batmass` as the base-name and add suffixes for different modules, e.g. `umich.ms.batmass.filesupport` for the _File Support_ module. This is needed to avoid class-name clashes.

![Add new module](/images/developing-first-plugin/add-new-module-01.png)
![Give the module a name](/images/developing-first-plugin/add-new-module-02.png)
![Provide code name base](/images/developing-first-plugin/add-new-module-03.png)

Add 3 packages `data`, `model` and `providers`.  

- The `model` package will contain our java model for the data in the file.
- The `data` package will contain the wrappers which will adapt the raw data model to the format suitable for viewers.
- The `providers` package will hold the necessary infrastructure to hook into the project system - it will give a way to import existing files into the project, provide an icon for the node, etc.


## Adding support for the new file-type
We'll be working in the `providers` package.  
You can find a working example of what we're about to create in package `umich.ms.batmass.filesupport.files.types.xcms.peaks.providers` of `FileSupport (BatMass)` module.  

The first thing to do is to make the system recognize the new files.  

### Registering a TypeResolver
To make the system recognize new files we need to register an implementation of `umich.ms.batmass.filesupport.core.spi.filetypes.FileTypeResolver` interface using `@FileTypeResolverRegistration` annotation. To simplify things there's an abstract class `AbstractFileTypeResolver`, so we'll use that.

Create a class `UmpireTypeResolver` extending `AbstractFileTypeResolver`. Then copy the contents of `XCMSCsvPeaksTypeResolver` for simplicity and modify as needed. After copy-pasting you'll notice that some parts are underlined with a red wavy line (specifically `@StaticResource`, `ImageUtilities`). Those two come from other NetBeans modules provided by the NetBeans platform, so we'll need to add dependencies. There is a simpler way, rather than going to the _Properties_ of your module. Place the cursor on the line with an error and press <kbd>Alt+Enter</kbd> and select `Search module dependency for ...`, in this case there will be a single search hit for both errors: "Utilities API" and "Common Annotations".  

The `@StaticResource` annotation is particularly useful - it checks if a static resource can be found at the provided path. If you copy-pasted everything from `XCMSCsvPeaksTypeResolver`, then after adding the correct dependency the line
```java
@StaticResource
public static final String ICON_BASE_PATH = "umich/ms/batmass/filesupport/resources/features_16.png";
```
should still be underlined with the error saying that it "cannot find resource".  
Create a new package `resources` under `umich.ms.batmass.diaumpire` and put some 16x16 pixels icon there. You can copy `features_16.png` from `umich.ms.batmass.filesupport.resources` (which is in `FileSupport (BatMass)` module) and change the `ICON_BASE_PATH` to the correct path to the new icon. Fix other things, like the FileFilter, which will be used in the FileChooser when the user imports the file in.  

I ended up with the following class, everything should be clear to you, except maybe `BMFileFilter` and `@FileTypeResolverRegistration`. I used `FileFilterUtils.suffixFileFilter(EXT, IOCase.INSENSITIVE)` instead of an extension filter, because we want to match the files that end in "\_PeakCluster.csv" and we'll get to `@FileTypeResolverRegistration` right after the code listing:  
```java
@FileTypeResolverRegistration(
        fileCategory = AgilentCefTypeResolver.CATEGORY,
        fileType = AgilentCefTypeResolver.TYPE
)
public class AgilentCefTypeResolver extends AbstractFileTypeResolver {
    private static final AgilentCefTypeResolver INSTANCE = new AgilentCefTypeResolver();

    @StaticResource
    public static final String ICON_BASE_PATH = "umich/ms/batmass/filesupport/resources/features_16.png";
    public static final ImageIcon ICON = ImageUtilities.loadImageIcon(ICON_BASE_PATH, false);

    public static final String CATEGORY = "features";
    public static final String TYPE = "umpire-se";
    protected static final String EXT = "_PeakCluster.csv";
    protected static final BMFileFilter FILE_FILTER = new UmpireSeFileFilter();
    protected static final String DESCRIPTION = "DIA-Umpire Signal Extraction peak clusters";

    public static AgilentCefTypeResolver getInstance() {
        return INSTANCE;
    }

    @Override
    public String getCategory() {
        return CATEGORY;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public ImageIcon getIcon() {
        return ICON;
    }

    @Override
    public String getIconPath() {
        return ICON_BASE_PATH;
    }

    @Override
    public boolean isFileOnly() {
        return true;
    }

    @Override
    public BMFileFilter getFileFilter() {
        return FILE_FILTER;
    }

    public static class UmpireSeFileFilter extends BMFileFilter {

        public UmpireSeFileFilter() {
            super(FileFilterUtils.suffixFileFilter(EXT, IOCase.INSENSITIVE));
        }

        @Override
        public String getShortDescription() {
            return EXT;
        }

        @Override
        public String getDescription() {
            return DESCRIPTION;
        }
    }
}
```


## Create a parser for the file
We will use DIA-Umpire Signal Extraction text output file as an example. It's a simple delimited text format with the first line being column headers, the rest being data stored as text. Even though these are simple text delimited files, we will not be using a library for parsing, instead we'll provide a fast home-grown reader. We'll be using existing helper classes from other _BatMass_ modules for that.  

To use functionality provided by some NetBeans module from another module two requirements must be met:

- The module providing the functionality should declare the package it wants to expose to the outside world as public API
- The module that needs to use that functionality must declare a dependency on the providing module. For code completion to work as expected, first compile the provider module.

### Providing public API from modules
Expand `BatMass -> Modules` node in the project explorer, open `FileSupport Core (BatMass)` module by double clicking on it. In _Source Packages_ node navigate to `umich.ms.batmass.filesupport.core.util` package. We will be using the `DelimitedFiles` class. This package is already declared public, but for the learning purposes let's go ahead and check that. Right click `FileSupport Core (BatMass)` module in the Project Explorer and go to `Properties -> API Versioning`. You should see that checkboxes of all packages but one are already selected, these packages will be available to other modules if they set dependency on the `FileSupport Core (BatMass)` module. If you change selection here, recompile the module by right clicking the module node, and running _Build_ (_Clean Build_ is not necessary).

![Expose public API from module](/images/developing-first-plugin/expose-public-api-from-module.png)

### Setting another module as a dependency
Now right click the newly created `DIA Umpire SE Features` module, go to `Properties -> Libraries`. In the `Module Dependencies` tab click `Add Dependency`. In the search bar at the top start typing "FileSupport", it should find 2 modules: "FileSupport (BatMass)" and "FileSupport Core (BatMass)". We're interested in the second one, so select it and click OK, you should now see it in the dependencies list.

![Search for module dependency](/images/developing-first-plugin/search-for-module-dependency.png)
![Module dependency added](/images/developing-first-plugin/module-dependency-added.png)

We should now be able to use `umich.ms.batmass.filesupport.core.util.DelimitedFiles` in our module.  


It might seem like a lot of work at first, but as you get used to it, it's not harder than adding dependencies in maven, for example. It is also possible to create maven-based NetBeans modules, but we will not be covering that in this tutorial and this is not used in BatMass.

### Model the data
{{< note title="Note" >}}
You can skip that part of this section if you like. It just describes the usual Java stuff of reading a file and parsing it into some POJO (Plain Old Java Object). This does not have to be done the way described here.
{{< /note >}}

You can find a sample Umpire Signal Extraction text output file in the folder _DiaUmpireSeFeatures/resources_ inside _BatMass_ project. We will not be using all the columns from the file, only a few. Here's our model class:
```java
public class UmpireIsoCluster {
    protected double rtLo;
    protected double rtHi;
    protected int scanNumLo;
    protected int scanNumHi;
    protected int charge;
    protected double mz1;
    protected double mz2;
    protected double mz3;
    protected double mz4;
    protected double peakHeight;
    protected double peakArea;

    /**
     * Bare minimum info required to plot something.
     * @param rtLo
     * @param rtHi
     * @param mz1
     */
    public UmpireIsoCluster(double rtLo, double rtHi, double mz1) {
        this.rtLo = rtLo;
        this.rtHi = rtHi;
        this.mz1 = mz1;
    }

    // ... getters and setters
}
```

And here is the simple but relatively fast reader for the delimited text file. All it does is read the first line of the file to get the column names, it then maps the indexes of the columns of interest to the indexing recognized by our parser, so that if the column order ever changes in the file, the parser will still work.

```java
/**
 * A simple array-list storage for parsed LCMS features from Umpire PeakCluster csv files.
 * @author Dmitry Avtonomov
 */
public class UmpireIsoClusters {
    public static String COL_NAME_RT_LO = "StartRT";
    public static String COL_NAME_RT_HI = "EndRT";
    public static String COL_NAME_SCAN_NUM_LO = "StartScan";
    public static String COL_NAME_SCAN_NUM_HI = "EndScan";
    public static String COL_NAME_CHARGE = "Charge";
    public static String COL_NAME_MZ1 = "mz1";
    public static String COL_NAME_MZ2 = "mz2";
    public static String COL_NAME_MZ3 = "mz3";
    public static String COL_NAME_MZ4 = "mz4";
    public static String COL_NAME_PEAK_HEIGHT = "PeakHeight1";
    public static String COL_NAME_PEAK_AREA = "PeakArea1";

    protected List<UmpireIsoCluster> clusters;

    public UmpireIsoClusters() {
        clusters = new ArrayList<>();
    }

    public List<UmpireIsoCluster> getClusters() {
        return clusters;
    }

    /**
     * Factory method to create UmpireIsoClusters object from a file.
     * @param path the file to parse data from
     * @return
     */
    public static UmpireIsoClusters create(Path path) throws IOException {
        if (!Files.exists(path))
            throw new IllegalArgumentException("File path for XCMS peaks does not exist.");

        // first read the header to figure out which column indexes we need
        String[] headers = null;
        try (InputStream is = new FileInputStream(path.toFile())) {
            headers = DelimitedFiles.readDelimitedHeader(is, ',');
        }
        int[] colMapping = new int[headers.length];
        findHeaderIndex(headers, COL_NAME_RT_LO, colMapping, 0);
        findHeaderIndex(headers, COL_NAME_RT_HI, colMapping, 1);
        findHeaderIndex(headers, COL_NAME_SCAN_NUM_LO, colMapping, 2);
        findHeaderIndex(headers, COL_NAME_SCAN_NUM_HI, colMapping, 3);
        findHeaderIndex(headers, COL_NAME_CHARGE, colMapping, 4);
        findHeaderIndex(headers, COL_NAME_MZ1, colMapping, 5);
        findHeaderIndex(headers, COL_NAME_MZ2, colMapping, 6);
        findHeaderIndex(headers, COL_NAME_MZ3, colMapping, 7);
        findHeaderIndex(headers, COL_NAME_MZ4, colMapping, 8);
        findHeaderIndex(headers, COL_NAME_PEAK_HEIGHT, colMapping, 9);
        findHeaderIndex(headers, COL_NAME_PEAK_AREA, colMapping, 10);
        UmpireNumberParser parser = new UmpireNumberParser(colMapping);

        UmpireIsoClusters clusters = new UmpireIsoClusters();
        clusters.getClusters();
        try (BufferedReader br = new BufferedReader(new FileReader(path.toFile()))) {
            String line;
            while ((line = br.readLine()) != null) {
                DelimitedFiles.readLineOfNumbers(line, ',', '.', parser);
            }
        }
        return clusters;
    }

    /**
     * Find the index of a string within an array of strings.
     * @param headers
     * @param header
     * @return -1 if the header was not found in the array of headers
     */
    private static void findHeaderIndex(String[] headers, String header, int[] colMapping, int map) {
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].equals(header)) {
                colMapping[i] = map;
                break;
            }
        }
        throw new IllegalStateException(String.format(
                "Header '%s' not found among column headers of DIA Umpire PeakCluster file.",
                header));
    }
}
```

And this is the parser that actually takes the values provided by the reader, converts them to appropriate data types and populates the fields of its internal object.

```java
public class UmpireNumberParser extends DelimitedFiles.NumberParsingDelegate {
    protected UmpireIsoCluster cluster;
    protected int[] colMapping;

    public UmpireNumberParser(int[] colMapping) {
        cluster = new UmpireIsoCluster();
        this.colMapping = colMapping;
    }

    @Override
    public void parse(int idx, int number, int length, int decimalPos) {
        int mapping = colMapping[idx];
        switch (idx) {
            case 0:
                cluster.setRtLo(DelimitedFiles.parseDouble(number, length, decimalPos));
                break;
            case 1:
                cluster.setRtHi(DelimitedFiles.parseDouble(number, length, decimalPos));
                break;
            case 2:
                cluster.setScanNumLo(DelimitedFiles.parseInt(number, length, decimalPos));
                break;
            case 3:
                cluster.setScanNumHi(DelimitedFiles.parseInt(number, length, decimalPos));
                break;
            case 4:
                cluster.setCharge(DelimitedFiles.parseInt(number, length, decimalPos));
                break;
            case 5:
                cluster.setMz1(DelimitedFiles.parseDouble(number, length, decimalPos));
                break;
            case 6:
                cluster.setMz2(DelimitedFiles.parseDouble(number, length, decimalPos));
                break;
            case 7:
                cluster.setMz3(DelimitedFiles.parseDouble(number, length, decimalPos));
                break;
            case 8:
                cluster.setMz4(DelimitedFiles.parseDouble(number, length, decimalPos));
                break;
            case 9:
                cluster.setPeakHeight(DelimitedFiles.parseDouble(number, length, decimalPos));
                break;
            case 10:
                cluster.setPeakArea(DelimitedFiles.parseDouble(number, length, decimalPos));
                break;
        }
    }
}
```

## Enabling viewing of data
