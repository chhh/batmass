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
package umich.ms.batmass.gui.viewers.map2d.options;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.util.Exceptions;
import umich.ms.batmass.gui.viewers.map2d.components.BaseMap2D;
import umich.ms.batmass.nbputils.config.BmConfig;

/**
 * Singleton implementation of options for Map2D.
 * Lazy loading via static inner instance holder class.
 * @author dmitriya
 */
public class Map2DOptions {
    @StaticResource
    private final String DEFAULT_OPTIONS_FILE_PATH = "umich/ms/batmass/gui/viewers/map2d/options/BaseMap2D.default.properties";
    private Configuration defaultConfig;

    private Map2DOptions() {
        defaultConfig = readDefaultConfig();
        if (defaultConfig == null)
            defaultConfig = new PropertiesConfiguration();
    }

    public static Map2DOptions getInstance() {
        return Map2DOptionsHolder.INSTANCE;
    }

    private static class Map2DOptionsHolder {
        private static final Map2DOptions INSTANCE = new Map2DOptions();
    }

    /**
     * Just read the default config from a property file.
     *
     * @return null if failed reading default confgi from file. <br/>
     * Default factory config, you just get that and mix with your user
     * configuration using config.addConfiguration()
     */
    private Configuration readDefaultConfig() {
//        Configuration readConfig = new PropertiesConfiguration();
//        readConfig.addProperty("doUpscaling", true);
//        readConfig.addProperty("doBasePeakMode", true);
//        readConfig.addProperty("doProfileModeGapFilling", false);
//        readConfig.addProperty("doMzCloseZoomGapFilling", true);
//        readConfig.addProperty("colorLevels", 65536);
//        readConfig.addProperty("colorPivots", new String[]{"#000000", "#00007F", "#0000FF", "#007FFF", "#00FFFF", "#7FFF7F", "#FFFF00", "#FF7F00", "#FF0000", "#7F0000"});


        URL configUrl = this.getClass().getClassLoader().getResource(DEFAULT_OPTIONS_FILE_PATH);
        Configuration readConfig = null;
        try {
            readConfig = new PropertiesConfiguration(configUrl);
        } catch (ConfigurationException ex) {
            Exceptions.printStackTrace(ex);
        }


        return readConfig;
    }

    public Configuration flushUserConfigToDefault() {
        FileConfiguration userConfig = null;
        try {
            userConfig = BmConfig.forClass(BaseMap2D.class);
            userConfig.clear();
            userConfig.save();
            userConfig = BmConfig.forClass(BaseMap2D.class);
        } catch (ConfigurationException | IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        defaultConfig = readDefaultConfig();
        return userConfig;
    }

    /**
     * Returns the default config created when this class was loaded.
     * @return
     */
    public Configuration getDefaultConfig() {
        return defaultConfig;
    }

    /**
     * The combined config = current user config + defaults for non-specified properties.
     * TODO: instead of re-reading user config every time, it would be better
     *       to set up Commons Configuration listeners on the userfile.
     * @return even if reading user config fails, will return default config
     */
    public CompositeConfiguration getConfig() {

        // try loading user config
        FileConfiguration userConfig = null;
        try {
            userConfig = BmConfig.forClass(BaseMap2D.class);
        } catch (ConfigurationException | IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        CompositeConfiguration compositeConfig = new CompositeConfiguration();
        // the order of adding configurations to combined configuration is important
        // whoever is added first "shadows" the same key in all subsequently added
        // configuraions. Thus, add the DEFAULTs at the very end
        if (userConfig != null) {
            compositeConfig.addConfiguration(userConfig);
        }
        compositeConfig.addConfiguration(defaultConfig);

        return compositeConfig;
    }

    /**
     * Gets only the properties stored in user.dir in Netbeans App.
     * @return null if something went wrong during retrieving userconfig
     */
    public FileConfiguration getUserConfig() {
        try {
            return BmConfig.forClass(BaseMap2D.class);
        } catch (ConfigurationException | IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    /**
     * Converts HEX colors stored in an Apache Commons Configuration, e.g.:<br/>
     * <code>defaultConfig.addProperty("colorPivots", new String[]{"#000000", "#00007F", "#0000FF"});</code>
     * to {@link java.awt.Color}[].
     * @param config
     * @return
     */
    public static Color[] getColorsFromConfig(Configuration config) {
        String[] colorsArr = config.getStringArray("colorPivots");
        Color[] colors = new Color[colorsArr.length];
        for (int i = 0; i < colorsArr.length; i++) {
            colors[i] = Color.decode(colorsArr[i]);
        }
        return colors;
    }

    /**
     * Returns Color[] stored in this Configuration.
     * @return
     */
    public Color[] getColors() {
        return getColorsFromConfig(this.getConfig());
    }

}
