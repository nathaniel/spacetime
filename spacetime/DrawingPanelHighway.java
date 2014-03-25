package spacetime;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.BorderFactory;

/**
 * @author Slavomir Tuleja
 *
 * 
 */
public class DrawingPanelHighway extends DrawingPanel implements MouseWheelListener{
   double betaGammaScaleWidth=100;
   double gammaStep=0.1;
   int nbvlength = 2*Math.round((float)((5-1)/gammaStep))+3;
   double[] niceBetaValue = new double[nbvlength];
   public int popupCurX, popupCurY;
   STObject popupObjectOver;
   
   
   
   
  /**
   * Constructor.
   *
   */
  public DrawingPanelHighway(SpacetimeApp app){
    super(app);
    addMouseWheelListener(this);
    prepareNiceBetaVlaues();
  }
  
  
  
  public double betaToY(double beta){
    double n=4;
    double eps=1e-9;
    if(Math.abs(beta)<eps){
      return n*beta;
    }
    else if(beta>=eps){
      return Math.pow(beta, n);
    }
    else {//beta<=-eps
      return -Math.pow(-beta, n);
    }
  }
  
  public double yToBeta(double y){
    double n=4;
    double eps=1e-9;
    if(Math.abs(y)<eps){
      return y/n;
    }
    else if(y>=eps){
      return Math.pow(y,1/n);
    }
    else {//y<=-eps
      return -Math.pow(-y, 1/n);
    }
  }
  
  
  public void drawContents(Graphics2D g2){
    
    
    float[] dashed = {4f,4f};
    BasicStroke dbs = new BasicStroke(0.75f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER,1.0f,dashed,0.0f);
    BasicStroke bs = new BasicStroke(1.5f);
    BasicStroke bst = new BasicStroke(1.0f);
    
    Font f3=new Font("SansSerif", Font.PLAIN, 10);
    g2.setFont(f3);
    
    //highway
    g2.setColor(new Color(250,250,250));
    g2.fill(new Rectangle2D.Double(betaGammaScaleWidth,yToPix(1),getWidth(),yToPix(-1)-yToPix(1)));
    
    g2.setColor(Color.black);
    g2.setStroke(bst);
    //horizontal axis
    FontMetrics fm = g2.getFontMetrics();
    g2.draw(new Line2D.Double(betaGammaScaleWidth,yToPix(0),getWidth(),yToPix(0)));
    double i1 = Math.floor(sx1);
    double i2 = Math.floor(sx2)+1;
    double unit=1;
    while(xToPix(unit)-xToPix(0)<50){
      unit=unit*2;
    }
    while(xToPix(unit)-xToPix(0)>300){
      unit=unit/2;
    }
    DecimalFormat format;
    if(unit<=20 && unit>1-1e-6) {
      format = (DecimalFormat)NumberFormat.getNumberInstance(app.defaultLocale);
      format.applyPattern("0");
    }
    else {
      format = (DecimalFormat)NumberFormat.getNumberInstance(app.defaultLocale);
      format.applyPattern("0.0E0");
    }
    
    int n = Math.round((float)(i1/unit));
    
    
    for(double i=(n-1)*unit; i<=i2; i+=unit){
      double xPix = xToPix(i);
      if(xPix>betaGammaScaleWidth) {
        //vertical tick
        g2.setColor(Color.black);
        g2.draw(new Line2D.Double(xPix,yToPix(0)-4,xPix,yToPix(0)+4));
        //label under the tick
        g2.setColor(Color.gray);
        String s = format.format(i);
        float sW=(float)(fm.stringWidth(s));
        float sH= (float)fm.getHeight();
        g2.drawString(s, (float)(xPix-sW/2), (float)(yToPix(0)+4+sH));
      }
    }
    
    
    g2.setStroke(bs);
    g2.setColor(Color.black);
    //  here all the registered STEvents are drawn
    Iterator it = app.sc.events.iterator();
    while(it.hasNext()){
      STEvent ev = ((STEvent)(it.next())); 
      ev.drawInHighway(g2);
    }
    
    g2.setStroke(bs);
    //  here all the registered STObjects are drawn
    it = app.sc.objects.iterator();
    while(it.hasNext()){
      STObject d = ((STObject)(it.next())); 
      d.drawInHighway(g2);
    }
    
    //background for the vertical axis
    g2.setColor(new Color(250,250,250));
    g2.fill(new Rectangle2D.Double(0,yToPix(1),betaGammaScaleWidth,yToPix(-1)-yToPix(1)));
    g2.setColor(Color.white);
    g2.fill(new Rectangle2D.Double(0,0,betaGammaScaleWidth,yToPix(1)));
    g2.fill(new Rectangle2D.Double(0,yToPix(-1),betaGammaScaleWidth,getHeight()-yToPix(-1)));
    
    
    
    //  vertical axis with beta and gamma scales
    fm = g2.getFontMetrics();
    String s;
    float sW;
    float sH= (float)fm.getHeight();
    
    g2.setColor(Color.black);
    g2.setStroke(bst);
    g2.draw(new Line2D.Double(betaGammaScaleWidth/2,yToPix(1),betaGammaScaleWidth/2,yToPix(-1)));
    double[] axBeta = new double[]{-1, -0.9, -0.8, 0, 0.8, 0.9, 1};
    double[] axGamma = new double[]{1, 2, 3, 5};
    for(int i=0; i<axBeta.length; i++){
      double pixY = yToPix(betaToY(axBeta[i]));
      g2.draw(new Line2D.Double(betaGammaScaleWidth/2-5,pixY,betaGammaScaleWidth/2,pixY));
      s = app.format2.format(axBeta[i]);
      sW = (float)(fm.stringWidth(s));
      g2.drawString(s, (float)(betaGammaScaleWidth/2-5-sW-2), (float)(pixY+sH/3));
    }
    
    for(int i=0; i<axGamma.length; i++){
      double gamma = axGamma[i];
      double pixY = yToPix(betaToY(Math.sqrt(1-1/(gamma*gamma))));
      g2.draw(new Line2D.Double(betaGammaScaleWidth/2+5,pixY,betaGammaScaleWidth/2,pixY));
      s = app.format1.format(axGamma[i]);
      sW = (float)(fm.stringWidth(s));
      g2.drawString(s, (float)(betaGammaScaleWidth/2+5+2), (float)(pixY+sH/3));
      pixY = yToPix(betaToY(-Math.sqrt(1-1/(gamma*gamma))));
      g2.draw(new Line2D.Double(betaGammaScaleWidth/2+5,pixY,betaGammaScaleWidth/2,pixY));
      s = app.format1.format(axGamma[i]);
      sW = (float)(fm.stringWidth(s));
      g2.drawString(s, (float)(betaGammaScaleWidth/2+5+2), (float)(pixY+sH/3));
    }
    //+-Infinity
    double pixY = yToPix(betaToY(1));
    g2.draw(new Line2D.Double(betaGammaScaleWidth/2+5,pixY,betaGammaScaleWidth/2,pixY));
    s = "\u221E";
    sW = (float)(fm.stringWidth(s));
    g2.drawString(s, (float)(betaGammaScaleWidth/2+5+2), (float)(pixY+sH/3));
    
    pixY = yToPix(betaToY(-1));
    g2.draw(new Line2D.Double(betaGammaScaleWidth/2+5,pixY,betaGammaScaleWidth/2,pixY));
    g2.drawString(s, (float)(betaGammaScaleWidth/2+5+2), (float)(pixY+sH/3));
    
    //sign beta under the axis
    pixY = yToPix(betaToY(-1))+15;
    s = "\u03B2";
    sW = (float)(fm.stringWidth(s));
    g2.drawString(s, (float)(betaGammaScaleWidth/2-5-sW-2), (float)(pixY+sH/3));
    
    //  sign gamma under the axis
    pixY = yToPix(betaToY(-1))+15;
    s = "\u03B3";
    sW = (float)(fm.stringWidth(s));
    g2.drawString(s, (float)(betaGammaScaleWidth/2+5+2), (float)(pixY+sH/3));
    
    
    //focus rectangle
    double w=3;
    if(app.hasFocus()){
      g2.setStroke(new BasicStroke((float)w));
      g2.setColor(Color.gray);
      g2.draw(new Rectangle2D.Double(0,0,getWidth()-w,getHeight()-w));
    }
    else{
      g2.setColor(Color.white);
      g2.draw(new Rectangle2D.Double(0,0,getWidth()-w,getHeight()-w));
    }
    g2.setStroke(new BasicStroke((float)1));
  }  
  
  
    
