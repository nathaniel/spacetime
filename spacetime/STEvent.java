package spacetime;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

public class STEvent {
  DrawingPanelHighway pnlHighway;
  DrawingPanelDiagram pnlDiagram;
  Scenario sc;
  double x = 0; //lab position of the event
  double t; //lab time of the event
  final Color HIGHLIGHTED_COLOR=new Color(132,24,168);
  final Color USUAL_COLOR=Color.black;
  Color color = USUAL_COLOR;
  double tol ;
  boolean isPlacedAtWorldline=false;
  boolean isFixedAtIntersection=false;
  STObject d; //the object the event is placed at
  STObject d1, d2; //the objects the coincidence of which the event is placed at
  String name="", label="", note="";
  boolean highlighted;
  
  public STEvent(Scenario sc){
    this.sc = sc;
    this.pnlHighway = sc.app.pnlHighway;
    this.pnlDiagram = sc.app.pnlDiagram;
    
    this.x=0;
    this.t=0;
    
    tol = sc.app.TOLERANCE;
  }
  
  public STEvent(Scenario sc, double xp, double tp){
    this.sc = sc;
    this.pnlHighway = sc.app.pnlHighway;
    this.pnlDiagram = sc.app.pnlDiagram;
    double betaRel = sc.getBetaRel();
    double gammaRel=1/Math.sqrt(1-betaRel*betaRel);
    
    this.x=gammaRel*(xp + betaRel*tp);
    this.t=gammaRel*(tp + betaRel*xp);
    
    tol = sc.app.TOLERANCE;
  }
  
  public void drawInHighway(Graphics2D g2){
    if(Math.abs(sc.app.t-getTp())<tol){
      double xPix0 = pnlHighway.betaGammaScaleWidth;
      double xPix2 = pnlHighway.getWidth();
      double x=pnlHighway.xToPix(getXp());
      if(xPix0<=x && x<=xPix2){
        //vretical red line
        g2.setColor(Color.red);
        g2.draw(new Line2D.Double(x, pnlHighway.yToPix(1), x, pnlHighway.yToPix(-1)));
        
        //event label
        String s = getLabel();
        //String sLabel = getLabel().concat(": ").concat(getNote());
        FontMetrics fm = g2.getFontMetrics();
        float sW = (float)(fm.stringWidth(s));
        float sH = (float)fm.getHeight();
        g2.setColor(new Color(250,0,0,100));
        g2.fill(new RoundRectangle2D.Double((float)x-sW/2-1.5, (float)(pnlHighway.yToPix(-1)+8+1.5), sW+3, sH+3, 8, 8));
        g2.setColor(Color.black);
        g2.drawString(s, (float)(x-sW/2), (float)(pnlHighway.yToPix(-1)+sH+8));
       
        
      }
    }
    
   
  }
  
  public void drawInDiagram(Graphics2D g2){
    g2.setColor(color);
    g2.fill(new Ellipse2D.Double(pnlDiagram.xToPix(getXp())-4, pnlDiagram.yToPix(getTp())-4, 8, 8));
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
  
  public void setHighlighted(boolean b){
    if(b) {
      color = HIGHLIGHTED_COLOR;
      highlighted=true;
    }
    else {
      color=USUAL_COLOR;
      highlighted=false;
    }
  }
  
  public boolean isHighlighted(){
    return highlighted;
  }
  
  public void setName(String name){
    this.name = name;
  }
  
  public String getName(){
    return name;
  }
  
  public void setLabel(String lbl){
    label = lbl;
  }
  
  public String getLabel(){
    return label;
  }
  
  public void setNote(String note){
    this.note = note;
  }
  
  public String getNote(){
    return note;
  }
  
  
  public double getXp(){
    double betaRel = sc.getBetaRel();
    double gammaRel = 1/Math.sqrt(1-betaRel*betaRel);
    return gammaRel*(x-betaRel*t);
  }
  
  public double getTp(){
    double betaRel = sc.getBetaRel();
    double gammaRel = 1/Math.sqrt(1-betaRel*betaRel);
    return gammaRel*(t-betaRel*x);
  }
  
  public void setXpTp(double xp, double tp){
    double betaRel = sc.getBetaRel();
    double gammaRel=1/Math.sqrt(1-betaRel*betaRel);
    this.x = gammaRel*(xp + betaRel*tp);
    this.t = gammaRel*(tp + betaRel*xp);
  }
  
  public void placeAtObject(STObject d, double tp){
    double xp = d.getXpAtTp(tp);
    double betaRel = sc.getBetaRel();
    double gammaRel=1/Math.sqrt(1-betaRel*betaRel);
    this.x = gammaRel*(xp + betaRel*tp);
    this.t = gammaRel*(tp + betaRel*xp);
    isPlacedAtWorldline=true;
    this.d=d;
  }
  
  
  
  public boolean isPlacedAtWorldline(){
    return isPlacedAtWorldline;
  }
  
  public boolean isFixedAtIntersection(){
    return isFixedAtIntersection;
  }
  
  
  
  
}
