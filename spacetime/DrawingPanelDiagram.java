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



/**
 * @author Slavomir Tuleja
 *
 * 
 */
public class DrawingPanelDiagram extends DrawingPanel implements MouseWheelListener{
  LinkedList<STObject> objectsOver=new LinkedList<STObject>();
  public int popupCurX, popupCurY;
  STEvent popupEventOver;
  STObject popupObjectOver;
  STObject d1, d2;//used in creating intersection of worldlines
  boolean choosingSecondObject=false;
  private LinkedList<Segment> segments1 = new LinkedList<Segment>(),
      segments2 = new LinkedList<Segment>();
  boolean isOverDecoration=false;
  STDiagramDecoration decorationOver, popupDecorationOver;
  boolean choosingSecondEvent=false;
  STEvent ev1, ev2;//used in creating interval
  /**
   * Constructor.
   *
   */
  public DrawingPanelDiagram(SpacetimeApp app){
    super(app);
    addMouseWheelListener(this);
    
  }
  
  
  public void drawContents(Graphics2D g2){
    float[] dashed = {2f,2f};
    BasicStroke dbs = new BasicStroke(0.75f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER,1.0f,dashed,0.0f);
    BasicStroke bs = new BasicStroke(1.5f);
    BasicStroke bst = new BasicStroke(1.0f);
    
    Font f3=new Font("SansSerif", Font.PLAIN, 10);
    g2.setFont(f3);
    
    FontMetrics fm = g2.getFontMetrics();
    
    
    //  here the horizontal strip of simultaneity
    g2.setColor(new Color(0,0,250,100));
    g2.fill(new Rectangle2D.Double(0, yToPix(app.t)-4, getWidth(), 8));
    
    g2.setColor(Color.black);
   
    g2.setStroke(bst);
    //horizontal axis
    g2.draw(new Line2D.Double(0,yToPix(0),getWidth(),yToPix(0)));
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
      g2.setColor(Color.black);
      g2.draw(new Line2D.Double(xToPix(i),yToPix(0)-4,xToPix(i),yToPix(0)+4));
      //label under the tick
      g2.setColor(Color.gray);
      String s = format.format(i);
      float sW=(float)(fm.stringWidth(s));
      float sH= (float)fm.getHeight();
      g2.drawString(s, (float)(xToPix(i)-sW/2), (float)(yToPix(0)+4+sH));
    }
    
    //vertical axis
    g2.setColor(Color.black);
    g2.draw(new Line2D.Double(xToPix(0),0,xToPix(0),getHeight()));
    double j1 = Math.floor(sy1)-1;
    double j2 = Math.floor(sy2)+1;
    n = Math.round((float)(j1/unit));
    for(double j=(n-1)*unit; j<=j2; j+=unit){
      g2.setColor(Color.black);
      g2.draw(new Line2D.Double(xToPix(0)-4, yToPix(j),xToPix(0)+4, yToPix(j)));
      //label left to the tick
      g2.setColor(Color.gray);
      String s = format.format(j);
      float sW=(float)(fm.stringWidth(s));
      float sH= (float)fm.getHeight();
      g2.drawString(s, (float)(xToPix(0)-4-sW), (float)(yToPix(j)+sH/3));
    }
    
    g2.setStroke(bs);
    
    
    
    
    //  here all the registered STObjects are drawn
    Iterator it = app.sc.objects.iterator();
    while(it.hasNext()){
      STObject d = ((STObject)(it.next())); 
      d.drawInDiagram(g2);
    }
    
    g2.setStroke(bs);
    //  here all the registered STEvents are drawn
    it = app.sc.events.iterator();
    while(it.hasNext()){
      STEvent ev = ((STEvent)(it.next())); 
      ev.drawInDiagram(g2);
    }
    
    //  here all decorations (Light cones, hyperbolas, intervals) are drawn
    it = app.sc.decorations.iterator();
    while(it.hasNext()){
      STDiagramDecoration d = ((STDiagramDecoration)(it.next())); 
      d.drawInDiagram(g2);
    }
    
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

  
  /**
   * Returns a value of x adjusted so that it is in a grid of 'nice' values
   * @param xp
   * @return
   */
  public double getAdjustedXp(double xp){
    return 0.1*Math.round(10*xp);
  }
  
