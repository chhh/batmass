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
package umich.ms.batmass.projects.core.type;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.ImageIcon;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.spi.project.ProjectFactory2;
import org.netbeans.spi.project.ProjectState;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

/**
 * Subtypes of this class must be registered with:
 *
 * @ServiceProvider(service=ProjectFactory.class) A generic base class for all
 * project types that use some MS files as their base, e.g. metabolomics,
 * proteomics, etc all use mzXML/RAW/mzML... files as the main input source.
 * Those project types should extend this one, providing some project-specific
 * actions.
 * @author Dmitry Avtonomov
 */
public abstract class BMProjectFactory implements ProjectFactory2 {

    public abstract ImageIcon getIcon();

    public abstract String getProjectDir();

    public abstract String getProjectPropfile();

    public abstract Class<? extends BMProject> getProjectClass();

    public abstract String getProjectTypeDescription();

    public abstract String getProjectTypeName();

    public void createProjectDirStructure(Path path, String name) throws IOException, ConfigurationException {
        Path projDirPath = Paths.get(path.toAbsolutePath().toString(), getProjectDir());
        Files.createDirectories(projDirPath);
        Path projPropFile = Paths.get(projDirPath.toString(), getProjectPropfile());
        Files.createFile(projPropFile);

        PropertiesConfiguration config = new PropertiesConfiguration(projPropFile.toFile());
        config.addProperty(BMProject.PROP_NAME, name);
        config.save();
    }

    /**
     * This method needs to be very fast—it should determine whether or not a
     * directory is a project as quickly as possible, because it will be called
     * once for each directory shown in the file chooser
     *
     * @param projectDirectory
     * @return
     */
    @Override
    public ProjectManager.Result isProject2(FileObject projectDirectory) {
        FileObject projFo = projectDirectory.getFileObject(getProjectDir());
        if ( projFo != null) {
            FileObject projPropFo = projFo.getFileObject(getProjectPropfile());
            if (projPropFo != null)
                return new ProjectManager.Result(getIcon());
        }
        return null;
    }

    /**
     * This method needs to be very fast—it should determine whether or not a
     * directory is a project as quickly as possible, because it will be called
     * once for each directory shown in the file chooser
     *
     * @param projectDirectory
     * @return
     */
    @Override
    public boolean isProject(FileObject projectDirectory) {
        FileObject projFo = projectDirectory.getFileObject(getProjectDir());
        if ( projFo != null) {
            FileObject projPropFo = projFo.getFileObject(getProjectPropfile());
            if (projPropFo != null)
                return true;
        }
        return false;
    }

    /**
     * Loads a project, given a directory. The project system handles caching of
     * projects, so all that's needed here is to create a new project.
     *
     * @param projectDirectory
     * @param projectState
     * @return
     * @throws IOException
     */
    @Override
    @SuppressWarnings("unchecked")
    public Project loadProject(FileObject projectDirectory, ProjectState projectState) throws IOException {
        if (isProject(projectDirectory)) {
            try {
                Class<? extends BMProject> projectClass = getProjectClass();
                Constructor<BMProject> c = (Constructor<BMProject>)projectClass
                        .getConstructor(FileObject.class, ProjectState.class);
                BMProject proj = projectClass.cast(c.newInstance(projectDirectory, projectState));
                return proj;
            } catch (NoSuchMethodException ex) {
                Exceptions.printStackTrace(ex);
            } catch (InstantiationException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IllegalAccessException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IllegalArgumentException ex) {
                Exceptions.printStackTrace(ex);
            } catch (InvocationTargetException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return null;
    }

    /**
     * This is what will write out any unsaved changes to disk when a
     * LCMSProject project is closed, or when the application shuts down.
     *
     * @param project
     * @throws IOException
     * @throws ClassCastException
     */
    @Override
    public void saveProject(Project project) throws IOException, ClassCastException {
        FileObject projectRoot = project.getProjectDirectory();
        if (projectRoot.getFileObject(getProjectDir()) == null) {
            throw new IOException(
                    "Project dir (" + projectRoot.getPath() + ") deleted, cannot save project.");
        }
        BMProject bmp = project.getLookup().lookup(BMProject.class);

        FileObject propertiesFile = bmp.getProjectPropertiesFile();
        if (propertiesFile == null) {
            //Recreate the properties file if needed
            propertiesFile = projectRoot.createData(bmp.getProjectPropsFileName());
        }

        PropertiesConfiguration properties = project.getLookup().lookup(PropertiesConfiguration.class);
        File f = FileUtil.toFile(propertiesFile);
//        properties.store(new FileOutputStream(f), "Project Properties");
    }

}
