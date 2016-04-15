---
weight: -30
date: 2016-04-14T00:33:00-04:00
title: Developing the first plugin
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

## Installation
Download the latest release for your platform from [GitHub](https://github.com/chhh/batmass/releases/latest).  

- [Windows installer](https://github.com/chhh/batmass/releases/download/v.0.04/batmass-windows.exe)

- [Linux installer](https://github.com/chhh/batmass/releases/download/v.0.04/batmass-linux.sh)

- [MacOS installer](https://github.com/chhh/batmass/releases/download/v.0.04/batmass-macosx.tgz)

- [Platform independent zip](https://github.com/chhh/batmass/releases/download/v.0.04/batmass.zip)

{{< note title="Note" >}}
If you select the platform independent version, you'll have to launch _BatMass_ by manually running the correct executable for your platform from _&lt;extracted-archive-path&gt;/bin/_.
{{< /note >}}

```java
public static void main(String[] args) {
  System.out.println("Hello world!");
}
```

## Configuration
The only two things you might want to configure are the maximum amount of memory, that _BatMass_ will be allowed to use and the path to JRE (Java Runtime Environment). The default amount of RAM is set to 4GB, if your machine doesn't have that much, the JVM won't start.  
The startup configuration is stored in `<batmass-install-path>/etc/batmass.conf`.

- Max memory is set up in `default_options` option, the flag is `-J-Xmx`. By default after installation you'll find that it is set to `-J-Xmx4G`
- You can set any standard JVM startup options using this `default_options` line, just prepend the arguments with `-J-`. E.g. `-J-DisableExplicitGC`, however you likely won't need that.
- If you get a message that the JRE can't be found, try explicitly specifying the full path using the `jdkhome` option of `batmass.conf`. Even though the name says `jdk`, it's actually the path to the JRE. The line with that option is commented out by default, so remove the `#` symbol at the beginning of the line and set the path, e.g. `jdkhome="C:\Program Files\Java\jre1.8.0_77"`

## Creating a project
Go to `Main menu -> New project`, select any project type when presented with a choice, it won't matter now. Choose an existing directory or create a new empty one for the project, all the project-related files will be created inside this directory and ***not*** _a new directory with the project name under the selected directory_.

The project should now appear in the _Project Explorer_ tab on the left. Expand the project's node and right click `LC/MS Files` node, choose `Import LC/MS Files`. You can select multiple files at once by holding <kbd>Shift</kbd> or <kbd>Ctrl</kbd>, you can also restrict the files that are being shown to a particular format using the drop-down menu on the bottom of the file-chooser.

## Viewer controls
Main controls for...

## Linking viewers
Drag and drop...
