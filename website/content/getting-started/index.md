---
weight: -10
date: 2016-04-14T00:10:00-04:00
title: Getting started
---

This guide will quickly step you through the installation, creation of a project and viewing mzML/mzXML files.

## Installation
Download the latest release for your platform from [GitHub](https://github.com/chhh/batmass/releases/latest).  

- [Windows installer](https://github.com/chhh/batmass/releases/download/v.0.04/batmass-windows.exe)
  - It is preferable to install BatMass to non-system locations (e.g. not into _Program Files_ or similar locations) as you might need Administrator privileges to modify the configuration file.

- [Linux installer](https://github.com/chhh/batmass/releases/download/v.0.04/batmass-linux.sh)

- [MacOS installer](https://github.com/chhh/batmass/releases/download/v.0.04/batmass-macosx.tgz)

- [Platform independent zip](https://github.com/chhh/batmass/releases/download/v.0.04/batmass.zip)

{{< note title="Note" >}}
If you select the platform independent version, you'll have to launch _BatMass_ by manually running the correct executable for your platform from _&lt;extracted-archive-path&gt;/bin/_.
{{< /note >}}


## Configuration
_You can skip this section if you can launch the application and view the files without problems._

The only two things you might want to configure are the maximum amount of memory, that _BatMass_ will be allowed to use and the path to JRE (Java Runtime Environment). The default amount of RAM is set to 4GB, if your machine doesn't have that much, the JVM won't start.  
The startup configuration is stored in `<batmass-install-path>/etc/batmass.conf`.

- Max memory is set up in `default_options` option, the flag is `-J-Xmx`. By default after installation you'll find that it is set to `-J-Xmx4G`
- You can set any standard JVM startup options using this `default_options` line, just prepend the arguments with `-J-`. E.g. `-J-DisableExplicitGC`, however you likely won't need that.
- If you get a message that the JRE can't be found, try explicitly specifying the full path using the `jdkhome` option of `batmass.conf`. Even though the name says `jdk`, it's actually the path to the JRE. The line with that option is commented out by default, so remove the `#` symbol at the beginning of the line and set the path, e.g. `jdkhome="C:\Program Files\Java\jre1.8.0_77"`

{{< note title="Note to Windows users" >}}
If you've isntalled BatMass into some system folder (most commonly _C:\Program Files\batmass_), you might need to run text editor as Administrator in order to edit the config file, otherwise you likely won't be able to save changes to _batmass.conf_.
{{< /note >}}


## Creating a project
Go to `Main menu -> New project`, select any project type when presented with a choice, it won't matter now. Choose an existing directory or create a new empty one for the project, all the project-related files will be created inside this directory and ***not*** _a new directory with the project name under the selected directory_.

The project should now appear in the _Project Explorer_ tab on the left. Expand the project's node and right click `LC/MS Files` node, choose `Import LC/MS Files`. You can select multiple files at once by holding <kbd>Shift</kbd> or <kbd>Ctrl</kbd>, you can also restrict the files that are being shown to a particular format using the drop-down menu on the bottom of the file-chooser.

{{< note title="Memory usage" >}}
For opening large LC/MS runs in Map 2D viewer BatMass requires enough memory to load the whole MS level in memory (MS<sup>1</sup> or MS<sup>2</sup> or a single 'swath'/'window' of MS<sup>2</sup> in case of DIA), the default configuration file is set to use 4GB of RAM maximum. This has two implications:

 - If your machine does not have enough available RAM you might not be able to run the program at all. E.g. MS<sup>1</sup> only run of 10GB on a machine with 4GB RAM (You should still be able to view spectra though).
 - To open larger experiments (multi-gigabyte mzML/mzXML files) you might want to set larger memory limit in the configuration file discussed above.
{{< /note >}}


## Viewer controls
Open one of the files in the 2D viewer by right clicking and `View -> 2D Map` as shown below.  

![Open a file as a 2D Map](/images/getting-started/view-file.png)  

File parsing might take some time, when it's done you should see something akin to the following.  

![Map 2D viewer](/images/getting-started/standard-lcms-run-map2d.png)  

The image can be zoomed and panned using the mouse and keyboard.

- **Drag to zoom**. Press the left mouse button (`LMB`) and drag.
- **Zoom in/out with mouse wheel**. Using the mouse wheel with <kbd>Shift</kbd> or <kbd>Alt</kbd> modifiers will restrict zooming to only m/z or RT direction (the mnemonic rule is that the <kbd>Shift</kbd> key is streched out horizontally, so it corresponds to m/z direction, which is horizontal in charts).
- **Go To Dialog**. If you are interested in a particular location, you can use this dialog to type in the coordinates. While a chart has focus, press <kbd>Ctrl</kbd>+<kbd>G</kbd> or click the magnifying glass icon (![Link icon](/images/getting-started/icon_zoom.png)).
- **Panning**. Hold <kbd>Ctrl</kbd> while dragging the mouse to pan the view.
- **Restricted panning**. Using the same modifier keys (<kbd>Shift</kbd> or <kbd>Alt</kbd>) panning can be restricted to only one direction.
- **Zoom out to original full overview**. <kbd>Ctrl+Alt+Shift</kbd>+`LMB click` will un-zoom the image completely, bringing you to the view of the whole experiment.
- If you accidentally zoom to wrong location use the `Undo` button in the main toolbar.
- If you are viewing DIA (Data Independent Acquisition) data, the toolbar of the 2D viewer should have drop-down menus for MS level and precursor windows in case of MS<sup>2</sup>.


## Linking viewers
Multiple viewers can be linked together by dragging and dropping the link icon (![Link icon](/images/getting-started/icon_link.png)) from the viewer toolbar to another viewer's link icon. You can do this for multiple viewers of different types to assemble thme into groups. Clicking on the link icon will highlight the other viewer windows which are currently in in the same linked group, i.e. all synchronized, their borders will be highlighted by the same color. By clicking the unlink button in the viewer's toolbar (![Link icon](/images/getting-started/icon_unlink.png)), you can remove viewers from the group.
