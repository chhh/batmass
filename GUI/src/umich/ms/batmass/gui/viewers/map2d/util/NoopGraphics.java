/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.gui.viewers.map2d.util;


import java.awt.AlphaComposite;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.Map;

import javax.swing.SwingUtilities;

/**
 * A subclass of Graphics2D that returns the correct FontMetrics but does not
 * actually paint anything.
 *
 * @see <a href="http://stackoverflow.com/questions/16227877/how-to-update-a-jcomponent-with-html-without-flickering">
 * How to update a JComponent with HTML without flickering?</a>
 */
public class NoopGraphics extends Graphics2D {
    private Font font;
    private Color color = Color.BLACK;
    private final Rectangle clip;
    private Stroke stroke;
    private Paint paint;
    private Color background;
    private AffineTransform transform = new AffineTransform();
    private final RenderingHints renderingHints = new RenderingHints(null);
    private Composite composite = AlphaComposite.SrcOver;
    private boolean isAntiAliased;
    private boolean usesFractionalMetrics;
    private GraphicsConfiguration graphicsConfiguration;

    public static GraphicsConfiguration getDefaultScreenGraphicsConfiguration() {
        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice graphicsDevice = graphicsEnvironment.getDefaultScreenDevice();
        GraphicsConfiguration graphicsConfiguration = graphicsDevice.getDefaultConfiguration();
        return graphicsConfiguration;
    }
    public NoopGraphics(int x, int y, int width, int height) {
        this(x, y, width, height, getDefaultScreenGraphicsConfiguration(), false, false);
    }
    public NoopGraphics(int x, int y, int width, int height, GraphicsConfiguration graphicsConfiguration, boolean isAntiAliased, boolean usesFractionalMetrics) {
        this.graphicsConfiguration = graphicsConfiguration;
        this.isAntiAliased = isAntiAliased;
        this.usesFractionalMetrics = usesFractionalMetrics;
        this.clip = new Rectangle(x, y, width, height);
    }
    @Override public void setXORMode(Color c1) {}
    @Override public void setPaintMode() {}
    @Override public Font getFont() {return font;}
    @Override public void setFont(Font font) {this.font=font;}
    @Override public Color getColor() {return color;}
    @Override public void setColor(Color c) {this.color=c;}
    @Override public void setClip(int x, int y, int width, int height) {
    }
    @Override public void setClip(Shape clip) {this.clip.setRect(clip.getBounds());}
    @Override public FontMetrics getFontMetrics(Font f) {
        // http://stackoverflow.com/questions/2753514/java-friendlier-way-to-get-an-instance-of-fontmetrics
        return new Canvas(graphicsConfiguration).getFontMetrics(f);
    }
    @Override public Rectangle getClipBounds() {return clip.getBounds();}
    @Override public Shape getClip() {return clip;}
    @Override public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {}
    @Override public void fillRect(int x, int y, int width, int height) {}
    @Override public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {}
    @Override public void fillOval(int x, int y, int width, int height) {}
    @Override public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {}
    @Override public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {}
    @Override public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {}
    @Override public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {}
    @Override public void drawOval(int x, int y, int width, int height) {}
    @Override public void drawLine(int x1, int y1, int x2, int y2) {}
    @Override public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) {return true;}
    @Override public boolean drawImage(Image img, int dx1,
            int dy1, int dx2, int dy2, int sx1, int sy1,
            int sx2, int sy2, ImageObserver observer) {
        return drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null, observer);
    }
    @Override public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
        return false;
    }
    @Override public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
        return drawImage(img, x, y, width, height, null, observer);
    }
    @Override public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
        return false;
    }
    @Override public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
        return drawImage(img, x, y, null, observer);
    }
    @Override public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {}
    @Override public void dispose() {}
    @Override public Graphics create() {return this;}
    @Override public void copyArea(int x, int y, int width, int height, int dx, int dy) {}
    @Override public void clipRect(int x, int y, int width, int height) {SwingUtilities.computeIntersection(x, y, width, height, this.clip);}
    @Override public void clearRect(int x, int y, int width, int height) {}
    @Override public void translate(double tx, double ty) {getTransform().translate(tx, ty);}
    @Override public void translate(int x, int y) {translate((double)x, (double)y);}
    @Override public void transform(AffineTransform Tx) {getTransform().concatenate(Tx);}
    @Override public void shear(double shx, double shy) {getTransform().shear(shx, shy);}
    @Override public void scale(double sx, double sy) {getTransform().scale(sx, sy);}
    @Override public void setTransform(AffineTransform Tx) {this.transform = Tx;}
    @Override public void setStroke(Stroke s) {this.stroke = s;}
    @Override public void setRenderingHints(Map<?, ?> hints) {this.renderingHints.clear(); this.renderingHints.putAll(hints);}
    @Override public void setRenderingHint(Key hintKey, Object hintValue) {this.renderingHints.put(hintKey, hintValue);}
    @Override public void setPaint(Paint paint) {this.paint = paint;}
    @Override public void setComposite(Composite comp) {this.composite = comp;}
    @Override public void setBackground(Color color) {this.background = color;}
    @Override public void rotate(double theta, double x, double y) {getTransform().rotate(theta, x, y);}
    @Override public void rotate(double theta) {getTransform().rotate(theta);}
    @Override public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
        return false;
    }
    @Override public AffineTransform getTransform() {return this.transform;}
    @Override public Stroke getStroke() {return this.stroke;}
    @Override public RenderingHints getRenderingHints() {return renderingHints;}
    @Override public Object getRenderingHint(Key hintKey) {return renderingHints.get(hintKey);}
    @Override public Paint getPaint() {return this.paint;}
    @Override public FontRenderContext getFontRenderContext() {return new FontRenderContext(transform, isAntiAliased, usesFractionalMetrics);}
    @Override public GraphicsConfiguration getDeviceConfiguration() {return graphicsConfiguration;}
    @Override public Composite getComposite() {return composite;}
    @Override public Color getBackground() {return background;}
    @Override public void fill(Shape s) {}
    @Override public void drawString(AttributedCharacterIterator iterator, float x, float y) {}
    @Override public void drawString(AttributedCharacterIterator iterator, int x, int y) {drawString(iterator, (float)x, (float)y);}
    @Override public void drawString(String str, float x, float y) {drawString(new AttributedString(str).getIterator(), x, y);}
    @Override public void drawString(String str, int x, int y) {drawString(str, (float)x, (float)y);}
    @Override public void drawRenderedImage(RenderedImage img, AffineTransform xform) {}
    @Override public void drawRenderableImage(RenderableImage img, AffineTransform xform) {}
    @Override public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {}
    @Override public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {return false;}
    @Override public void drawGlyphVector(GlyphVector g, float x, float y) {}
    @Override public void draw(Shape s) {}
    @Override public void clip(Shape s) {}
    @Override public void addRenderingHints(Map<?, ?> hints) {renderingHints.putAll(hints);}
}