  public void mouseClicked(MouseEvent e) {
    app.requestFocus();
    if(e.getButton()==MouseEvent.BUTTON1){
      //we turn off the programmed state in all the objects
      for(int i=0; i<app.sc.objects.size();i++){
        ((STObject)app.sc.objects.get(i)).setProgrammed(false);
      }
      app.repaint();
           
    }
  }
  
  public void prepareNiceBetaVlaues(){
    //  prepare nice beta values
    int iMax=(nbvlength-3)/2+1;
    for(int i=0; i<iMax; i++){
      double gamma = 1+gammaStep*i;
      double beta = Math.sqrt(1-1/(gamma*gamma));
      niceBetaValue[iMax+i]=beta;
      niceBetaValue[iMax-i]=-beta;
    }
    niceBetaValue[0]=-1;
    niceBetaValue[nbvlength-1]=1;
  }
  
  /**
   * Returns a value of beta adjusted so that it is in a grid of 'nice' values
   * @param betap
   * @return
   */
  public double getAdjustedBetap(double betap){
    double newBetap=0;
    if(betap<=niceBetaValue[0]){
      newBetap = niceBetaValue[0];
    }
    else if(betap>=niceBetaValue[nbvlength-1]){
      newBetap = niceBetaValue[nbvlength-1];
    }
    else{
      for(int i=0; i<nbvlength-1; i++){
        if((niceBetaValue[i]<=betap)&&(betap<niceBetaValue[i+1])){
          double b1=betap-niceBetaValue[i];
          double b2=niceBetaValue[i+1]-betap;
          if(b1<b2) newBetap = niceBetaValue[i];
          else newBetap = niceBetaValue[i+1];
        }
        
      }
    }
    return newBetap;
  }
  
