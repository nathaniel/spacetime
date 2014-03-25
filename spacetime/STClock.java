package spacetime;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

public class STClock extends STObject{
  
  public STClock(Scenario sc, double xp, double tp, double betap){
    super(sc, xp, tp, betap);
    
    double betaMin=sc.app.pnlHighway.niceBetaValue[1];
    double betaMax=sc.app.pnlHighway.niceBetaValue[sc.app.pnlHighway.niceBetaValue.length-2];
    if(betap>betaMax) betap=betaMax;
    else if(betap<betaMin) betap=betaMin;
    setXpTpBetap(xp, tp, betap);
    
    
  }
  
  
  public void drawInHighway(Graphics2D g2){
    double eps = sc.app.TOLERANCE;
    if(doesExist(sc.app.t-eps)||doesExist(sc.app.t+eps)){
      if(!isProgrammed()){
        //old position
        drawClock(g2, sc.app.t, getXpAtTp(sc.app.t), getBetaP(sc.app.t-eps), new Color(180,180,180,200), new Color(255,255,255,200), isSynchronized(sc.app.t-eps));

        //new position
        drawClock(g2, sc.app.t, getXpAtTp(sc.app.t), getBetaP(sc.app.t+eps), color, Color.white, isSynchronized(sc.app.t+eps));

      }
      else {//is programmed
        //old position
        drawClock(g2, sc.app.t, getXpAtTp(sc.app.t), getBetaP(sc.app.t-eps), new Color(180,180,180,200), new Color(255,255,255,200),isSynchronized(sc.app.t-eps));

        //new position
        drawClock(g2, sc.app.t, getXpAtTp(sc.app.t), getBetaP(sc.app.t+eps), new Color(0,192,0), Color.white, isSynchronized(sc.app.t+eps));
      }


    }
  }
  
