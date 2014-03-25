package spacetime;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;



public class STFlash extends STObject{
  
  public STFlash(Scenario sc, double xp, double tp, double betap){
    super(sc, xp, tp, betap);
    
    if(betap>0) betap=1;
    else betap=-1;
    setXpTpBetap(xp, tp, betap);
    
    pixW=18;
    pixH=18;
  }
  
  
  public void drawInHighway(Graphics2D g2){
    double eps = sc.app.TOLERANCE;
    if(doesExist(sc.app.t-eps)||doesExist(sc.app.t+eps)){
      
      if(!isProgrammed()){
        //old position
        drawFlash(g2, sc.app.t, getXpAtTp(sc.app.t), getBetaP(sc.app.t-eps), new Color(180,180,180,200), new Color(255,255,255,200));

        //new position
        drawFlash(g2, sc.app.t, getXpAtTp(sc.app.t), getBetaP(sc.app.t+eps), color, Color.white);

      }
      else {//is programmed
        //old position
        drawFlash(g2, sc.app.t, getXpAtTp(sc.app.t), getBetaP(sc.app.t-eps), new Color(180,180,180,200), new Color(255,255,255,200));

        //new position
        drawFlash(g2, sc.app.t, getXpAtTp(sc.app.t), getBetaP(sc.app.t+eps), new Color(0,192,0), Color.white);
      }
    }
  }
  
  private void drawFlash(Graphics2D g2, double tp, double xp, double betap, Color fcolor, Color bcolor){
    double h = getPixHeight();
    double xPixFlash = pnlHighway.xToPix(xp);
    double betaPix = pnlHighway.yToPix(sc.app.pnlHighway.betaToY(betap));
    double xPix0 = sc.app.pnlHighway.betaGammaScaleWidth;
    double xPix2 = sc.app.pnlHighway.getWidth();
    if(xPixFlash<xPix0){//little triangle to the left
      g2.setStroke(new BasicStroke(0.75f));
      g2.setColor(fcolor);
      g2.draw(new Line2D.Double(xPix0, betaPix, xPix0+10, betaPix+5 ));
      g2.draw(new Line2D.Double(xPix0+10, betaPix+5, xPix0+10, betaPix-5 ));
      g2.draw(new Line2D.Double(xPix0+10, betaPix-5, xPix0, betaPix ));
    }
    else if(xPixFlash>xPix2){//little triangle to the right
      g2.setStroke(new BasicStroke(0.75f));
      g2.setColor(fcolor);
      g2.draw(new Line2D.Double(xPix2, betaPix, xPix2-10, betaPix+5 ));
      g2.draw(new Line2D.Double(xPix2-10, betaPix+5, xPix2-10, betaPix-5 ));
      g2.draw(new Line2D.Double(xPix2-10, betaPix-5, xPix2, betaPix ));
    }
    else {//flash
      g2.setStroke(new BasicStroke(1.5f));
      g2.setColor(fcolor);
      g2.setColor(fcolor);
      
      double phi=0;
      do{
        double x1=getPixWidth()/2*Math.cos(phi);
        double x2=getPixWidth()/2*Math.cos(phi+Math.PI);
        double y1=getPixHeight()/2*Math.sin(phi);
        double y2=getPixHeight()/2*Math.sin(phi+Math.PI);
        g2.draw(new Line2D.Double(xPixFlash+x1, betaPix+y1, xPixFlash+x2, betaPix+y2));
        phi+=Math.toRadians(45);
      }
      while(phi<Math.PI);
      
      
      //name label
      String s = getLabel();
      FontMetrics fm = g2.getFontMetrics();
      float sW = (float)(fm.stringWidth(s));
      float sH = (float)fm.getHeight();
      g2.setColor(new Color(250,250,250,200));
      g2.fill(new RoundRectangle2D.Double((float)(xPixFlash+3+getPixWidth()/2-1.5), (float)(betaPix+sH/3-sH+1.5), sW+3, sH+3, 8, 8));
      
      g2.setColor(Color.black);
      g2.drawString(s, (float)(xPixFlash+3+getPixWidth()/2), (float)(betaPix+sH/3));

    }
      
      
  }
  
  public void drawInDiagram(Graphics2D g2){
    double r=8;
    g2.setColor(color);
    double t1 = pnlDiagram.sy1;
    double x1 = getXpAtTp(t1);
    double t2 = pnlDiagram.sy2;
    double x2 = getXpAtTp(t2);
    float[] dashed = {5f, 5f};
    BasicStroke dbs = new BasicStroke(1.5f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER,1.0f,dashed,0.0f);
    BasicStroke bs = new BasicStroke(1.5f);
    
    g2.setStroke(dbs);
    
    double tFirst=((WorldlineRecord)worldlineData.get(0)).getTp();
    double tLast=((WorldlineRecord)worldlineData.getLast()).getTp();
    double xFirst=((WorldlineRecord)worldlineData.get(0)).getXp();
    double xLast=((WorldlineRecord)worldlineData.getLast()).getXp();
    
    if(!hasBirth()){
      g2.setColor(color);
      if(tFirst>t1) {
        g2.draw(new Line2D.Double(pnlDiagram.xToPix(x1),pnlDiagram.yToPix(t1),pnlDiagram.xToPix(xFirst),pnlDiagram.yToPix(tFirst)));
      }
    }
    
    if(!hasTermination()){
      if(tLast<t2) {
        g2.draw(new Line2D.Double(pnlDiagram.xToPix(x2),pnlDiagram.yToPix(t2),pnlDiagram.xToPix(xLast),pnlDiagram.yToPix(tLast)));
      }
    }
    
    for(int i=0; i<worldlineData.size()-1; i++){
      x1=((WorldlineRecord)worldlineData.get(i)).getXp();
      x2=((WorldlineRecord)worldlineData.get(i+1)).getXp();
      t1=((WorldlineRecord)worldlineData.get(i)).getTp();
      t2=((WorldlineRecord)worldlineData.get(i+1)).getTp();
      g2.draw(new Line2D.Double(pnlDiagram.xToPix(x1),pnlDiagram.yToPix(t1),pnlDiagram.xToPix(x2),pnlDiagram.yToPix(t2)));
      
    }
    
    
    //the white-circle position in the line of simultaneity
    if(doesExist(sc.app.t)){
      g2.setStroke(bs);
      g2.setColor(Color.white);
      g2.fill(new Ellipse2D.Double(pnlDiagram.xToPix(getXpAtTp(sc.app.t))-r/2, pnlDiagram.yToPix(sc.app.t)-r/2,r,r ));
      
      String s = getLabel();
      FontMetrics fm = g2.getFontMetrics();
      float sW = (float)(fm.stringWidth(s));
      float sH = (float)fm.getHeight();
      g2.setColor(new Color(250,250,250,200));
      g2.fill(new RoundRectangle2D.Double((float)(pnlDiagram.xToPix(getXpAtTp(sc.app.t))-sW/2-1.5), (float)(pnlDiagram.yToPix(sc.app.t)+0.3*sH+1.5), sW+3, sH+3, 8, 8));
      g2.setColor(new Color(0,0,250,200));
      g2.drawString(s, (float)(pnlDiagram.xToPix(getXpAtTp(sc.app.t))-sW/2), (float)(pnlDiagram.yToPix(sc.app.t)+0.3*sH+sH));
    }
  }
  
  public double getGammaP(double tp){
    return Double.POSITIVE_INFINITY;
  }
  
  
  public String getReading(double tp){
    return "";
  }
  
}
