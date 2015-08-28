/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.nbputils.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration. ConfigurationException;
import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import umich.ms.batmass.nbputils.PathUtils;
/**
 * THIS IS AN XML BASED CONFIG, IT SHOULD WORK IN BATMASS, BUT NOT IN msgui
 * @author dmitriya
 */
public class BmConfig {
//    private static final String EXT = ".config.xml";
    private static final String EXT = ".config.properties";
    private static final String CONF_DIR = "BmConfig";

    public static FileConfiguration forClass(Class<?> clazz) throws ConfigurationException, IOException {
//         return new XMLConfiguration(getConfigFile(clazz));
         return new PropertiesConfiguration(getConfigFile(clazz));
    }

    private static String constructConfigFilePath(Class<?> clazz) {
        return Paths.get(PathUtils.getNbUserDir(), CONF_DIR, 
                PathUtils.getPackageAsPath(clazz), clazz.getSimpleName() + EXT)
                .toAbsolutePath().toString();
    }

    private static <T> void initNewConfiguration(Configuration conf, Class<T> clazz) {
        conf.addProperty("bm.config.forClass", clazz.getName());
    }

    private static synchronized File getConfigFile(Class<?> clazz) throws IOException, ConfigurationException {
        String filePath = constructConfigFilePath(clazz);
        Path path = Paths.get(filePath);

        // if there was an empty file by that name - it's an artifact from
        // some previous unsuccessful run, so we just delete it
        if (Files.exists(path) && path.toFile().length() == 0) {
            path.toFile().delete();
        }

        if (!Files.exists(path)) {
            Files.createDirectories(path.getParent());
            Path createdConfFile = Files.createFile(path);
//            XMLConfiguration conf = new XMLConfiguration();
            PropertiesConfiguration conf = new PropertiesConfiguration();
            initNewConfiguration(conf, clazz);
            conf.save(createdConfFile.toFile());
        }
        return path.toFile();
    }
}