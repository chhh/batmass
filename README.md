# BatMass

**Please visit http://batmass.org for more information.**  


## A small introductory video
[![BatMass introductory video on YouTube](http://img.youtube.com/vi/EY9wvd6ckb0/1.jpg)](http://www.youtube.com/watch?v=EY9wvd6ckb0 "BatMass intro")  
Check out http://batmass.org for more info.


## Mass spectrometry data visualization tools
BatMass is a mass-spectrometry data visualization tool, with the main focus on being fast and interactive while providing comprehensive visualizations without any parameter tweaking. It is written in pure Java and built on top of the [NetBeans Platform](https://netbeans.org/features/platform/all-docs.html).


## Citing
Please cite the following paper if you used [BatMass](https://github.com/chhh/batmass) or [MSFTBX](https://github.com/chhh/msftbx) (data access library) in your work:  
[Avtonomov D.M. et al: J. Proteome Res. June 16, 2016. DOI: 10.1021/acs.jproteome.6b00021](https://dx.doi.org/10.1021/acs.jproteome.6b00021)


## Java compatibility
It looks like BatMass can't be run on java versions after 8. This is a problem of all NetBeans platform applications, not specific to BatMass.  
**On Linux**, a tried solution is to install JDK 8 and to set java 8 as the java of choice. E.g. on Ubuntu:   
- `sudo apt install openjdk-8-jdk`
- `sudo update-alternatives --config java` and select `java 8` manually (if possible)
- Modify `<batmass-install-path>/etc/batmass.conf` file. Uncomment and change `jdkhome` variable to something like:  
`jdkhome="/usr/lib/jvm/java-1.8.0-openjdk-amd64/"`


# Important before you begin (Troubleshooting)
The only 2 parameters that you mgiht want to change after installing/unzipping is the amount of memory allowed to be used by BatMass and the path to JRE (Java Runtime Environment) to be used.  
The startup parameters are stored in `<install_path>/etc/batmass.conf`  
- To change the memory limit, change `default_options` parameter, look for `-J-Xmx` in its value string. The default is `-J-Xmx4G`, which means 4Gb max will be allowed and if your machine does not have enough free memory, the JVM (Java Virtual Machine) might fail to start.<br/>
- If you can't start the application and you are sure you have enough memory and a valid JRE/JDK installation, uncomment variable `jdkhome` in `batmass.conf`. Even though it's name starts with `jdk` it's actually just a path where `./bin/java` binary can be found. E.g. use `jdkhome="C:\Program Files\Java\jre1.8.0_31"` on Windows, or `jdkhome="/usr/lib/jvm/java-1.8.0-openjdk-amd64/"` on Linux.
- **Windows users**, if you've isntalled BatMass on your system drive in the default folder (most commonly `C:\Program Files\batmass`), you might need to run text editor as administrator, otherwise you won't be able to save changes made to `batmass.conf`.


## Features
- Support for the open standard _mzML_ and _mzXML_ mass spectrometry data types. We are hoping to bring native vendor format support as well.

- Viewer synchronization. Link any number of viewers and zooming/panning will be synchronized across them. If you're viewing MS<sup>1</sup> data in one view and MS<sup>2</sup> data in the other the retention time is synchronized, while m/z is not. Open a detected LC/MS feature table or a peptide identification table, a double click on the row will open the corresponding spectrum, or bring you to the corresponding location in a 2D Map viewer.

- Data access layer. For the Java developers out there, the highly optimized mzML/mzXML parsers can be used in any standalone Java program as a simple _jar_ dependency. Parsing has been manually tuned to produce few garbage objects, thus minimizing time spent in GC (Garbage Collection), the speed is comparable to or better than in C/C++ implementations. The API for LC/MS data files gives access to most of the features supported by mzML/mzXML standards.


## Installation
Download an installer for your system [here](https://github.com/chhh/batmass/releases/latest).  
If you choose to use the platform independent _zip_ file (batmass.zip), you'll need to run the launcher for your system in `<install_path>/bin`, e.g. `<install_path>/bin/batmass64.exe` if you're running a 64 bit version of Windows. For linux-based systems there is `<install_path>/bin/batmass` shell script to start BatMass.  

If you have an earlier version of BatMass installed, you have to manually uninstall it.  

**For more detailed instructions see the [getting started guide](http://www.batmass.org/getting-started/).**

[![Analytics](https://ga-beacon-nocache.appspot.com/UA-5572974-15/github/chhh/batmass/landing-page?flat&useReferer)](https://github.com/igrigorik/ga-beacon)