  /**
   * Returns a value of t adjusted so that it is in a grid of 'nice' values
   * @param xp
   * @return
   */
  public double getAdjustedTp(double tp){
    return 0.1*Math.round(10*tp);
  }
  
  
  /**
   * This creates an event. It distinguishes three different situations.
   * (1) The cursor is NOT over a worldline -- create a free event
   * (2) The cursor is over ONE worldline -- create an event constrained to this worldline
   * (3) The cursor is over more than one worldline -- create an event in the intersection of 
   *     the first and second worldlines the cursor is over.
   * @param iC
   * @param jC
   */
  public void createEvent(int iC, int jC){
    double xp, tp;
    STEvent ev;
    if(objectsOver.size()==0){
      xp = getAdjustedXp(pixToX(iC));
      tp = getAdjustedTp(pixToY(jC));
      ev =new STEvent(app.sc, xp, tp);
      app.sc.addEvent(ev);
      draggedEvent=ev;
      isEventDragged=true;
    }
    else if(objectsOver.size()==1){
      double jCNow = yToPix(app.t);
      if(Math.abs(jCNow-jC)<8){//we want to be able to place an event on the intersection of a worldline with the line of simultaneity
        tp = app.t;
      }
      else{
        tp = getAdjustedTp(pixToY(jC));
      }
      ev = new STEvent(app.sc);
      STObject d = (STObject)objectsOver.get(0);
      ev.placeAtObject(d, tp);
      app.sc.addEvent(ev);
      draggedEvent=ev;
      isEventDragged=true;
    }
    else {
      STObject d1 = (STObject)objectsOver.get(0);
      STObject d2 = (STObject)objectsOver.get(1);
      createIntersection(d1, d2, iC, jC);
      //createAllIntersectionsAndAddThemToScenario(d1, d2);
    }
    
    
    
  }
  
  public void removeEvent(STEvent ev){
    app.sc.removeEvent(ev);
  }
  
  private void loadWorldlineSegments(STObject d1, STObject d2){
    segments1.removeAll(segments1);
    segments2.removeAll(segments2);
    
    //load all segments of worldline of d1 to a list
    for(int i=0; i<d1.worldlineData.size(); i++){
      if(i==0){
        if(!d1.hasBirth()) {
          WorldlineRecord wr = (WorldlineRecord)d1.worldlineData.getFirst();
          Segment s = new Segment(wr.getXp(), wr.getTp(), wr.getBetaPOld(), Segment.HAS_ONLY_END);
          segments1.add(s);
        }
      }
      else{
        WorldlineRecord wr1 = (WorldlineRecord)d1.worldlineData.get(i-1);
        WorldlineRecord wr2 = (WorldlineRecord)d1.worldlineData.get(i);
        Segment s = new Segment(wr1.getXp(), wr1.getTp(), wr2.getXp(), wr2.getTp());
        segments1.add(s);
      }
    }
    if(!d1.hasTermination()){
      WorldlineRecord wr = (WorldlineRecord)d1.worldlineData.getLast();
      Segment s = new Segment(wr.getXp(), wr.getTp(), wr.getBetaPNew(), Segment.HAS_ONLY_BEGINNING);
      segments1.add(s);
    }

    
    //load all segments of worldline of d2 to a list
    for(int i=0; i<d2.worldlineData.size(); i++){
      if(i==0){
        if(!d2.hasBirth()){
          WorldlineRecord wr = (WorldlineRecord)d2.worldlineData.getFirst();
          Segment s = new Segment(wr.getXp(), wr.getTp(), wr.getBetaPOld(), Segment.HAS_ONLY_END);
          segments2.add(s);
        }
      }
      else{
        WorldlineRecord wr1 = (WorldlineRecord)d2.worldlineData.get(i-1);
        WorldlineRecord wr2 = (WorldlineRecord)d2.worldlineData.get(i);
        Segment s = new Segment(wr1.getXp(), wr1.getTp(), wr2.getXp(), wr2.getTp());
        segments2.add(s);
      }
    }
    if(!d2.hasTermination()){
      WorldlineRecord wr = (WorldlineRecord)d2.worldlineData.getLast();
      Segment s = new Segment(wr.getXp(), wr.getTp(), wr.getBetaPNew(), Segment.HAS_ONLY_BEGINNING);
      segments2.add(s);
    }
  }
  
