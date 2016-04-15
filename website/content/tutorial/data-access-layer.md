---
weight: -20
date: 2016-04-14T00:32:00-04:00
title: Using data access library
summary: "The data access library provides a relatively rich API to mzML/mzXML files (MS level, polarity, precursor isolation window, instrument data, etc.) and a few other file formats common to the proteomics field, such as PepXML, ProtXML and MzIdentML. In this tutorial will step through parsing some data, using the library as a jar in a simple console window application."
menu:
  main:
    parent: Tutorials
    identifier: "Using data access library"
    weight: 20
---

In this guide we will quickly go through using the standalone java library for accessing some common mass spectrometry data formats. This is the same library that powers _BatMass_.

All the classes responsible for parsing files live in `umich.ms.fileio.filetypes` package, each in its own subpackage, e.g. `umich.ms.fileio.filetypes.pepxml` for PepXML files. Most of those sub-packages contain a separate package `example` with working examples.  

The source code for the library lives in [MSFTBX repository on GitHub](https://github.com/chhh/msftbx). Start by cloning:  
`git clone https://github.com/chhh/MSFTBX.git`
and explore

## Parsing LC/MS data (mzML/mzXML files)
Unfortunately, it's near impossible to easily access raw mass spec data from the original vendor file formats using java. You can convert most data from proprietary formats (_.RAW_ files for Thermo, _.d_ directories for Agilent, etc.) using `msconvert` program from [ProteoWizard]().

The API is separated into two parts. First you create a data source from your file. The data source can be used by itself, if you just want to iterate over spectra by yourself. It can also be attached to a special data structure, which handles data loading, management, indexing and garbage collection.

mzML and mzXML share the same common base interface `umich.ms.fileio.filetypes.LCMSDataSource`, you can use that if you want to write code that can work seamlessly with both file formats.




## Parsing identification files (PepXML, ProtXML, MzIdentML)
The library gives low level access to those file formats. There is no unifying API here, as the formats are very different. These parsers are not hand optimized for efficiency, so they might consume quite a bit more memory than they should, but they also are very error resilient.

## Parsing huge identification files more efficiently
Sometimes you might have PepXML files that are many gigabytes in size, this happens when you combine search results from multiple experiments and store them in a single output file. In that case using `XMLStreamReader` class it is possible to first rewind the input stream to some large structural element of the underlying file, such as `<msms_run_summary>` in PepXML files.  
You will need to have an idea of how the files are organized for this though, explore the corresponding XML schemas. The schemas can also be found in the sources of the library in file-specific sub-packages of `umich.ms.fileio.filetypes` in `resources` directories.
