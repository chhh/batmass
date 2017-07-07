---
weight: 30
title: "MSFTBX: Mass-Spec File Toolbox, the data access library"
---

The data access library is at the core of _BatMass_, but is a separate standalone project. It can be used in regular Java programs (read as a _jar_ library) and NetBeans platform applications via the included NetBeans Module wrapper.

## Features
- Single API to __mzML__ and __mzXML__ files
- __mzML__ and __mzXML__ parsing
  - Very fast multi-threaded parser
  - Can separately parse LC/MS run information, the index and data
  - Separation of parsing of scan meta-information and spectral data
  - Automatic indexing of the data
      - maps from scan numbers to scans
      - maps from retention time to scans
      - same maps separately at each MS level
      - automatic DIA (data Independent Acquisition) detection and automated grouping of DIA MS2 scans according to the corresponding isolation windows
  - Memory management
      - can parse the whole structure of the run (all scans with all meta-info) and dynamically parse spectral data from the disk only when it's accessed
      - an object can be used as the 'owner' of loaded data, if the 'owner' is garbage collected, and no other 'owners' claimed the scans, the corresponding resources can be automatically released
  - Tolerance to broken index
      - automatically detects errors in the index, such as all scan offsets are the same (which happens with some versions of ProteoWizard's _msconvert_ when converting large files)
      - if the index is not present, will reindex the file
  - Tolerance to MS2 scan tags being enclosed in the corresponding MS1 scan tag (old data converted with ReAdW)
- __PepXML__ parsing/writing
- __ProtXML__ parsing/writing
- __MzIdentML__ parsing/writing

## Usage
Take a look at [this tutorial]({{< ref "tutorial/data-access-layer.md" >}}) for a short introduction and check the [sources at github](https://github.com/chhh/msftbx).
