/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.projects;

import org.openide.loaders.DataNode;
import org.openide.modules.ModuleInstall;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import static umich.ms.batmass.nbputils.WindowSystemUtils.PROJECT_LOGICAL_TAB_ID;

public class Installer extends ModuleInstall {

    @Override
    public void restored() {
        // Force always showing file extensions for Nodes representing files on disk.
        DataNode.setShowFileExtensions(true);
        
        WindowManager.getDefault().invokeWhenUIReady(new Runnable() {

            @Override
            public void run() {
                
                
                TopComponent projectExplorer = WindowManager.getDefault().findTopComponent(PROJECT_LOGICAL_TAB_ID);
                if (!projectExplorer.isOpened()) {
                    projectExplorer.open();
                    projectExplorer.requestActive();
                }
            }
        });
        
        // This was the solution to change BeanTree view of the Projects tab to Outline view.

//        final String PROJECT_LOGICAL_TAB_ID = "projectTabLogical_tc";
//        WindowManager.getDefault().invokeWhenUIReady(new Runnable() {
//            @Override
//            public void run() {
//                RequestProcessor.getDefault().post(new Runnable() {
//                    @Override
//                    public void run() {
//                        //We must do this in the awt thread
//                        SwingUtilities.invokeLater(new Runnable() {
//                            @Override
//                            public void run() {
//                                TopComponent findTopComponent = WindowManager.getDefault().findTopComponent(PROJECT_LOGICAL_TAB_ID); // TODO add your handling code here:
//                                findTopComponent.setVisible(false);
//                                findTopComponent.removeAll();
//                                findTopComponent.setLayout(new BorderLayout());
//                                OutlineView myView2 = new OutlineView("Filename");
//                                Outline outline2 = myView2.getOutline();
//                                outline2.setRootVisible(false);
//                                outline2.setTableHeader(null);
//                                findTopComponent.add(myView2, BorderLayout.CENTER);
//                                findTopComponent.setVisible(true);
//                                findTopComponent.open();
//                                findTopComponent.requestActive();
//                            }
//                        });
//                    }
//                    //This delay is important!
//                }, 1000);
//            }
//        });
    }

}
