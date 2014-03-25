package spacetime;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

public class STLightCone extends STDiagramDecoration {
  
  public STLightCone(Scenario sc, STEvent ev){
    super(sc);
    this.ev=ev;
  }

  public void drawInDiagram(Graphics2D g2) {
    g2.setColor(color);
    g2.setStroke(new BasicStroke(1f));
    g2.draw(new Line2D.Double(0,pnlDiagram.yToPix(ev.getTp()+(pnlDiagram.pixToX(0)-ev.getXp())),pnlDiagram.getWidth(),pnlDiagram.yToPix(ev.getTp()+(pnlDiagram.pixToX(pnlDiagram.getWidth())-ev.getXp()))));
    g2.draw(new Line2D.Double(0,pnlDiagram.yToPix(ev.getTp()-(pnlDiagram.pixToX(0)-ev.getXp())),pnlDiagram.getWidth(),pnlDiagram.yToPix(ev.getTp()-(pnlDiagram.pixToX(pnlDiagram.getWidth())-ev.getXp()))));
  }

  public boolean isMouseOver(int iC, int jC) {
    double x = pnlDiagram.pixToX(iC);
    double tplus = ev.getTp() + (x-ev.getXp());
    double tminus = ev.getTp() - (x-ev.getXp());
    double jCplus=pnlDiagram.yToPix(tplus);
    double jCminus=pnlDiagram.yToPix(tminus);
    
    return (Math.abs(jC-jCplus)<8)||(Math.abs(jC-jCminus)<8);
  }
  
  
}