  public void createAllIntersectionsAndAddThemToScenario(STObject d1, STObject d2){
    loadWorldlineSegments(d1, d2);
    
    //now we create all the intersection events
    for(int i=0; i<segments1.size(); i++)
      for(int j=0; j<segments2.size(); j++){
        Segment s1 = (Segment)segments1.get(i);
        Segment s2 = (Segment)segments2.get(j);
        if(haveIntersection(s1,s2)) {
          STEvent ev = getIntersection(s1,s2);
          ev.isFixedAtIntersection=true;
          ev.d1=d1;
          ev.d2=d2;
          app.sc.addEvent(ev);
        }
      }
  }
  
  
  public void createIntersection(STObject d1, STObject d2, int iC, int jC){
    loadWorldlineSegments(d1, d2);
    
    //now we create all the intersection events
    for(int i=0; i<segments1.size(); i++)
      for(int j=0; j<segments2.size(); j++){
        Segment s1 = (Segment)segments1.get(i);
        Segment s2 = (Segment)segments2.get(j);
        if(haveIntersection(s1,s2)) {
          STEvent ev = getIntersection(s1,s2);
          ev.isFixedAtIntersection=true;
          ev.d1=d1;
          ev.d2=d2;
          double ePixX = xToPix(ev.getXp());
          double ePixY = yToPix(ev.getTp());
          if(Math.abs(iC-ePixX)<8 && Math.abs(jC - ePixY)<8){
            app.sc.addEvent(ev);
          }
          
        }
      }
    
  }
  
  
  
  private boolean haveIntersection(Segment s1, Segment s2){
    double x01 = s1.x1;
    double x02 = s2.x1;
    double t01 = s1.t1;
    double t02 = s2.t1;
    double ax1 = s1.ax;
    double at1 = s1.at;
    double ax2 = s2.ax;
    double at2 = s2.at;
    
    double diskr = at2*ax1 - at1*ax2;
    
    if(Math.abs(diskr)<1e-9) return false;//parallel
    else{//not parallel
      double s = (at1*(x02-x01)+ax1*(t01-t02))/diskr;
      double t = (at2*(x02-x01)+ax2*(t01-t02))/diskr;
      boolean firstOK=false;
      boolean secondOK=false;
      
      if(s1.isRay) {
        if(s1.type==Segment.HAS_ONLY_BEGINNING){
          if(t>=0)firstOK= true;
          else firstOK= false;
        }
        else{
          if(t>0)firstOK= true;
          else firstOK= false;
        }
        
      }
      else{
        if(0<=t && t<1) firstOK=true;
        else firstOK=false;
      }
      
      if(s2.isRay) {
        
        if(s2.type==Segment.HAS_ONLY_BEGINNING){
          if(s>=0)secondOK= true;
          else secondOK= false;
        }
        else{
          if(s>0)secondOK= true;
          else secondOK= false;
        }
      }
      else{
        if(0<=s && s<1) secondOK=true;
        else secondOK=false;
      }
      
      if(firstOK && secondOK) return true;
      else return false;
    }
    
  }
  
  private STEvent getIntersection(Segment s1, Segment s2){
    double x01 = s1.x1;
    double x02 = s2.x1;
    double t01 = s1.t1;
    double t02 = s2.t1;
    double ax1 = s1.ax;
    double at1 = s1.at;
    double ax2 = s2.ax;
    double at2 = s2.at;
    
    double diskr = at2*ax1 - at1*ax2;
    
    double s = (at1*(x02-x01)+ax1*(t01-t02))/diskr;
    double x = x02 + ax2*s;
    double t = t02 + at2*s;
    return new STEvent(app.sc, x, t);
  }



