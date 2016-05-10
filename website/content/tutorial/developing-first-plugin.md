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

## Create a parser for the file
We will use DIA-Umpire Signal Extraction text output file as an example. It's a simple delimited text format with the first line being column headers, the rest being data stored as text.

###
