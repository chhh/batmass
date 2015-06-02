# BatMass
### Mass spectrometry data visualization tools

It was originally created to provide an extensible platform with basic functionality, like project management, raw mass-spectrometry data access, various GUI widgets and extension points. E.g. project system, option panels, a system for easy addition of support for new file types, actions for those files, memory management for viewers, etc.

More specifically, the original goal was to be able to visualize results of LC/MS feature-finding algorithms (a feature in this context is an isotopic cluster as it elutes from the chromatographic column over time).

### What can we offer so far

As there is no paper yet, we can not release the full source code, however, the code will be out as soon as the paper lands. The software is in alpha stage.

The binaries are released in the hopes that it might help you take a fresh look at the raw data from your experiments.

### How to start

Check out the releases section, download the isntaller for your system.
You can also download the zip file, which does not require any installation.

The only 2 parameters that you mgiht want to change after installing/unzipping is the amount of memory allowed to be used by BatMass and the path to JRE (Java Runtime Environment) to be used. The minimum supported version is Java 7, which is also the recommended one.<br/>
These parameters are stored in `<install_path>/etc/batmass.conf`<br/>
- To change the memory limit, change `default_options` parameter, look for `-J-Xmx` in its value string. The default is `-J-Xmx4G`, which means 4Gb max will be allowed and if your machine does not have enough free memory, the JVM (Java Virtual Machine) might fail to start.<br/>
- If you can't start the application and you are sure you have enough memory and a valid JRE installation, then try uncommenting `jdkhome` in batmass.conf. Even though named 'jdk..' it's actually the path to your JRE.
