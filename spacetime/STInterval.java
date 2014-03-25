package spacetime;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;

public class STInterval extends STDiagramDecoration {
  
  public STInterval(Scenario sc, STEvent ev1, STEvent ev2){
    super(sc);
    this.ev1=ev1;
    this.ev2=ev2;
  }

  public void drawInDiagram(Graphics2D g2) {
    g2.setColor(color);
    g2.setStroke(new BasicStroke(1f));
    g2.draw(new Ellipse2D.Double(pnlDiagram.xToPix(ev1.getXp())-4,pnlDiagram.yToPix(ev1.getTp())-4,8,8));
    g2.draw(new Ellipse2D.Double(pnlDiagram.xToPix(ev2.getXp())-4,pnlDiagram.yToPix(ev2.getTp())-4,8,8));
    g2.draw(new Line2D.Double(pnlDiagram.xToPix(ev1.getXp()),pnlDiagram.yToPix(ev1.getTp()),pnlDiagram.xToPix(ev2.getXp()),pnlDiagram.yToPix(ev2.getTp())));
    
    //value
    double tau2 = (ev2.getTp()-ev1.getTp())*(ev2.getTp()-ev1.getTp()) - (ev2.getXp()-ev1.getXp())*(ev2.getXp()-ev1.getXp());
    double interval;
    String stype="";
    if(Math.abs(tau2)<1e-6){
      stype=sc.app.bundle.getString("lightlike");
      interval=0;
    }
    else if(tau2>0){
      stype=sc.app.bundle.getString("timelike");
      interval=Math.sqrt(tau2);
    }
    else{
      stype=sc.app.bundle.getString("spacelike");;
      interval=Math.sqrt(-tau2);
    }
    
    if(!stype.equals("L")) stype=stype.concat(": ").concat(sc.app.format2.format(0.01*Math.round(100*interval)));
    stype=stype.concat("; \u0394x=").concat(sc.app.format2.format(0.01*Math.round(100*(Math.abs(ev1.getXp()-ev2.getXp()))))).concat("; \u0394t=").concat(sc.app.format2.format(0.01*Math.round(100*(Math.abs(ev1.getTp()-ev2.getTp())))));
    
    FontMetrics fm = g2.getFontMetrics();
    float sW = (float)(fm.stringWidth(stype));
    float sH = (float)fm.getHeight();
    g2.setColor(new Color(250,150,150,200));
    double x = (ev1.getXp()+ev2.getXp())/2;
    double t = (ev1.getTp()+ev2.getTp())/2;
    g2.fill(new RoundRectangle2D.Double((float)(pnlDiagram.xToPix(x)-sW/2-1.5), (float)(pnlDiagram.yToPix(t)-sH-sH/3+8+1.5), sW+3, sH+3, 8, 8));
    g2.setColor(Color.black);
    g2.drawString(stype, (float)(pnlDiagram.xToPix(x)-sW/2), (float)(pnlDiagram.yToPix(t)-sH/3+8));
  }

  public boolean isMouseOver(int iC, int jC) {
    double ux=ev2.getXp()-ev1.getXp();
    double ut=ev2.getTp()-ev1.getTp();
    double u=Math.sqrt(ux*ux+ut*ut);
    ux=ux/u;
    ut=ut/u;
    double nx=ut;
    double nt=-ux;
    double xC=pnlDiagram.pixToX(iC);
    double tC=pnlDiagram.pixToY(jC);
    double vx=xC-ev1.getXp();
    double vt=tC-ev1.getTp();
    
    double pu = vx*ux+vt*ut;//projection
    double pn = vx*nx+vt*nt;//perpendicular projection
    pn=Math.abs(pn);
    
    double distancePix = pnlDiagram.xToPix(pn)-pnlDiagram.xToPix(0);
    
    if(0<=pu && pu<=u && distancePix<8) return true;
    else return false;
   
  }
  
  
}