  public void mouseClicked(MouseEvent e) {
    app.requestFocus();
    if(e.getButton()==MouseEvent.BUTTON1){
      if(choosingSecondObject){
        testWhatTheCursorIsOver(e.getX(),e.getY());
        if(objectsOver.size()==1){
          d2 = (STObject)objectsOver.get(0);
          if(!d1.equals(d2)){
            this.createAllIntersectionsAndAddThemToScenario(d1, d2);
          }
        }
        choosingSecondObject=false;
        app.lblComment.setText(" ");
        app.repaint();
        app.historyWriter.continueWriting();
        app.eventTable.updateTable();
      }
      else if(choosingSecondEvent){
        testWhatTheCursorIsOver(e.getX(),e.getY());
        if(isEventDragged){
          ev2=draggedEvent;

          if(!ev1.equals(ev2)){
            STDiagramDecoration d = new STInterval(app.sc,ev1, ev2);
            app.sc.addDecoration(d);
          }
        }
        choosingSecondEvent=false;
        app.lblComment.setText(" ");
        app.repaint();
        app.historyWriter.continueWriting();
      }
    }
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
    
    if(e.isPopupTrigger()&&!choosingSecondEvent && !choosingSecondObject) {
      popupCurX=iC;
      popupCurY=jC;
      testWhatTheCursorIsOver(iC, jC);
      popupEventOver = draggedEvent;
      if(objectsOver.size()>0) popupObjectOver = (STObject)objectsOver.get(0);
      else popupObjectOver=null;
      
      if(isEventDragged) {//over event

        if(draggedEvent.getClass().toString().endsWith("STBetaChangeEvent")){
          app.menuItemRemoveEvent.setVisible(false);
        }
        else{
          app.menuItemRemoveEvent.setVisible(true);
        }
        app.popupOverEvent.show(e.getComponent(),popupCurX, popupCurY);

      }
      else if(objectsOver.size()==0 || objectsOver.size()>1){// not over event nor over worldline
        if(isOverDecoration){
         popupDecorationOver=decorationOver;
         app.popupOverDecoration.show(e.getComponent(), popupCurX, popupCurY);
        }
        else{
          app.menuItemRemoveObject.setVisible(false);
          app.menuItemConstructIntersectionWith.setVisible(false);
          app.popupOverDiagram.show(e.getComponent(), popupCurX, popupCurY);
        }
      }
      else{//not over event but over worldline
        app.menuItemRemoveObject.setVisible(true);
        if(app.sc.getObjectsCount()>1) app.menuItemConstructIntersectionWith.setVisible(true);
        else app.menuItemConstructIntersectionWith.setVisible(false);
        app.popupOverDiagram.show(e.getComponent(), popupCurX, popupCurY);
      }
    }
  }


  public void mouseReleased(MouseEvent e) {
    mousePressed(e);
  }


  public void mouseDragged(MouseEvent e) {
    if(!app.popupOverDiagram.isVisible()&& !app.popupOverEvent.isVisible()&& !app.popupOverDecoration.isVisible()){
      int iC = e.getX();
      int jC = e.getY();

      if(isEventDragged){
        setCursor( new Cursor(Cursor.MOVE_CURSOR));
        double xp = draggedEvent.getXp();
        double tp = draggedEvent.getTp();
        double newTp = pixToY(jC);
        double newXp;

        newTp = getAdjustedTp(newTp);
        if(!draggedEvent.isFixedAtIntersection()){
          if(draggedEvent.isPlacedAtWorldline()){
            newXp = draggedEvent.d.getXpAtTp(newTp);
            draggedEvent.setXpTp(newXp, newTp);
          }
          else{//is not constrained at all
            newXp = pixToX(iC);
            newXp = getAdjustedXp(newXp);
            if(e.isShiftDown()){
              draggedEvent.setXpTp(xp, newTp);
            }
            else if(e.isControlDown()){
              draggedEvent.setXpTp(newXp, tp);
            }
            else {
              draggedEvent.setXpTp(newXp, newTp);
            }
          }
        }
        app.printEventInfo(draggedEvent);

      }
      else{//horizontal dragging
        setCursor( new Cursor(Cursor.E_RESIZE_CURSOR));
        //horizontal dragging
        double x2New = x2Old - (pixToX(iC)-pixToX(iCurDown));
        double x1New = x1Old - (pixToX(iC)-pixToX(iCurDown));
        sx1=x1New;
        sx2=x2New;
        app.pnlHighway.sx1=x1New;
        app.pnlHighway.sx2=x2New;
      }

      app.repaint();
      app.historyWriter.continueWriting();
      app.eventTable.updateTable();
      
    }
  }

  
  /**
   * This method tries to determine if the cursor is or is not over an event 
   * or over a worldline. If it is over an event it writes it into the 
   * variable draggedEvent and sets the boolean isEventDragged to true.
   * If it is over some worldlines, it writes all of them into the list
   * objectsOver. 
   * @param iC
   * @param jC
   */
  public void testWhatTheCursorIsOver(int iC, int jC){
    double tC = pixToY(jC);//time of the point under cursor
    isEventDragged=false;
    
    //First we learn if the mouse pointer is above any event
    
    for(int k=app.sc.events.size()-1; k>=0; k--){
      STEvent ev = ((STEvent)(app.sc.events.get(k)));
      
      int i=(int)xToPix(ev.getXp());
      int j=(int)yToPix(ev.getTp());
      if((Math.abs(i-iC)<16)&&(Math.abs(j-jC)<16)) {
        ev.setHighlighted(true);
        draggedEvent = ev;
        isEventDragged=true;

        break;
      }
      else {
        ev.setHighlighted(false);
        isEventDragged=false;

      }
    }

    //Here we learn if we are above a worldline
    objectsOver.removeAll(objectsOver);
    
    for(int k=0; k<app.sc.objects.size(); k++){
      STObject d = ((STObject)(app.sc.objects.get(k)));
      if(d.doesExist(tC)){
        int i=(int)xToPix(d.getXpAtTp(tC));
        if((Math.abs(i-iC)<8)) {
          d.setHighlighted(true);
          objectsOver.add(d);

        }
        else {
          d.setHighlighted(false);
        }
      }
      else d.setHighlighted(false);
    }
    
    //finally we learn if we are over a decoration
    //first un-highlight all decorations
    for(int k=0; k<app.sc.decorations.size(); k++){
      STDiagramDecoration d=(STDiagramDecoration)app.sc.decorations.get(k);
        d.setHighlighted(false);
    }
    isOverDecoration=false;
    for(int k=0; k<app.sc.decorations.size(); k++){
      STDiagramDecoration d=(STDiagramDecoration)app.sc.decorations.get(k);
      if(d.isMouseOver(iC, jC)) {
        isOverDecoration=true;
        decorationOver=d;
        d.setHighlighted(true);
        break;
      }
    }
  }

