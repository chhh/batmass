# BatMass

Please visit http://batmass.org for more information.  

Watch the small introductory video on youtube:  
[![BatMass introductory video on YouTube](http://img.youtube.com/vi/EY9wvd6ckb0/1.jpg)](http://www.youtube.com/watch?v=EY9wvd6ckb0 "BatMass intro")


## Important before you begin
The only 2 parameters that you mgiht want to change after installing/unzipping is the amount of memory allowed to be used by BatMass and the path to JRE (Java Runtime Environment) to be used. The minimum supported version is Java 7, which is also the recommended one.<br/>
These parameters are stored in `<install_path>/etc/batmass.conf`<br/>
- To change the memory limit, change `default_options` parameter, look for `-J-Xmx` in its value string. The default is `-J-Xmx4G`, which means 4Gb max will be allowed and if your machine does not have enough free memory, the JVM (Java Virtual Machine) might fail to start.<br/>
- If you can't start the application and you are sure you have enough memory and a valid JRE installation, then try uncommenting `jdkhome` in batmass.conf. Even though named 'jdk..' it's actually the path to your JRE. E.g. use `jdkhome="C:\Program Files\Java\jre1.8.0_31"` on Windows.
- **Windows users**, if you've isntalled BatMass on your system drive in the default folder (most commonly `C:\Program Files\batmass`), you might need to run text editor as administrator, otherwise you won't be able to save changes made to `batmass.conf`.



## Mass spectrometry data visualization tools
BatMass is a mass-spectrometry data visualization tool, with the main focus on being fast and interactive while providing comprehensive visualizations without any parameter tweaking. It is written in pure Java and built on top of the [NetBeans Platform](https://netbeans.org/features/platform/all-docs.html).


## Features
- Support for the open standard _mzML_ and _mzXML_ mass spectrometry data types. We are hoping to bring native vendor format support as well.

- Viewer synchronization. Link any number of viewers and zooming/panning will be synchronized across them. If you're viewing MS<sup>1</sup> data in one view and MS<sup>2</sup> data in the other the retention time is synchronized, while m/z is not. Open a detected LC/MS feature table or a peptide identification table, a double click on the row will open the corresponding spectrum, or bring you to the corresponding location in a 2D Map viewer.

- Data access layer. For the Java developers out there, the highly optimized mzML/mzXML parsers can be used in any standalone Java program as a simple _jar_ dependency. Parsing has been manually tuned to produce few garbage objects, thus minimizing time spent in GC (Garbage Collection), the speed is comparable to or better than in C/C++ implementations. The API for LC/MS data files gives access to most of the features supported by mzML/mzXML standards.



## Installation
Download an installer for your system [here](https://github.com/chhh/batmass/releases/latest).  
If you choose to use the platform independent _zip_ file (batmass.zip), you'll need to run the launcher for your system in `<install_path>/bin`, e.g. `<install_path>/bin/batmass64.exe` if you're running a 64 bit version of Windows. For linux-based systems there is `<install_path>/bin/batmass` shell script to start BatMass.  

If you have an earlier version of BatMass installed, you have to manually uninstall it.  

**For more detailed instructions see the [getting started guide](http://www.batmass.org/getting-started/).**
