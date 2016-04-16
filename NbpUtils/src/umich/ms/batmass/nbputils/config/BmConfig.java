/* 
 * Copyright 2016 Dmitry Avtonomov.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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