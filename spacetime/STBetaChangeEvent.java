package spacetime;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

public class STBetaChangeEvent extends STEvent {
  
  public STBetaChangeEvent(Scenario sc, STObject d, double tp){
    super(sc);
    super.placeAtObject(d, tp);
    d1=d;
    d2=d;
    this.d=d;
    isFixedAtIntersection=true;
  }
  
  public STBetaChangeEvent(Scenario sc){
    super(sc);
  }
  
  
  public void drawInHighway(Graphics2D g2){
    if(Math.abs(sc.app.t-getTp())<tol){
      double xPix0 = pnlHighway.betaGammaScaleWidth;
      double xPix2 = pnlHighway.getWidth();
      double x=pnlHighway.xToPix(getXp());
      if(xPix0<=x && x<=xPix2){
        //vretical green line
        g2.setColor(new Color(0,127,0));
        g2.draw(new Line2D.Double(x, pnlHighway.yToPix(1), x, pnlHighway.yToPix(-1)));
        
        //event label
        String s = getLabel();
        FontMetrics fm = g2.getFontMetrics();
        float sW = (float)(fm.stringWidth(s));
        float sH = (float)fm.getHeight();
        g2.setColor(new Color(0,250,0,100));
        g2.fill(new RoundRectangle2D.Double((float)x-sW/2-1.5, (float)(pnlHighway.yToPix(-1)+8+1.5), sW+3, sH+3, 8, 8));
        g2.setColor(Color.black);
        g2.drawString(s, (float)(x-sW/2), (float)(pnlHighway.yToPix(-1)+sH+8));
       
        
      }
    }
    
   
  }
  
  public void drawInDiagram(Graphics2D g2){
    g2.setColor(color);
    g2.fill(new Rectangle2D.Double(pnlDiagram.xToPix(getXp())-4, pnlDiagram.yToPix(getTp())-4, 8, 8));
    //  event label
    String s = getLabel();
    FontMetrics fm = g2.getFontMetrics();
    float sW = (float)(fm.stringWidth(s));
    float sH = (float)fm.getHeight();
    g2.setColor(new Color(250,250,250,200));
    g2.fill(new RoundRectangle2D.Double((float)pnlDiagram.xToPix(getXp())+8-1.5, (float)(pnlDiagram.yToPix(getTp())+3-sH+1.5), sW+3, sH+3, 8, 8));
    g2.setColor(Color.black);
    g2.drawString(s, (float)pnlDiagram.xToPix(getXp())+8, (float)(pnlDiagram.yToPix(getTp())+3));
  }
  
  
  
  public void placeAtObject(STObject d, double tp){
    
  }
  
  public void placeAtCoincidence(STObject d1, STObject d2){
    
  }
  
  public boolean isPlacedAtWorldline(){
    return false;
  }
  
  public boolean isFixedAtIntersection(){
    return true;
  }
  
  
  
  
  
}