  /**
   * Returns a value of x adjusted so that it is in a grid of 'nice' values
   * @param xp
   * @return
   */
  public double getAdjustedXp(double xp){
    return 0.1*Math.round(10*xp);
  }
  
  
  public void createObject(int iC, int jC, String s){
    double xp = pixToX(iC);
    xp = getAdjustedXp(xp);
    double betap = yToBeta(pixToY(jC));
    betap = getAdjustedBetap(betap);
    STObject d;
    
    //we turn off the programmed state in all the objects
    for(int i=0; i<app.sc.objects.size();i++){
      ((STObject)app.sc.objects.get(i)).setProgrammed(false);
    }
    
    if(s.equals("flash")) {
      d =new STFlash(app.sc, xp, app.t, betap);
    }
    else {//clock
      d =new STClock(app.sc, xp, app.t, betap); 
    }
    
    d.setHighlighted(true);
    app.sc.addObject(d);
    draggedObject = d;
    isObjectDragged=true;
    app.printObjectInfo(d);
    draggedObject=d;
  }

  public void jumpToObject(STObject d){
    double eps = 1e-6; 
    double betap = d.getBetaP(app.t+eps);
    double tRead = d.getSynchronizedTimeReading(app.t);
    double newBetaRel = (app.sc.getBetaRel()+betap)/(1+app.sc.getBetaRel()*betap);
    
    
    app.sc.setBetaRel(newBetaRel);
    app.t=tRead;
    app.printTimeInfo();
    app.pnlDiagram.cY=app.t;
    app.repaint();
    app.historyWriter.continueWriting();
    
  }
  
