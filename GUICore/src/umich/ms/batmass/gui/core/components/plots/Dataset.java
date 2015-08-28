/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.gui.core.components.plots;

/**
 *
 * @author Dmitry
 */
public class Dataset {
    public enum Type {STICKS, PROFILE, DOTS};
    
    protected double[] xVals;
    protected double[] yVals;
    
    protected double xLo;
    protected double xHi;
    protected double yLo;
    protected double yHi;
}
