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

- Create a parser for the file
- Add recognition support for the new file type
- Add support for importing the file into a project
- Add basic support for viewing the data in tabular viewer
- Add support for overlay of data over Map2D
- Add the feature that will allow us to double click a row in the table and automatically zoom into the region of interest in Map2D.

The prerequisite for this tutorial is that you have the development environment set up. If you don't make sure to follow

// TODO: this is a work in progress