  private void drawClock(Graphics2D g2, double tp, double xp, double betap, Color fcolor, Color bcolor, boolean isSynchronized){
    double h = getPixHeight();
    double qq=0.75, zz=1.1;
    double gamma = 1/Math.sqrt(1-betap*betap);
    double w=(getPixWidth()/gamma);
    double wc=(zz*qq*getPixWidth()*Math.sqrt(1-betap*betap));
    double xPix = pnlHighway.xToPix(xp);
    double betaPix = pnlHighway.yToPix(sc.app.pnlHighway.betaToY(betap));

    double xPix0 = sc.app.pnlHighway.betaGammaScaleWidth;
    double xPix2 = sc.app.pnlHighway.getWidth();
    if(xPix<xPix0-w/2){//little triangle to the left
      g2.setStroke(new BasicStroke(0.75f));
      g2.setColor(fcolor);
      g2.draw(new Line2D.Double(xPix0, betaPix, xPix0+10, betaPix+5 ));
      g2.draw(new Line2D.Double(xPix0+10, betaPix+5, xPix0+10, betaPix-5 ));
      g2.draw(new Line2D.Double(xPix0+10, betaPix-5, xPix0, betaPix ));
    }
    else if(xPix>xPix2+w/2){//little triangle to the right
      g2.setStroke(new BasicStroke(0.75f));
      g2.setColor(fcolor);
      g2.draw(new Line2D.Double(xPix2, betaPix, xPix2-10, betaPix+5 ));
      g2.draw(new Line2D.Double(xPix2-10, betaPix+5, xPix2-10, betaPix-5 ));
      g2.draw(new Line2D.Double(xPix2-10, betaPix-5, xPix2, betaPix ));
    }
    else {
      g2.setStroke(new BasicStroke(0.75f));
      g2.setColor(fcolor);
      g2.draw(new Line2D.Double(xPix-w/2-4, betaPix, xPix+w/2+4, betaPix ));
      g2.setColor(fcolor);
      g2.fill(new Ellipse2D.Double(xPix-w/2, betaPix-h/2, w, h));
      g2.setColor(bcolor);
      g2.fill(new Ellipse2D.Double(xPix-wc/2, betaPix-zz*qq*h/2, wc, zz*qq*h));
      g2.setStroke(new BasicStroke(1.5f));
      g2.setColor(fcolor);
      
      double r=h/2;
      double x1p=0, y1p=qq*r;
      double x2p=2, y2p=y1p-3;
      double x3p=-2, y3p=y2p;
      
      double time = getTimeReading(tp);
      double cosphi = Math.cos(2*Math.PI*time);
      double sinphi = Math.sin(2*Math.PI*time);
      
      
      double x1 = (x1p*cosphi + y1p*sinphi)/gamma;
      double y1 = -x1p*sinphi + y1p*cosphi;
      
      double x2 = (x2p*cosphi + y2p*sinphi)/gamma;
      double y2 = -x2p*sinphi + y2p*cosphi;
      
      double x3 = (x3p*cosphi + y3p*sinphi)/gamma;
      double y3 = -x3p*sinphi + y3p*cosphi;
      
      g2.draw(new Line2D.Double(xPix, betaPix, xPix+x1, betaPix-y1 ));
      g2.draw(new Line2D.Double(xPix+x1, betaPix-y1, xPix+x2, betaPix-y2 ));
      g2.draw(new Line2D.Double(xPix+x1, betaPix-y1, xPix+x3, betaPix-y3 ));
      g2.setStroke(new BasicStroke(0.75f));
      //g2.fill(new Ellipse2D.Double(xPix-1.0/gamma, betaPix-1.0, 2.0, 2.0/gamma));
      
      //clock reading
      
      time = 0.01*Math.round(100*time);
      String s = sc.app.format2.format(new Double(time));
      FontMetrics fm = g2.getFontMetrics();
      float sW = (float)(fm.stringWidth(s));
      float sH = (float)fm.getHeight();
      if(isSynchronized) g2.setColor(new Color(250,250,250,200));
      else g2.setColor(new Color(5,5,5,200));
      g2.fill(new RoundRectangle2D.Double((float)xPix-sW/2-1.5, (float)betaPix+h/2+1.5, sW+3, sH+3, 8, 8));
      
      if(isSynchronized) g2.setColor(Color.black);
      else g2.setColor(Color.white);
      g2.drawString(s, (float)xPix-sW/2, (float)(betaPix+h/2+sH));
      
      //label
      s = getLabel();
      sW = (float)(fm.stringWidth(s));
      sH = (float)fm.getHeight();
      g2.setColor(new Color(250,250,250,200));
      g2.fill(new RoundRectangle2D.Double((float)(xPix+3+w/2-1.5), (float)(betaPix+sH/3-sH+1.5), sW+3, sH+3, 8, 8));
      
      g2.setColor(Color.black);
      g2.drawString(s, (float)(xPix+3+w/2), (float)(betaPix+sH/3));
    }
  }
  
  public void drawInDiagram(Graphics2D g2){
    double r=8;
    
    
    g2.setColor(color);
    BasicStroke bs = new BasicStroke(1.5f);
    g2.setStroke(bs);
    
    double t1 = pnlDiagram.sy1;
    double x1 = getXpAtTp(t1);
    double t2 = pnlDiagram.sy2;
    double x2 = getXpAtTp(t2);
    
    double tFirst=((WorldlineRecord)worldlineData.get(0)).getTp();
    double tLast=((WorldlineRecord)worldlineData.getLast()).getTp();
    double xFirst=((WorldlineRecord)worldlineData.get(0)).getXp();
    double xLast=((WorldlineRecord)worldlineData.getLast()).getXp();
    
    if(!hasBirth()){
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
  
  public String getReading(double tp){
    return sc.app.bundle.getString("clockReading").concat(" ").concat(sc.app.format2.format(getTimeReading(tp)));
  }
}
