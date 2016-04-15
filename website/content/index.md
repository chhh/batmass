---
weight: -1
date: 2016-04-14T00:00:01-04:00
title: "BatMass: mass spectrometry data visualization"
type: index
imageSrc: images/batmass-logo-300.png
imageClass: batmass-about-image
---


## About BatMass
BatMass is a mass-spectrometry data visualization tool, with the main focus on being fast and interactive while providing comprehensive visualizations without any parameter tweaking. It is written in pure Java and built on top of the [NetBeans Platform](https://netbeans.org/features/platform/all-docs.html).


## Features
- Support for the open standard _mzML_ and _mzXML_ mass spectrometry data types. We are hoping to bring native vendor format support as well.

- Viewer synchronization. Link any number of viewers and zooming/panning will be synchronized across them. If you're viewing MS<sup>1</sup> data in one view and MS<sup>2</sup> data in the other the retention time is synchronized, while m/z is not. Open a detected LC/MS feature table or a peptide identification table, a double click on the row will open the corresponding spectrum, or bring you to the corresponding location in a 2D Map viewer.

- Data access layer. For the Java developers out there, the highly optimized mzML/mzXML parsers can be used in any standalone Java program as a simple _jar_ dependency. Parsing has been manually tuned to produce few garbage objects, thus minimizing time spent in GC (Garbage Collection), the speed is comparable to or better than in C/C++ implementations. The API for LC/MS data files gives access to most of the features supported by mzML/mzXML standards.

See the [getting started guide]({{< relref "getting-started/index.md" >}}) for instructions how to get
it up and running.

## Contacts
**General inquiries**  
Alexey Nesvizhskii, Ph.D.  
University of Michigan, Ann Arbor  
http://www.nesvilab.org  
Email: nesvi@umich.edu   

**Technical questions**  
Dmitry Avtonomov  
University of Michigan, Ann Arbor  
Email: dmitriya@umich.edu  

Please use the [bug tracker](https://github.com/chhh/batmass/issues) to ask questions, submit feature requests and bug reports.