  public void removeObject(STObject d){
    Iterator itE = app.sc.events.iterator();
    LinkedList eventsToRemove = new LinkedList();
    while(itE.hasNext()){
      STEvent ev=(STEvent)itE.next();
      if(ev.isPlacedAtWorldline()&&(ev.d).equals(d)) eventsToRemove.add(ev);
      if(ev.isFixedAtIntersection()){
        if(ev.getClass().toString().endsWith("STBetaChangeEvent")){
          if(ev.d.equals(d)) eventsToRemove.add(ev);
        }
        else if(ev.isFixedAtIntersection && (ev.d1.equals(d)||ev.d2.equals(d))) eventsToRemove.add(ev);
      }

    }
    app.sc.events.removeAll(eventsToRemove);
    app.sc.removeObject(d);
    if(app.sc.getObjectsCount()==0){
      isObjectDragged=false;
    }
  }

  
  public void removeEventsOnDraggedWorldline(STObject draggedObject){
    //first we cancel birth and termination for that object
    draggedObject.setHasBirth(false);
    draggedObject.setHasTermination(false);
    
    //we REMOVE the events on a worldline of a dragged object
    Iterator itE = app.sc.events.iterator();
    LinkedList eventsToRemove = new LinkedList();
    while(itE.hasNext()){
      STEvent ev=(STEvent)itE.next();
      if(!ev.getClass().toString().endsWith("STBetaChangeEvent")){
        if((ev.isPlacedAtWorldline && ev.d.equals(draggedObject)) || (ev.isFixedAtIntersection &&(ev.d1.equals(draggedObject)||ev.d2.equals(draggedObject))))
          if(draggedObject.isProgrammed()) {
            if( ev.getTp()>=app.t) eventsToRemove.add(ev);//we remove only those events which are in the future
          }
          else{
            eventsToRemove.add(ev);
          }
      }    
    }
    app.sc.events.removeAll(eventsToRemove);
  }
  
  public void mouseEntered(MouseEvent e) {
  }


  public void mouseExited(MouseEvent e) {
  }


  public void mousePressed(MouseEvent e) {
    int iC = e.getX();
    int jC = e.getY();
    iCurDown=iC;
    x1Old=sx1;
    x2Old=sx2;
    double eps = 1e-6;
    
    if(e.isPopupTrigger()) {
      //we turn off the programmed state in all the objects except the current one
      for(int i=0; i<app.sc.objects.size();i++){
        STObject d = ((STObject)app.sc.objects.get(i));
        if(!d.equals(draggedObject)) d.setProgrammed(false);
      }
      
      
      popupCurX=iC;
      popupCurY=jC;
      popupObjectOver=null;
      
      //first we learn if we are OVER an object
      Iterator it = app.sc.objects.iterator();
      while(it.hasNext()){
        STObject d = ((STObject)(it.next()));
        if(d.doesExist(app.t)){
          int i=(int)xToPix(d.getXpAtTp(app.t));
          int j=(int)yToPix(betaToY(d.getBetaP(app.t+eps)));
          if((Math.abs(i-popupCurX)<d.getPixWidth()/2)&&(Math.abs(j-popupCurY)<d.getPixHeight()/2)&& !d.isProgrammed()) {
            if(d.hasBirth()) {
              app.menuItemCancelBirth.setVisible(true);
              app.menuItemSetBirth.setVisible(false);
            }
            else{
              app.menuItemCancelBirth.setVisible(false);
              app.menuItemSetBirth.setVisible(true);
            }

            if(d.hasTermination()) {
              app.menuItemCancelTermination.setVisible(true);
              app.menuItemSetTermination.setVisible(false);
            }
            else{
              app.menuItemCancelTermination.setVisible(false);
              app.menuItemSetTermination.setVisible(true);
            }

            app.popupOverObject.show(e.getComponent(),popupCurX, popupCurY);
            popupObjectOver=d;
            //we do not want to allow jumping to a photon
            String cl = d.getClass().toString();
            if(cl.endsWith("STFlash")) app.menuItemJump.setEnabled(false);
            else app.menuItemJump.setEnabled(true);
            break;
          }
        }
      
      }
      //if we are NOT OVER an object we call a different popup menu
      if(popupObjectOver==null&& !isObjectDragged) app.popupOverHighway.show(e.getComponent(), popupCurX, popupCurY);
    }
  }


  public void mouseReleased(MouseEvent e) {
    mousePressed(e);
  }


