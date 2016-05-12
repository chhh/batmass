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

The prerequisite for this tutorial is that you have the development environment set up. If you don't have that yet, make sure to go through [the setting up dev environment tutorial]({{< relref "tutorial/setting-up-development-environment.md" >}}).  

This tutorial might be a little too much if you had no exposure to the NetBeans platform development, so I also strongly recommend that you visit [the NetBeans platform documentation website](https://netbeans.org/features/platform/all-docs.html) and at least walk through [NetBeans Platform Quick Start tutorial](https://platform.netbeans.org/tutorials/nbm-quick-start.html) it provides. The documentation website has a lot of tutorials covering many aspects of the platform in great detail.

Also recommended are: [NetBeans Platform Runtime Container Tutorial](https://platform.netbeans.org/tutorials/nbm-runtime-container.html) - so you'll know the anatomy of a platform application better and the set of 10 videos links to which can be found [here](https://platform.netbeans.org/tutorials/nbm-10-top-apis.html).

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
You can find a working example of what we're about to create in package `umich.ms.batmass.filesupport.files.types.agilent.cef.providers` of `FileSupport (BatMass)` module.  

The first thing to do is to make the system recognize the new files.  

### Registering a TypeResolver
To make the system recognize new files we need to register an implementation of `umich.ms.batmass.filesupport.core.spi.filetypes.FileTypeResolver` interface using `@FileTypeResolverRegistration` annotation. To simplify things there's an abstract class `AbstractFileTypeResolver`, so we'll use that.

Create a class `UmpireTypeResolver` extending `AbstractFileTypeResolver`. Then copy the contents of `AgilentCefTypeResolver` for simplicity and modify as needed. After copy-pasting you'll notice that some parts are underlined with a red wavy line (specifically `@StaticResource`, `ImageUtilities`). Those two come from other NetBeans modules provided by the NetBeans platform, so we'll need to add dependencies. There is a simpler way, rather than going to the _Properties_ of your module. Place the cursor on the line with an error and press <kbd>Alt+Enter</kbd> and select `Search module dependency for ...`, in this case there will be a single search hit for both errors: "Utilities API" and "Common Annotations".  

The `@StaticResource` annotation is particularly useful - it checks if a static resource can be found at the provided path. If you copy-pasted everything from `AgilentCefTypeResolver`, then after adding the correct dependency the line
```java
@StaticResource
public static final String ICON_BASE_PATH = "umich/ms/batmass/filesupport/resources/features_16.png";
```
should still be underlined with the error saying that it "cannot find resource".  
Create a new package `resources` under `umich.ms.batmass.diaumpire` and put some 16x16 pixels icon there. You can copy `features_16.png` from `umich.ms.batmass.filesupport.resources` (which is in `FileSupport (BatMass)` module) and change the `ICON_BASE_PATH` to the correct path to the new icon. Fix other things, like the FileFilter, which will be used in the FileChooser when the user imports the file in.  

I ended up with the following class, everything should be clear to you, except maybe `BMFileFilter` and `@FileTypeResolverRegistration`. I used `FileFilterUtils.suffixFileFilter(EXT, IOCase.INSENSITIVE)` instead of an extension filter, because we want to match the files that end in "\_PeakCluster.csv". You'll also need to add a dependencies on "Lib Apache Commons IO" to make this work. We'll get to that `@FileTypeResolverRegistration` thing right after the code listing:  
```java
@FileTypeResolverRegistration(
        fileCategory = UmpireTypeResolver.CATEGORY,
        fileType = UmpireTypeResolver.TYPE
)
public class UmpireTypeResolver extends AbstractFileTypeResolver {
    private static final UmpireTypeResolver INSTANCE = new UmpireTypeResolver();

    @StaticResource
    public static final String ICON_BASE_PATH = "umich/ms/batmass/diaumpire/resources/features_16.png";
    public static final ImageIcon ICON = ImageUtilities.loadImageIcon(ICON_BASE_PATH, false);

    public static final String CATEGORY = "features";
    public static final String TYPE = "umpire-se";
    protected static final String EXT = "_PeakCluster.csv";
    protected static final BMFileFilter FILE_FILTER = new UmpireSeFileFilter();
    protected static final String DESCRIPTION = "DIA-Umpire Signal Extraction peak clusters";

    public static UmpireTypeResolver getInstance() {
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

Now as promised we move on to `@FileTypeResolverRegistration`. This annotation is defined in `FileSupport Core (BatMass)` module. What it does is it registers an instance of `FileTypeResolver` in the 'layer' file. If you've read/watched basic info about the NetBeans platform linked to in the beginning of this page, you should know that a platform application has a kind of an XML file in it which stores most configuration information. You can edit this file manually, however it is much less error prone to automate the process, and that is done with annotations and annotation processors. After you've annotate a class and try to build the module, the annotation processors are run first - they scan the source code for annotations that they (the processors) support and perform whatever actions are coded in them. With `@FileTypeResolverRegistration` annotation the `FileTypeResolverRegistrationProcessor` will read it and automagically create an entry in the 'layer'. Also, there is not just a single layer file, each module can have its own layer and layers from all the modules comprising an application are merged before application starts.  
The good news is that the NetBeans IDE provides a GUI to check the contents of the layer. In the Project Explorer open `FileSupport (BatMass)` module, and expand the _Important Files_ node in it, you will see the 'XML Layer' node in there. If you expand it, there are two entries:

- \<this layer\> - this one contains the layer entries that this particular module has in its layer file
- \<this layer in context\> - this one shows the aggregated layer file for the whole application and the modifications/additions by the current module's layer are shown in **bold** font.

![Locate XML Layer file](/images/developing-first-plugin/locate-xml-layer-file.png)

If you now take a look at _Important Files_ for the module we're creating, you won't find the 'XML Layer' entry there. Even though the annotation processor did its job, the entry only appears there after you create the "manual" version of the file, thankfully, adding the file is very easy: right click the module node in the Project Explorer, select `New -> Other -> Module Development -> XML Layer`.

![Create XML Layer file](/images/developing-first-plugin/create-layer-xml-file.png)

You should now have the _layer.xml_ file in the root of your source packages and also the 'XML Layer' node should have appeared under 'Important Files'. If you've built the module, you'll also see the instance of `UmpireTypeResolver` has been registered under `BatMass/FileTypeResolvers/features/umpire-se`.

![XML Layer file created](/images/developing-first-plugin/layer-file-created.png)

We're not done yet, this step only provided enough data to the system to be able to import the file in a project, we'll also need to specify how the file should be rendered in the Project Explorer. For that we need to register an implementation of `FileNodeInfo` interface in the layer. There's an annotation for that as well, see `AgilentCefNodeInfo` as an example.  

This one is much simpler, I'll just copy-paste everything from the `AgilentCefNodeInfo` class and change names as appropriate:

```java
@NodeInfoRegistration(
        fileCategory = UmpireTypeResolver.CATEGORY,
        fileType = UmpireTypeResolver.TYPE
)
public class UmpireNodeInfo extends AbstractFileNodeInfo{

    @Override
    public FileTypeResolver getFileTypeResolver() {
        return UmpireTypeResolver.getInstance();
    }
}
```

There's not much going on here, we'll be using the `AbstractFileNodeInfo` class as base, which only leaves the implementation of `getFileTypeResolver()` to us. We'll simply return the singleton instance from our newly created type resolver, leaving the rest default.

You can now try running the application! Right click _BatMass_ module suite in the Project Explorer and choose _Run_. It will build the application first and then run it. If you get tons of errors, you have likely not built the dependent projects. If so, locate the other 3 yellow modules (_BatMassExt_, _BatMassLibs_ and _MSFTBX_) in the Project Explorer, right click and _Build_ each of them. When _BatMass_ starts, create a new Proteomics Project, right click _LC/MS Features_ node in it, choose _Import LC/MS Features_. A file chooser should open and the File Types drop-down menu should contain an entry for the newly created data type.  
In this example case I mande a type resolver which recognized the files by their file-name suffix, specifically looking for files which end in "\_PeakCluster.csv". If you create a file with that name anywhere in your filesystem and select it, it should be added as a child of _LC/MS Features_ node now.  

Even though the file has been imported into the project, not much can be done with it yet. We have neither exposed any system for parsing the file into meaningful data structures nor declared how the file can be viewed. However, if you right click the imported file, you'll see that the _View_ sub-menu contains two actions: _Table_ and _Outline_. How did that happen?  

Those two entries are _Actions_ and _Actions_ are registered for nodes in the project based on **fileCategory** and **fileType** which we used for the type resolver. They are registered using annotations as well. If you go back to `UmpireTypeResolver` you'll see that:

```java
// Here's the registration annotation
@FileTypeResolverRegistration(
        fileCategory = UmpireTypeResolver.CATEGORY,
        fileType = UmpireTypeResolver.TYPE
)

// And here are the corresponding variables
public static final String CATEGORY = "features";
public static final String TYPE = "umpire-se";
```

So now you see that we have registered our new file-type under category "features" with type "umpire-se". It so happens that some other module in _BatMass_ has already registered an two actions for the "features" category, that's why we see those two available. If you read the last sentence carefully, you've probably noticed that I said the actions were registered for the **category**, however with the type-system in _BatMass_ you can register actions for categories, for concrete file-types or even for categories only for certain types of projects. The targets of action registration only depend on the path in the layer where they're registered.

You'll also notice that the _View_ actions are greyed-out, that's because those actions expect the node to expose some resources, like file-parser or table-model-provider for tabular views. So far our node exposes nothing as we haven't implemented any capabilities.

## Adding capabilities to the node
So far our new node is pretty useless. We've seen those 2 actions appear, they are provided by a separate module `GUI (BatMass)` which contains everything GUI related as its name suggests. Take a look at the `umich.ms.batmass.gui.nodes.actions` package. Here live the actions that you can invoke from context menus of nodes in the Project Explorer. We're interested in class `OpenFeature2DTable` from `umich.ms.batmass.gui.nodes.actions.features` package.


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
            throw new IllegalArgumentException("File path for Umpire-SE file does not exist.");

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
