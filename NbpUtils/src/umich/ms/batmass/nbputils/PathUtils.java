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
package umich.ms.batmass.nbputils;

import org.openide.modules.Places;

/**
 * Container for utility methods for path manipulation.
 * Both file-system paths and layer paths.
 * @author Dmitry Avtonomov
 */
public abstract class PathUtils {

    private PathUtils() {
    }
    
    
    /**
     * Just replaces "/" with dots for this objects' class package.
     * No trailing slash added at the end.
     * @param o
     * @return
     */
    public static String getPackageAsPath(Object o) {
        return o.getClass().getPackage().getName().replace(".", "/");
    }

    /**
     * Just replaces "/" with dots for this objects' class package.
     * No trailing slash added at the end.
     * @param clazz
     * @return
     */
    public static String getPackageAsPath(Class<?> clazz) {
        return clazz.getPackage().getName().replace(".", "/");
    }
    

    /**
     * UserDir inside NetBeans running app, e.g.: G:/BatMass/build/testuserdir/<br/>
     * Not the same as system user directory!
     * @return absolute path WITHOUT a trailing slash
     */
    public static String getNbUserDir() {
        // This is the wrong one, it returns something like: G:/BatMass/DBManagerModule/
        //String userDirPath = System.getProperty("user.dir");
        // This is the correct one, e.g.: G:/BatMass/build/testuserdir/
        // This method has been deprecated.
        //String userDirPath = System.getProperty("netbeans.user");
     
        return Places.getUserDirectory().getAbsolutePath();
    }
    
    /**
     * Simply concatenates name with extension using dot.
     * @param name filename
     * @param ext new extension to add to the file
     * @return 
     */
    public static String filename(String name, String ext) {
        return name + "." + ext;
    }
}
