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
package umich.ms.batmass.projects.services.test;

import java.util.Collection;
import java.util.Date;
import javax.swing.JOptionPane;
import org.netbeans.api.project.Project;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 * TODO: WARNINIG: ACHTUNG: Delete this class! Was here for testing.
 * @author dmitriya
 */

//@ProjectServiceProvider(
//        service=TestService.class,
//        projectType={BMProject.TYPE_ANY}
//)
//        projectType={MetabolomicsProject.TYPE})
//        projectType={"extensions/broken"})
public class TestServiceImpl extends TestService {

    static {
        JOptionPane.showMessageDialog(null, "===> static loading " + TestServiceImpl.class.getCanonicalName());
    }

    private final Project p;

    public TestServiceImpl(Project p) {
        this.p = p;
        JOptionPane.showMessageDialog(null, "===> new TestServiceImpl on " + p + ". " + m());

        String path = "Some";
        Lookup forPath = Lookups.forPath(path);
        Collection<? extends Object> all = forPath.lookupAll(Object.class);
        for (Object o : all) {
            JOptionPane.showMessageDialog(null, String.format(
                    "TestServiceImpl Found an object of type [%s] in path [%s]",
                    o.getClass().getCanonicalName(), path));
        }
    }

    @Override
    public String m() {
        return "Today is: " + new Date().toString();
    }
}
