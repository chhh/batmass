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
package umich.ms.batmass.projects.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.netbeans.api.project.Project;
import org.openide.util.Exceptions;
import umich.ms.batmass.projects.core.annotations.BMProjectSubfolderType;
import umich.ms.batmass.projects.core.annotations.BMProjectType;
import umich.ms.batmass.projects.core.nodes.spi.BMNodeFactory;
import umich.ms.batmass.projects.core.services.spi.LayerMappedPathProvider;
import umich.ms.batmass.projects.core.services.spi.LayerPathProvider;
import umich.ms.batmass.projects.core.type.BMProject;

/**
 *
 * @author Dmitry Avtonomov
 */
public abstract class BMProjectUtils {
    private BMProjectUtils() {};

    /**
     * Read the project type from annotations.
     * @param p
     * @return
     */
    public static String getProjectType(Class<? extends BMProject> p) {
        if (p.isAnnotationPresent(BMProjectType.class)) {
            BMProjectType type = p.getAnnotation(BMProjectType.class);
            return type.projectType();
        }
        // A BMProject was not annotated, this should not happen.
        String msg = "You've extended BMProject (" + p.getCanonicalName() +
                ") and did not annotate it with @BMProjectType annotation.\n"
                + "Your project will for now be assigned type BMProject.TYPE_ANY,\n"
                + "but you must fix this.";
        Exceptions.printStackTrace(new IllegalStateException(msg));
        return BMProject.TYPE_ANY;
    }

    /**
     * Utility method for retrieving sub-folder type from it's annotation.
     * NodeFactory must be annotated with {@literal @}BMProjectSubfolderType annotation.
     * @param nodeFactory
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static String getSubfolderType(Class<? extends BMNodeFactory> nodeFactory) {
        if (nodeFactory.isAnnotationPresent(BMProjectSubfolderType.class)) {
            BMProjectSubfolderType type = nodeFactory.getAnnotation(BMProjectSubfolderType.class);
            return type.type();
        }
        // A BMNodeFactory was not annotated, this should not happen.
        String msg = "You've extended BMNodeFactory (" + nodeFactory.getCanonicalName() +
                ") and did not annotate it with @BMProjectSubfolderType annotation.\n"
                + "Your project will for now be assigned type BMNodeFactory.TYPE_ANY,\n"
                + "but you must fix this.";
        Exceptions.printStackTrace(new IllegalStateException(msg));
        return BMNodeFactory.TYPE_ANY;
    }

    /**
     * Get the unique id representing any project type.
     * @return
     */
    public static String getProjectAnyType() {
        return BMProject.TYPE_ANY;
    }

    /**
     *
     * @param pathProviders typically you'd get this from Project's lookup like:<br/>
     *    {@code project.getLookup().lookupAll(SomeSpecificPathProvider.class) }<br/>
     * Can be {@link LayerMappedPathProvider}.
     * @param otherPaths can be null. Other paths to be added to the result.
     * These paths will be added to the end of the list, in the same order they
     * appear in the array.
     * @return
     */
    public static String[] aggregateLayerPaths(
            Collection<? extends LayerPathProvider> pathProviders,
            String[] otherPaths) {
        ArrayList<String> list = new ArrayList<>();

        for (LayerPathProvider pathProvider : pathProviders) {
            list.addAll(Arrays.asList(pathProvider.getPaths()));
        }
        if (otherPaths != null) {
            list.addAll(Arrays.asList(otherPaths));
        }

        return list.toArray(new String[list.size()]);
    }

    /**
     * This is a pretty convoluted method. It will filter path providers, leaving
     * only those which map to a specific type or its subtypes ({@code filter} parameter).
     * @param pathProviders typically you'd get this from Project's lookup like:<br/>
     *    {@code project.getLookup().lookupAll(SomeSpecificPathProvider.class)}.<br/>
     * Or just lookup all LayerMappedPathProviders and provide your specific mapping type, but then you
     * won't be able to differentiate between path providers intended for  e.g. actions paths
     * extension and other types of providers.
     * @param filter the class a {@link LayerMappedPathProvider} should be mapping to.
     * @param otherPaths other paths to be added, these must be in order, they will be added to the end of the list
     *
     * @return
     */
    public static String[] aggregateLayerMappedPaths(
            Collection<? extends LayerMappedPathProvider> pathProviders,
            Class<?> filter,
            String[] otherPaths) {
        
        if (pathProviders.isEmpty())
            return otherPaths;

        ArrayList<LayerMappedPathProvider> capable = new ArrayList<>(pathProviders.size());
        for (LayerMappedPathProvider pathProvider : pathProviders) {
            if (filter.isAssignableFrom(pathProvider.getClassType())) {
                capable.add(pathProvider);
            }
        }
        if (!capable.isEmpty()) {
            ArrayList<String> list = new ArrayList<>(capable.size() + otherPaths.length);
            for (LayerMappedPathProvider p : capable) {
                list.addAll(Arrays.asList(p.getPaths()));
            }
            list.addAll(Arrays.asList(otherPaths));
            return list.toArray(new String[list.size()]);
        }

        return otherPaths;
    }

    /**
     * Looks up a {@link BMProject} implementation in the lookup of the provided
     * project.
     * @param p a generic NBP project, not null.
     * @return a BMProject
     *
     * @throws IllegalStateException in case the provided project is not a BMProject.
     * This is such a ubiquitous method, that it would be too much to force all
     * users to catch some checked exception.
     */
    public static BMProject toBMProject(Project p) {
        BMProject bmProject = p.getLookup().lookup(BMProject.class);
        if (bmProject != null) {
            return bmProject;
        }
        throw new IllegalStateException("You tried to convert a project, which "
                + "doesn't extend BMProject. In BatMass all projects are required"
                + "to extend BMProject.");
    }
}
