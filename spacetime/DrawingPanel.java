package spacetime;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;

/**
 * @author Slavomir Tuleja
 *
 * 
 */
public abstract class DrawingPanel extends javax.swing.JComponent implements MouseListener, MouseMotionListener {

  Dimension size;
  
  
  //display scaling variables
  double sx1, sx2, sy1, sy2, si1, si2, sj1, sj2;
  double cX=0, cY=0;
  boolean sqrAsp=true;
  STObject draggedObject;
  boolean isObjectDragged=false;
  STEvent draggedEvent;
  boolean isEventDragged=false;
  boolean fixedX, fixedY;
  SpacetimeApp app;
  double  x1Old, x2Old;//used for horizontal dragging of the scale
  int iCurDown;
  
  /**
   * Constructor.
   *
   */
  public DrawingPanel(SpacetimeApp app){
    this.app=app;
    size=getSize();
    addMouseListener(this);
    addMouseMotionListener(this);
  }
  
  
  /**
   * 
   * @param g Graphics
   */
  public void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
    //this is done in case the window is resized
    //from knowledge of sy1, sy2 we compute sx1, sx2 according to the size
    //of the display area
    size = getSize();
    si1 = 0;
    si2 = size.getWidth();
    sj1 = 0;
    sj2 = size.getHeight();
    
    if(isSquareAspect()){
      if(fixedY){
      sx1 = cX - 0.5 * (sy2 - sy1) / size.getHeight() * size.getWidth();
      sx2 = cX + 0.5 * (sy2 - sy1) / size.getHeight() * size.getWidth();
      }
      else if(fixedX){
        sy1 = cY - 0.5 * (sx2 - sx1) / size.getWidth() * size.getHeight();
        sy2 = cY + 0.5 * (sx2 - sx1) / size.getWidth() * size.getHeight();
      }
    }
    
    
    //background
    g2.setColor(Color.white);
    g2.fill(new Rectangle2D.Double(0, 0, size.width, size.height));
    g2.setColor(Color.black);
    
    
    
    drawContents(g2);
    
  }
  
  public abstract void drawContents(Graphics2D g2);
  
  
  
  public double xToPix(double x) {
    return si1 + (x - sx1) * (si2 - si1) / (sx2 - sx1);
  }

  public double yToPix(double y) {
    return sj1 + (sy2 - y) * (sj2 - sj1) / (sy2 - sy1);
  }

  public double pixToX(int i) {
    return sx1 + ((double) (i - si1)) * (sx2 - sx1) / (si2 - si1);
  }

  public double pixToY(int j) {
    return sy2 + ((double) (j - sj1)) * (sy1 - sy2) / (sj2 - sj1);
  }

  public void setPreferredMinMaxX(double sx1, double sx2) {
    this.sx1 = sx1;
    this.sx2 = sx2;
    sqrAsp=true;
    fixedX=true;
    fixedY=false;
  }

  public void setPreferredMinMaxY(double sy1, double sy2) {
    this.sy1 = sy1;
    this.sy2 = sy2;
    sqrAsp=true;
    fixedY=true;
    fixedX=false;

    
  }
  
  public void setPreferredMinMaxXY(double sx1, double sx2, double sy1, double sy2){
    this.sx1=sx1;
    this.sx2=sx2;
    this.sy1=sy1;
    this.sy2=sy2;
    sqrAsp=false;
  }
  
  public void setSquareAspect(boolean sqrAsp){
    this.sqrAsp=sqrAsp;
  }
  
  boolean isSquareAspect(){
    return sqrAsp;
  }
  
  public abstract void mouseClicked(MouseEvent e);

  public abstract void mouseEntered(MouseEvent e);

  public abstract void mouseExited(MouseEvent e);

  public abstract void mousePressed(MouseEvent e);

  public abstract void mouseReleased(MouseEvent e);

  public abstract void mouseDragged(MouseEvent e);

  public abstract void mouseMoved(MouseEvent e);
}
