---
weight: 55
title: Setting up development environment
summary: "This guide will quickly step you through setting up the environment for developing new functionality for BatMass. All the downloads, setting up the IDE and up to building BatMass from scratch."
menu:
  main:
    parent: Tutorials
    identifier: "Setting up development environment"
---

This guide will quickly step you through setting up the environment for developing new functionality for BatMass. All the downloads, setting up the IDE and up to building BatMass from scratch.

## Setting up development environement for BatMass
You will only need this information if you want to develop your own plugins
for BatMass or change its functionality in some way, if you're just a regular
user you can skip this tutorial.

Don't be scared by the size of this tutorial, it just assumes that you know
nothing about NetBeans and the NetBeans Platform. All the steps are simple.


## Get the JDK (Java Development Kit)
BatMass is written in Java, so we'll need Java. Download and isntall JDK 7:
[e.g. from Oracle](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html).
It should work with _OpenJDK_ as well.

## The NetBeans platform
BatMass is built on top of the NetBeans platform (we will call it just __the
platform__ or __NBP__ for simplicity). It's the same infrastructure  that the NetBeans IDE
is built on top of itself. This is an essential part of the setup, it is the
platform that provides the Window system, the Loader, Automatic Updates and much more.
All platform applications are developed against some version of the platform,
which itself is just a collection of libraries (called _Modules_). Though it is
possible to use the platform that comes with the NetBeans IDE, you'll likely
encounter multiple errors due to incorrect dependency versions, so __you'll need
to get the platform version that we are providing__.  

Developing platform applications is much simpler with the NetBeans IDE as it
provides a lot of wizards and other core functionality. It is possible to use
_ItelliJ_ as well, but we will not be covering this option, so [go ahead and get
the latest version of NetBeans](https://netbeans.org/downloads/) (which is 8.1
as of this writing). You can select the _Java SE_ version among all downloads.

* Download the platform files: [from BatMass github repository](https://github.com/chhh/batmass/releases/download/v.0.1.0/nb81-batmass.zip).
* Unpack the downloaded archive, it contains four zip files
* Unpack the following 3 files to the same location, e.g. `/<path>/nbp`:
  * `netbeans-...-harness.zip`
  * `netbeans-...-ide.zip`
  * `netbeans-...-platform.zip`
* Do not unpack `netbeans-8.1-...-platform-src.zip`
* You should now have `/<path>/nbp/harness`, `/<path>/nbp/ide`,
`/<path>/nbp/platform`
* Start the NetBeans IDE.
* In the main menu: _Tools -> NetBeans Platforms_.
* Click _Add Platform_ and select `/<path>/nbp`, the _Platform Name_ text box
on the right will show something like _nb81_, which means that the directory
has been recognized as containing _platform_ files.
* Click _Next_, change the platform name to `nb-batmass` (__This is very
  important!__) as it is and click _Finish_.
* You should now see the newly created _platform_ in the list, like this:  
![NetBeans platform added](/images/setting-up-development-environment/nb-platform-created.png)
* Change the tab to _Harness_ and make sure to switch the radio button to
_Harness supplied with Platform_.
* Change the tab to _Sources_ and add the path to _.zip_ file
`netbeans-*-platform-src.zip` that came with your platform download.

You should now have the environment set-up to bebing development.

## BatMass modules

BatMass itself is composed of multiple modules which are split into several
module suits. A module suite in the platform terms is just a collection of
modules groupped together all of which have access to some common set of
dependencies. Each suite lives in its own github repository, clone them all to
the to the same location:

* [BatMass](https://github.com/chhh/batmass) - the main suite, most of
development happens here

* [BatMassLibs](https://github.com/chhh/batmass-libs) - all the library
dependencies reside in this suite. In

* [BatMassExternalSuite](https://github.com/chhh/batmass-ext) - contains
separate functional pieces borrowed from the web or elsewhere. E.g. the color-
picker used in the options lives here.

* [MSFTBX](https://github.com/chhh/MSFTBX) - this is the data access library.
It provides pure-java implementations of _mzXML_, _mzML_, _pep.xml_, _prot.xml_
files among some others as well. The API (interfaces) is bundled with it.  
Although this repository contains a NetBeans platform module project, you'll
also notice that it contains a regular IntelliJ IDEA project as well, which
has artifacts configured to build a regular _jar_ out of it.

```bash
mkdir /<some-path>/batmass-projects
cd /<some-path>/batmass-projects
git clone https://github.com/chhh/batmass.git
git clone https://github.com/chhh/batmass-libs.git
git clone https://github.com/chhh/batmass-ext.git
git clone https://github.com/chhh/MSFTBX.git
```
You now have all the source code and libraries necessary to build BatMass.

## Building the projects
* Start NetBeans IDE
* `Main Menu -> File -> Open Project`
* Holding down <kbd>Ctrl</kbd> select the 4 cloned projects, and open them (see
image below)
![Open Cloned Projects](/images/setting-up-development-environment/netbeans-opening-projects.png)  
* Check the bottom right corner of the IDE for a progress bar, it might take a
while to index the projects.
* Right click `BatMass` project, select `Properties` in the context menu.
* Select `Libraries` in the _Categories_ list on the left.
* Make sure that drop-down _NetBeans Platform_ is set to `nb-batmass` as in
the image below, if this shows some other platform, switch to `nb-batmass`  
![Make sure opened projects are set to use the provided platform](/images/setting-up-development-environment/netbeans-project-batmass-check-library-settings.png)
  * If you see some errors in the _Project Properties_ window (e.g. saying that
    some module lacks some dependencies, and the _Resolve_ button is inactive),
    please contact us.

* Check the selected platform for the other 3 projects as well.

## We are ready to build the final product!
The build order is of importance. Automatic rebuilding of dependent module suits
was not set up to improve build performance.  
Only the `BatMass` project depends on others, so whenever you make changes to
`BatMassLibs`, `BatMassExternalSuite` or `MSFTBX` you will need to manually
trigger a `clean build` on them, and rebuild the `BatMass` project  after that.

* Right click `MSFTBX` project node in the _Project Explorer_ and choose `clean
build`
* Now do `clean build` for `BatMassLibs` and `BatMassExternalSuite`
* You can now `clean build` the main `BatMass` project
* When the build is done, go to `Main menu -> Run -> Set main project` and
select `BatMass` there. This will force the green `Run` button (<kbd>F6</kbd>
shortcurt) to always launch this project and not the currently selected one.
* Run the project: `Main menu -> Run -> Run main project`

If all is well and you can start the project, you should also be able to
create the installers for it. Right click `BatMass` project, `Package as ->
Installers`. This is a time-consuming process, when done, you'll find the
installers for different platforms in `batmass-projects/batmass/dist`.
