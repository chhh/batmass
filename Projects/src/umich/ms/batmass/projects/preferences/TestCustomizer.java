/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.projects.preferences;

/**
 *
 * @author dmitriya
 */
import java.awt.BorderLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.netbeans.spi.project.ui.support.ProjectCustomizer.Category;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import umich.ms.batmass.projects.types.proteomics.ProteomicsProject;


/**
 * This should provide a new tab in Project Properties dialog, but it won't work,
 * unless you have a Customizer Provider in project's lookup.
 * 
 * TODO: implement CustomizerProvider, put it into the lookup of BMProject.
 * See: {@link TestCustomizer} and tutorial here: https://blogs.oracle.com/gridbag/entry/project_properties_gui_for_custom
 * 
 * @author dmitriya
 */
public class TestCustomizer implements ProjectCustomizer.CompositeCategoryProvider {

    private String name;

    private TestCustomizer(String name) {
        this.name = name;
    }
    
    @Override
    public Category createCategory(Lookup lkp) {
        return ProjectCustomizer.Category.create(name, name, null);
    }

    @Override
    public JComponent createComponent(Category category, Lookup lkp) {
        JPanel jPanel1 = new JPanel();
        jPanel1.setLayout(new BorderLayout());
        jPanel1.add(new JLabel(name), BorderLayout.CENTER);
        return jPanel1;
    }


    @NbBundle.Messages({"LBL_Config=Configuration"})
    @ProjectCustomizer.CompositeCategoryProvider.Registration(
            projectType = ProteomicsProject.TYPE, 
            position = 10)
    public static TestCustomizer createMyDemoConfigurationTab() {
        return new TestCustomizer(Bundle.LBL_Config());
    }
}