  public void mouseMoved(MouseEvent e) {
    if(!app.popupOverDiagram.isVisible()&& !app.popupOverEvent.isVisible() && !app.popupOverDecoration.isVisible()){
      int iC = e.getX();
      int jC = e.getY();
      testWhatTheCursorIsOver(iC, jC);
      
      if(isEventDragged){
        if(!choosingSecondObject && !choosingSecondEvent) app.printEventInfo(draggedEvent);
      }
      else if(objectsOver.size()==0){//not above worldline
        if(!choosingSecondObject && !choosingSecondEvent) app.lblComment.setText(" ");
      }
      else if(objectsOver.size()==1){//above ONE worldline
        STObject d = (STObject)objectsOver.get(0);
        if(!choosingSecondObject && !choosingSecondEvent) app.printObjectInfo(d);
      }
      else{//above two worldlines
        if(!choosingSecondObject && !choosingSecondEvent){
          STObject d1 = (STObject)objectsOver.get(0);
          STObject d2 = (STObject)objectsOver.get(1);
          app.lblComment.setText("Intersection of worldlines of objects ".concat(d1.getLabel()).concat(" and ").concat(d2.getLabel()).concat("."));
        }
      }

      //here the cursor is set
      if(isEventDragged||objectsOver.size()>0||isOverDecoration) setCursor( new Cursor(Cursor.HAND_CURSOR));
      else setCursor( new Cursor(Cursor.DEFAULT_CURSOR));
      
      if(choosingSecondObject) d1.setHighlighted(true);
      if(choosingSecondEvent) ev1.setHighlighted(true);  
      
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
      app.stepForward();
    } 
    else {//scroll down
      app.stepBack();
    }
    
    
  }

  
  
  class Segment{
    boolean isRay;
    double x1, t1;
    double ax, at;
    static final int HAS_ONLY_END=1;
    static final int HAS_ONLY_BEGINNING=2;
    int type;
    
    public Segment(double x1, double t1, double x2, double t2){
      this.x1=x1;
      this.t1=t1;
      at=t2-t1;
      ax=x2-x1;
      isRay=false;
    }
    
    public Segment(double x1, double t1, double beta, int type){
      this.x1=x1;
      this.t1=t1;
      this.type=type;
      
      if(type==2){
        at=1;
        ax=beta*at;
      }
      else{
        at=-1;
        ax=beta*at;
      }
      isRay=true;
    }
  }
  

}
