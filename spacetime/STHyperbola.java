package spacetime;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

public class STHyperbola extends STDiagramDecoration {
  
  public STHyperbola(Scenario sc, STEvent ev){
    super(sc);
    this.ev=ev;
  }

  public void drawInDiagram(Graphics2D g2) {
    g2.setColor(color);
    g2.setStroke(new BasicStroke(1f));
    
    double eps=1e-6;
    double tau2 = ev.getTp()*ev.getTp() - ev.getXp()*ev.getXp();
    if(Math.abs(tau2)<eps){//lightlike interval
      if(Math.abs(ev.getTp())<eps){
        //just point -- do nothing
      }
      else if(ev.getTp()>0){
        if(ev.getXp()>0) g2.draw(new Line2D.Double(pnlDiagram.xToPix(0), pnlDiagram.yToPix(0), pnlDiagram.xToPix(pnlDiagram.sx2), pnlDiagram.yToPix(Math.abs(pnlDiagram.sx2))));
        else if(ev.getXp()<0) g2.draw(new Line2D.Double(pnlDiagram.xToPix(0), pnlDiagram.yToPix(0), pnlDiagram.xToPix(pnlDiagram.sx1), pnlDiagram.yToPix(Math.abs(pnlDiagram.sx1))));
      }
      else{//ev.getTp()<0
        if(ev.getXp()>0) g2.draw(new Line2D.Double(pnlDiagram.xToPix(0), pnlDiagram.yToPix(0), pnlDiagram.xToPix(pnlDiagram.sx2), pnlDiagram.yToPix(-Math.abs(pnlDiagram.sx2))));
        else if(ev.getXp()<0) g2.draw(new Line2D.Double(pnlDiagram.xToPix(0), pnlDiagram.yToPix(0), pnlDiagram.xToPix(pnlDiagram.sx1), pnlDiagram.yToPix(-Math.abs(pnlDiagram.sx1))));
      }
    }
    else if(tau2>0){//timelike interval
      if(ev.getTp()>0){
        double x=pnlDiagram.sx1;
        double dx=(pnlDiagram.sx2-pnlDiagram.sx1)/50;
        while(x<pnlDiagram.sx2){
          g2.draw(new Line2D.Double(pnlDiagram.xToPix(x), pnlDiagram.yToPix(getTPlus(x)), pnlDiagram.xToPix(x+dx), pnlDiagram.yToPix(getTPlus(x+dx))));
          x+=dx;
        }
      }
      else{//ev.getTp<0
        double x=pnlDiagram.sx1;
        double dx=(pnlDiagram.sx2-pnlDiagram.sx1)/50;
        while(x<pnlDiagram.sx2){
          g2.draw(new Line2D.Double(pnlDiagram.xToPix(x), pnlDiagram.yToPix(getTMinus(x)), pnlDiagram.xToPix(x+dx), pnlDiagram.yToPix(getTMinus(x+dx))));
          x+=dx;
        }
      }
    }
    else if(tau2<0) {//spacelike interval
      if(ev.getXp()>0){
        double t=pnlDiagram.sy1;
        double dt=(pnlDiagram.sy2-pnlDiagram.sy1)/50;
        while(t<pnlDiagram.sy2){
          g2.draw(new Line2D.Double(pnlDiagram.xToPix(getXPlus(t)), pnlDiagram.yToPix(t), pnlDiagram.xToPix(getXPlus(t+dt)), pnlDiagram.yToPix(t+dt)));
          t+=dt;
        }
      }
      else{//ev.getXp<0
        double t=pnlDiagram.sy1;
        double dt=(pnlDiagram.sy2-pnlDiagram.sy1)/50;
        while(t<pnlDiagram.sy2){
          g2.draw(new Line2D.Double(pnlDiagram.xToPix(getXMinus(t)), pnlDiagram.yToPix(t), pnlDiagram.xToPix(getXMinus(t+dt)), pnlDiagram.yToPix(t+dt)));
          t+=dt;
        }
      }
    }
    
    
  }
  
  public boolean isMouseOver(int iC, int jC) {
    boolean isOver=false;
    double eps=1e-6;
    
    double tau2 = ev.getTp()*ev.getTp() - ev.getXp()*ev.getXp();
    if(Math.abs(tau2)<eps){//lightlike interval
      if(Math.abs(ev.getTp())<eps){
        //just point -- do nothing
      }
      else if(ev.getTp()>0){
        if(ev.getXp()>0){
          double x = pnlDiagram.pixToX(iC);
          double t = Math.abs(x);
          isOver=Math.abs(jC - pnlDiagram.yToPix(t))<8 && x>0;
        }
        else {//ev.getXp()<0
          double x = pnlDiagram.pixToX(iC);
          double t = Math.abs(x);
          isOver=Math.abs(jC - pnlDiagram.yToPix(t))<8 && x<0;
        }
      }
      else{//ev.getTp()<0
        if(ev.getXp()>0){
          double x = pnlDiagram.pixToX(iC);
          double t = -Math.abs(x);
          isOver=Math.abs(jC - pnlDiagram.yToPix(t))<8 && x>0;
        }
        else {//ev.getXp()<0
          double x = pnlDiagram.pixToX(iC);
          double t = -Math.abs(x);
          isOver=Math.abs(jC - pnlDiagram.yToPix(t))<8 && x<0;
        }
      }
    }
    else if(tau2>0){//timelike interval
      if(ev.getTp()>0){
        double x = pnlDiagram.pixToX(iC);
        double t = getTPlus(x);
        isOver=Math.abs(jC - pnlDiagram.yToPix(t))<8;
      }
      else{//ev.getTp<0
        double x = pnlDiagram.pixToX(iC);
        double t = getTMinus(x);
        isOver=Math.abs(jC - pnlDiagram.yToPix(t))<8;
      }
    }
    else if(tau2<0) {//spacelike interval
      if(ev.getXp()>0){
        double t = pnlDiagram.pixToY(jC);
        double x = getXPlus(t);
        isOver=Math.abs(iC - pnlDiagram.xToPix(x))<8;
      }
      else{//ev.getXp<0
        double t = pnlDiagram.pixToY(jC);
        double x = getXMinus(t);
        isOver=Math.abs(iC - pnlDiagram.xToPix(x))<8;
      }
    }
    
    
    
    return isOver;
  }
  
  public double getTPlus(double x){
    double interval2 = ev.getTp()*ev.getTp() - ev.getXp()*ev.getXp();
    return Math.sqrt(x*x+interval2);
  }
  
  public double getTMinus(double x){
    double interval2 = ev.getTp()*ev.getTp() - ev.getXp()*ev.getXp();
    return -Math.sqrt(x*x+interval2);
  }
  
  public double getXPlus(double t){
    double interval2 = ev.getXp()*ev.getXp() - ev.getTp()*ev.getTp();
    return Math.sqrt(t*t+interval2);
  }
  
  public double getXMinus(double t){
    double interval2 = ev.getXp()*ev.getXp() - ev.getTp()*ev.getTp();
    return -Math.sqrt(t*t+interval2);
  }
}
