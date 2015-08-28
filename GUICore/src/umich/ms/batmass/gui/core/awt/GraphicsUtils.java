/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.gui.core.awt;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

/**
 * Utility methods for working with Graphics objects and AWT.
 * @author Dmitry Avtonomov
 */
public class GraphicsUtils {

    private GraphicsUtils() {
        throw new IllegalStateException("No instances.");
    }

    /**
     * Create an affine transform given two rectangles.<br/>
     * Or, alternatively, two points in the old basis and two corresponding points in the new one.
     * This, obviously, doesn't support rotations.<br/>
     * To transform from [(a,b)-(c,d)] to [(e,f)-(g,h)] perform the following computation:<br/>
     *
     * {@code x' = e + (x - a) * (g - e) / (c - a); } <br/>
     * {@code y' = f + (y - b) * (h - f) / (d - b); } <br/>
     *
     * @param r1 original rectangle
     * @param r2 rectangle we want to transform to
     * @return
     */
    public static AffineTransform transformFromPoints(Rectangle r1, Rectangle r2) {
        AffineTransform t = new AffineTransform();
        t.translate(r2.getMinX(), r2.getMinY());
        t.scale(r2.getWidth() / r1.getWidth(), r2.getHeight() / r1.getHeight());
        t.translate(-r1.getMinX(), -r1.getMinY());
        return t;
    }
}