  public void mouseDragged(MouseEvent e) {
    if(!app.popupOverHighway.isVisible()&& !app.popupOverObject.isVisible()){
      int iC = e.getX();
      int jC = e.getY();

      if(isObjectDragged){
        setCursor( new Cursor(Cursor.MOVE_CURSOR));
        double xp = draggedObject.getXpAtTp(app.t);
        double betap = draggedObject.getBetaP(app.t);
        double newXp = pixToX(iC);
        newXp = getAdjustedXp(newXp);
        double newBetap = yToBeta(pixToY(jC));
        newBetap = getAdjustedBetap(newBetap);
        if(e.isShiftDown()){
          draggedObject.setXpTpBetap(xp, app.t, newBetap);
          removeEventsOnDraggedWorldline(draggedObject);
        }
        else if(e.isControlDown()){
          draggedObject.setXpTpBetap(newXp, app.t, betap);
          removeEventsOnDraggedWorldline(draggedObject);
        }
        else {
          if(draggedObject.isProgrammed()) draggedObject.addWorldlineRecord(app.t, newBetap);
          else {
            draggedObject.setXpTpBetap(newXp, app.t, newBetap);
            removeEventsOnDraggedWorldline(draggedObject);
          }
        }

        
        
        
        app.printObjectInfo(draggedObject);

        
        
      }
      else{//horizontal dragging
        setCursor( new Cursor(Cursor.E_RESIZE_CURSOR));
        double x2New = x2Old - (pixToX(iC)-pixToX(iCurDown));
        double x1New = x1Old - (pixToX(iC)-pixToX(iCurDown));
        sx1=x1New;
        sx2=x2New;
        app.pnlDiagram.sx1=x1New;
        app.pnlDiagram.sx2=x2New;
      }

      app.repaint();
      
      app.historyWriter.continueWriting();
      app.objectTable.updateTable();
      app.eventTable.updateTable();
    }
  }


  public void mouseMoved(MouseEvent e) {
    double eps = 1e-6;
    if(!app.popupOverHighway.isVisible()&& !app.popupOverObject.isVisible()){
      int iC = e.getX();
      int jC = e.getY();
      Iterator it = app.sc.objects.iterator();
      while(it.hasNext()){
        STObject d = ((STObject)(it.next()));
        if(d.doesExist(app.t)){
          int i=(int)xToPix(d.getXpAtTp(app.t));
          int j=(int)yToPix(betaToY(d.getBetaP(app.t+eps)));
          if((Math.abs(i-iC)<d.getPixWidth()/2)&&(Math.abs(j-jC)<d.getPixHeight()/2)) {
            setCursor( new Cursor(Cursor.HAND_CURSOR));
            d.setHighlighted(true);
            draggedObject = d;
            isObjectDragged=true;
            app.printObjectInfo(d);
            break;
          }
          else {
            setCursor( new Cursor(Cursor.DEFAULT_CURSOR));
            d.setHighlighted(false);
            isObjectDragged=false;
            app.lblComment.setText(" ");
          }
        }
      }
      app.repaint();
      //NO app.historyWriter.continueWriting();
    }
    
  }
  
  public void mouseWheelMoved(MouseWheelEvent e) {
    int notches = e.getWheelRotation();
    
    //we turn off the programmed state in all the objects
    for(int i=0; i<app.sc.objects.size();i++){
      ((STObject)app.sc.objects.get(i)).setProgrammed(false);
    }
    
    
    if (notches < 0) {//scroll up
      double deltaBetap=0.05;
      double newBetaRel = (app.sc.getBetaRel()+deltaBetap)/(1+app.sc.getBetaRel()*deltaBetap);
      app.sc.setBetaRel(newBetaRel);
      app.printTimeInfo();
    } 
    else {//scroll down
      double deltaBetap=-0.05;
      double newBetaRel = (app.sc.getBetaRel()+deltaBetap)/(1+app.sc.getBetaRel()*deltaBetap);
      app.sc.setBetaRel(newBetaRel);
      app.printTimeInfo();
    }
    
    app.repaint();
    app.historyWriter.continueWriting();
    app.objectTable.updateTable();
  }

}
