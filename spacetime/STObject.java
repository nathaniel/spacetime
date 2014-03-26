package spacetime;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Iterator;
import java.util.LinkedList;

public abstract class STObject {
  DrawingPanelHighway pnlHighway;
  DrawingPanelDiagram pnlDiagram;
  Scenario sc;
   
  LinkedList<WorldlineRecord> worldlineData = new LinkedList<WorldlineRecord>();
  
  final Color HIGHLIGHTED_COLOR=new Color(132,24,168);
  final Color USUAL_COLOR=Color.black;
  Color color = USUAL_COLOR;
  double pixW=32, pixH=32;//pixel width and height of this drawable object
  String name="", label="", note="";
  boolean highlighted=false;
  boolean hasBirth=false, hasTermination=false;
  boolean isProgrammed=false;
  boolean isSynchronized=true;
  
  public STObject(Scenario sc, double xp, double tp, double betap){
    this.sc = sc;
    this.pnlHighway = sc.app.pnlHighway;
    this.pnlDiagram = sc.app.pnlDiagram;
    
    worldlineData.add(new WorldlineRecord(sc, tp, xp, betap, betap));
  }
  
  
  
  public abstract void drawInHighway(Graphics2D g2);  
  
  
  public abstract void drawInDiagram(Graphics2D g2);
  
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
  
  public void setNote(String s){
    note = s;
  }
  
  public String getNote(){
    return note;
  }
  
  public double getXpAtTp(double tp){
    WorldlineRecord wrFirst = (WorldlineRecord)worldlineData.get(0);
    WorldlineRecord wrLast = (WorldlineRecord)worldlineData.get(worldlineData.size()-1);
    
    double xp0, tp0, betap0;
    
    if(tp<=wrFirst.getTp()){//tp is before the defining worldline record
      xp0=wrFirst.getXp();
      tp0=wrFirst.getTp();
      betap0=wrFirst.getBetaPOld();
    }
    else if(tp>=wrLast.getTp()){//tp is after the last worldline record
      xp0=wrLast.getXp();
      tp0=wrLast.getTp();
      betap0=wrLast.getBetaPNew();
    }
    else{//tp is between the first and the last worldline record
      int i = 0;
      WorldlineRecord wr = (WorldlineRecord)worldlineData.get(i);
      while(wr.getTp()<tp){
        i++;
        wr = (WorldlineRecord)worldlineData.get(i);
      }
      wr=(WorldlineRecord)worldlineData.get(i-1);
      xp0=wr.getXp();
      tp0=wr.getTp();
      betap0=wr.getBetaPNew();
    }
    
    return xp0 + betap0*(tp-tp0);
  }
  
  public double getBetaP(double tp){
    WorldlineRecord wrFirst = (WorldlineRecord)worldlineData.get(0);
    WorldlineRecord wrLast = (WorldlineRecord)worldlineData.get(worldlineData.size()-1);
    
    double betap;
    
    if(tp<=wrFirst.getTp()){//tp is before the defining worldline record
      betap=wrFirst.getBetaPOld();
    }
    else if(tp>=wrLast.getTp()){//tp is after the last worldline record
      betap=wrLast.getBetaPNew();
    }
    else{//tp is between the first and the last worldline record
      int i = 0;
      WorldlineRecord wr = (WorldlineRecord)worldlineData.get(i);
      while(wr.getTp()<tp){
        i++;
        wr = (WorldlineRecord)worldlineData.get(i);
      }
      wr=(WorldlineRecord)worldlineData.get(i-1);
      betap=wr.getBetaPNew();
    }
    
    return betap;
  }
  
  public double getGammaP(double tp){
    double betap=getBetaP(tp);
    return 1/Math.sqrt(1-betap*betap);
  }
  
  
  
  public double getTimeReading(double tp){
    double eps = 1e-6;
    WorldlineRecord wrFirst = (WorldlineRecord)worldlineData.getFirst();
    double betapFirst = wrFirst.getBetaPOld();
    double gammapFirst = 1/Math.sqrt(1-betapFirst*betapFirst);
    double xpFirst = wrFirst.getXp();
    double tpFirst = wrFirst.getTp();    
    double tp0;
    
    if(tp<=tpFirst){
      tp0=getSynchronizedTimeReading(tp);
    }
    else{//tp>tFirst
      tp0=getSynchronizedTimeReading(tpFirst);/*gammapFirst*(tpFirst-betapFirst*xpFirst)*///this is the reading of the clock at the envent of first beta change
      WorldlineRecord wr1 = wrFirst;
      WorldlineRecord wr2;
      boolean finishedBeforeLast=false;
      for(int i=1; i<worldlineData.size(); i++){
        wr2 = (WorldlineRecord)worldlineData.get(i);
        if(wr2.getTp()+eps>tp){
          double xp = getXpAtTp(tp);
          tp0+=Math.sqrt((tp-wr1.getTp())*(tp-wr1.getTp()) - (xp-wr1.getXp())*(xp-wr1.getXp()));
          finishedBeforeLast=true;
          break;
        }
        else{
          tp0+=Math.sqrt((wr2.getTp()-wr1.getTp())*(wr2.getTp()-wr1.getTp()) - (wr2.getXp()-wr1.getXp())*(wr2.getXp()-wr1.getXp()));
          wr1=wr2;
        }
      }
      
      if(!finishedBeforeLast){
        double xp = getXpAtTp(tp);
        tp0+=Math.sqrt((tp-wr1.getTp())*(tp-wr1.getTp()) - (xp-wr1.getXp())*(xp-wr1.getXp()));
      }
      
      
    }
    
    
    return tp0;
  }
  
  public boolean isSynchronized(double tp){
    double eps = 1e-6;
    if(Math.abs(getTimeReading(tp)-getSynchronizedTimeReading(tp))<eps) isSynchronized=true;
    else isSynchronized=false;
    return isSynchronized;
  }
  
  public double getSynchronizedTimeReading(double tp){
    double xp = getXpAtTp(tp);
    double betap = getBetaP(tp);
    return 1/Math.sqrt(1-betap*betap)*(tp-betap*xp);
  }
  
  public abstract String getReading(double tp);
  
  
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
  
  public void setProgrammed(boolean b){
    isProgrammed=b;
  }
  
  public boolean isProgrammed(){
    return isProgrammed;
  }
  
  public void setHasBirth(boolean b){
    
    if(b){
      double tp = sc.app.t;//current tp
      double xp = getXpAtTp(tp);
      double betap = getBetaP(tp);
      double tol=1e-6;
      
      //remove all worldline data with tp <= current tp
      LinkedList<WorldlineRecord> wrtoremove = new LinkedList<WorldlineRecord>();
      for(int i=0; i<worldlineData.size(); i++){
        WorldlineRecord wr = (WorldlineRecord)worldlineData.get(i);
        if(wr.getTp()<=tp+tol) wrtoremove.add(wr);
      }
      worldlineData.removeAll(wrtoremove);
      
      
      //remove all asociated events with tp <= current tp
      String slabel="", snote="";
      LinkedList<STEvent> eventsToRemove = new LinkedList<STEvent>();
      for(int i = 0; i<sc.getEventsCount(); i++){
        STEvent ev = sc.getEvent(i);
        boolean isBetaChangeEventToRemove = ev.getClass().toString().endsWith("STBetaChangeEvent")&&ev.d.equals(this)&&(ev.getTp()<=tp+tol);
        boolean isOtherToRemove = ((ev.isPlacedAtWorldline && ev.d.equals(this)) || (ev.isFixedAtIntersection && (ev.d1.equals(this) || ev.d2.equals(this))))&&(ev.getTp()<=tp+tol);
        if(isBetaChangeEventToRemove || isOtherToRemove) {
          eventsToRemove.add(ev);
          if(Math.abs(ev.getXp()-xp)<1e-5 && Math.abs(ev.getTp()-tp)<1e-5 ){
            //if this event to be removed is positioned at the current event at the worldline
            slabel = ev.getLabel();
            snote = ev.getNote();
          }
        }
      }
      sc.events.removeAll(eventsToRemove);
      
      //create a new worldline record with the current tp
      WorldlineRecord wr = new WorldlineRecord(sc, xp, tp, betap, betap);
      worldlineData.add(0, wr);
      
      
      //create a new STEvent corresponding to the previously created worldline record
      STEvent ev = new STEvent(sc, xp, tp);
      ev.isFixedAtIntersection=true;
      ev.d1=this;
      ev.d2=this;
      sc.addEvent(ev);
      if(!slabel.equals("")){
        ev.setLabel(slabel);
        ev.setNote(snote);
      }
    }
    else{//b==false
      if(hasBirth()){
        //remove the birth event NOT the birth worldline record
        double tp = ((WorldlineRecord)worldlineData.getFirst()).getTp();//current tp
        double tol=1e-6;
        for(int i = 0; i<sc.getEventsCount(); i++){
          STEvent ev = sc.getEvent(i);
          boolean isToBeRemoved = !ev.getClass().toString().endsWith("STBetaChangeEvent")&&((ev.isFixedAtIntersection && (ev.d1.equals(this) || ev.d2.equals(this))))&&(Math.abs(ev.getTp()-tp)<=tol);
          if(isToBeRemoved) {
            sc.events.remove(ev);
            break;
          }
        }
      }
      
    }
    hasBirth=b;
    
  }
  
  public boolean hasBirth(){
    return hasBirth;
  }
  
  public void setHasTermination(boolean b){
    
    if(b){
      double tp = sc.app.t;//current tp
      double xp = getXpAtTp(tp);
      double betap = getBetaP(tp);
      double tol=1e-6;
      
      //remove all worldline data with tp >= current tp
      LinkedList<WorldlineRecord> wrtoremove = new LinkedList<WorldlineRecord>();
      for(int i=0; i<worldlineData.size(); i++){
        WorldlineRecord wr = (WorldlineRecord)worldlineData.get(i);
        if(wr.getTp()>=tp-tol) wrtoremove.add(wr);
      }
      worldlineData.removeAll(wrtoremove);
      
      //remove all associated events with tp >= current tp
      String slabel="", snote="";
      LinkedList<STEvent> eventsToRemove = new LinkedList<STEvent>();
      for(int i = 0; i<sc.getEventsCount(); i++){
        STEvent ev = sc.getEvent(i);
        boolean isBetaChangeEventToRemove = ev.getClass().toString().endsWith("STBetaChangeEvent")&&ev.d.equals(this)&&(ev.getTp()>=tp-tol);
        boolean isOtherToRemove = ((ev.isPlacedAtWorldline && ev.d.equals(this)) || (ev.isFixedAtIntersection && (ev.d1.equals(this) || ev.d2.equals(this))))&&(ev.getTp()>=tp-tol);
        if(isBetaChangeEventToRemove || isOtherToRemove) {
          eventsToRemove.add(ev);
          if(Math.abs(ev.getXp()-xp)<1e-5 && Math.abs(ev.getTp()-tp)<1e-5 ){
            //if this event to be removed is positioned at the current event at the worldline
            slabel = ev.getLabel();
            snote = ev.getNote();
          }
        }
      }
      sc.events.removeAll(eventsToRemove);
      
      //create a new worldline record with the current tp
      WorldlineRecord wr = new WorldlineRecord(sc, xp, tp, betap, betap);
      worldlineData.add(wr);
      
      //create a new STEvent corresponding to the previously created worldline record
      STEvent ev = new STEvent(sc, xp, tp);
      ev.isFixedAtIntersection=true;
      ev.d1=this;
      ev.d2=this;
      sc.addEvent(ev);
      if(!slabel.equals("")){
        ev.setLabel(slabel);
        ev.setNote(snote);
      }
    }
    
    else{//b==false
      if(hasTermination()){
        //remove the termination event NOT the termination worldline record
        double tp = ((WorldlineRecord)worldlineData.getLast()).getTp();//current tp
        double tol=1e-6;
        for(int i = 0; i<sc.getEventsCount(); i++){
          STEvent ev = sc.getEvent(i);
          boolean isToBeRemoved =  !ev.getClass().toString().endsWith("STBetaChangeEvent")&&((ev.isFixedAtIntersection && (ev.d1.equals(this) || ev.d2.equals(this))))&&(Math.abs(ev.getTp()-tp)<=tol);
          if(isToBeRemoved) {
            sc.events.remove(ev);
            break;
          }
        }
      }
    }
    hasTermination=b;
  }
  
  public boolean hasTermination(){
    return hasTermination;
  }
  
  /**
   * Returns true if the object exists in time tp. Otherwise returns false.
   * @param tp
   * @return
   */
  public boolean doesExist(double tp){
    boolean isTimeAfterBirth, isTimeBeforeTermination;
    if(hasBirth()) {
      if(tp>=((WorldlineRecord)worldlineData.getFirst()).getTp()) isTimeAfterBirth=true;
      else isTimeAfterBirth=false;
    }
    else isTimeAfterBirth=true;
    
    if(hasTermination()) {
      if(tp<=((WorldlineRecord)worldlineData.getLast()).getTp()) isTimeBeforeTermination=true;
      else isTimeBeforeTermination=false;
    }
    else isTimeBeforeTermination=true;
    
    
    return isTimeAfterBirth & isTimeBeforeTermination;
  }
  
  /**
   * Sets laboratory properties x, t, beta of the object from xp, tp, betap 
   * @param xp
   * @param tp
   * @param betap
   */
  public void setXpTpBetap(double xp, double tp, double betap){
    //first check if betap is in a reasonable interval
    if(getClass().toString().endsWith("STFlash")){
      if(betap>=0) betap=1;
      else betap=-1;
    }
    else{
      double betaMin=sc.app.pnlHighway.niceBetaValue[1];
      double betaMax=sc.app.pnlHighway.niceBetaValue[sc.app.pnlHighway.niceBetaValue.length-2];
      if(betap>betaMax) betap=betaMax;
      else if(betap<betaMin) betap=betaMin;
    }
    
    
    
    //here we remove all worldline data and create a new initial worldline record
    worldlineData.removeAll(worldlineData);
    
    WorldlineRecord wr = new WorldlineRecord(sc, xp, tp, betap, betap);
    worldlineData.add(wr);
    
    //here we remove associated events of beta changes
    LinkedList<STEvent> eventsToRemove = new LinkedList<STEvent>();
    for(int i = 0; i<sc.getEventsCount();i++){
      STEvent ev = sc.getEvent(i);
      if(ev.getClass().toString().endsWith("STBetaChangeEvent")&& ev.d.equals(this)) {
        eventsToRemove.add(ev);
      }
    }
    sc.removeBetaChangeEvents(eventsToRemove);
    
   
  }
  
  
  public void addWorldlineRecord(double tp, double betap){
    double tol = 1e-6;
    WorldlineRecord wrLast = (WorldlineRecord)worldlineData.getLast();
    double xpLast = wrLast.getXp();
    double tpLast = wrLast.getTp();
    double betapLast = wrLast.getBetaPNew();
    WorldlineRecord wrFirst = (WorldlineRecord)worldlineData.getFirst();
    double xpFirst = wrFirst.getXp();
    double tpFirst = wrFirst.getTp();
    double betapFirst = wrFirst.getBetaPOld();
    
    //first check if betap is in a reasonable interval
    if(getClass().toString().endsWith("STFlash")){
      if(betap>=0) betap=1;
      else betap=-1;
    }
    else{
      double betaMin=sc.app.pnlHighway.niceBetaValue[1];
      double betaMax=sc.app.pnlHighway.niceBetaValue[sc.app.pnlHighway.niceBetaValue.length-2];
      if(betap>betaMax) betap=betaMax;
      else if(betap<betaMin) betap=betaMin;
    }
    
    //Here we learn if tp is "equal" to one of the tp's of the worldline records (within tolerance tol).
    boolean isAt=false;
    int iAt=0;
    for(int i=0; i<worldlineData.size(); i++){
      WorldlineRecord wr = (WorldlineRecord)worldlineData.get(i);
      double tpD = wr.getTp();
      if(Math.abs(tp-tpD)<tol) {
        iAt=i;
        isAt=true;
        break;
      }
    }
    
    
    if(isAt){//tp is coincident with time of some of the worldline records
      //first remove all the subsequent records
      LinkedList<WorldlineRecord> wrremove = new LinkedList<WorldlineRecord>();
      for(int i = iAt+1; i<worldlineData.size(); i++){
        wrremove.add(worldlineData.get(i));
      }
      worldlineData.removeAll(wrremove);
      
      WorldlineRecord wr = (WorldlineRecord)worldlineData.get(iAt);
      
      
      //now remove associated events and other events with tp >= current tp
      String snote="";
      LinkedList<STEvent> eventsToRemove = new LinkedList<STEvent>();
      for(int i = 0; i<sc.getEventsCount();i++){
        STEvent ev = sc.getEvent(i);
        boolean isBetaChangeEventToRemove = ev.getClass().toString().endsWith("STBetaChangeEvent")&&ev.d.equals(this)&&(ev.getTp()>wr.getTp()-tol);
        boolean isOtherToRemove = !ev.getClass().toString().endsWith("STBetaChangeEvent")&&((ev.isPlacedAtWorldline && ev.d.equals(this)) || (ev.isFixedAtIntersection && (ev.d1.equals(this) || ev.d2.equals(this))))&&(ev.getTp()>wr.getTp()-tol);
        if(isBetaChangeEventToRemove || isOtherToRemove) {
          eventsToRemove.add(ev);
          if(Math.abs(ev.getXp()-getXpAtTp(tp))<1e-5 && Math.abs(ev.getTp()-tp)<1e-5 ){
            //if this event to be removed is positioned at the current event at the worldline
            snote = ev.getNote();
          }
        }
      }
      sc.removeBetaChangeEvents(eventsToRemove);
      
      //now set betapNew for the last event
      double ttp=wr.getTp();
      double xp=wr.getXp();
      double betapOld = wr.getBetaPOld();
      wr.setXpTpBetapOldNew(xp, ttp, betapOld, betap);
      
      STEvent ev = new STBetaChangeEvent(sc, this, ttp);
      sc.addEvent(ev);
      if(!snote.equals("")){
        ev.setNote(snote);
      }
      
    }
    else if(tp>tpLast){
      WorldlineRecord wr = new WorldlineRecord(sc, xpLast+betapLast*(tp-tpLast), tp, betapLast, betap);
      worldlineData.add(wr);
      
      //remove all events with tp >= than current tp
      String snote="";
      LinkedList<STEvent> eventsToRemove = new LinkedList<STEvent>();
      for(int i = 0; i<sc.getEventsCount();i++){
        STEvent ev = sc.getEvent(i);
        boolean isOtherToRemove = !ev.getClass().toString().endsWith("STBetaChangeEvent")&& ((ev.isPlacedAtWorldline && ev.d.equals(this)) || (ev.isFixedAtIntersection && (ev.d1.equals(this) || ev.d2.equals(this))))&&(ev.getTp()>wr.getTp()-tol);
        if(isOtherToRemove) {
          eventsToRemove.add(ev);
          if(Math.abs(ev.getXp()-getXpAtTp(tp))<1e-5 && Math.abs(ev.getTp()-tp)<1e-5 ){
            //if this event to be removed is positioned at the current event at the worldline
            snote = ev.getNote();
          }
        }
      }
      sc.removeBetaChangeEvents(eventsToRemove);
      
      
      STEvent ev = new STBetaChangeEvent(sc, this, tp);
      sc.addEvent(ev);
      if(!snote.equals("")){
        ev.setNote(snote);
      }
      
      
    }
    else if(tp<tpFirst){
      //first remove all worldline data
      worldlineData.removeAll(worldlineData);
      
      
      //here we remove associated events of beta changes
      LinkedList<STEvent> eventsToRemove = new LinkedList<STEvent>();
      for(int i = 0; i<sc.getEventsCount();i++){
        STEvent ev = sc.getEvent(i);
        if(ev.getClass().toString().endsWith("STBetaChangeEvent")&& ev.d.equals(this) ) {
          eventsToRemove.add(ev);
        }
      }
      sc.removeBetaChangeEvents(eventsToRemove);
      
      
      
      //add the first worldline record and its event
      WorldlineRecord wr = new WorldlineRecord(sc, xpFirst+betapFirst*(tp-tpFirst), tp, betapFirst, betap);
      worldlineData.add(wr);
      STEvent ev = new STBetaChangeEvent(sc, this, tp);
      sc.addEvent(ev);
      
      //now remove all events with tp >= current tp
      eventsToRemove = new LinkedList<STEvent>();
      for(int i = 0; i<sc.getEventsCount();i++){
        ev = sc.getEvent(i);
        boolean isOtherToRemove = !ev.getClass().toString().endsWith("STBetaChangeEvent")&& ((ev.isPlacedAtWorldline && ev.d.equals(this)) || (ev.isFixedAtIntersection && (ev.d1.equals(this) || ev.d2.equals(this))))&&(ev.getTp()>wr.getTp()-tol);
        if(isOtherToRemove) {
          eventsToRemove.add(ev);
        }
      }
      sc.removeBetaChangeEvents(eventsToRemove);
      
    }
    else{//tpFirst < tp < tpLast
      //first remove worldline records with times >= tp
      LinkedList<WorldlineRecord> wrremove = new LinkedList<WorldlineRecord>();
      for(int i = 0; i<worldlineData.size();i++){
        WorldlineRecord wrtemp = (WorldlineRecord)worldlineData.get(i);
        if(tp<wrtemp.getTp()) {
          wrremove.add(wrtemp);
        }
      }
      worldlineData.removeAll(wrremove);
      
      
      //now remove associated events and other events with tp >= current tp
      String snote="";
      LinkedList<STEvent> eventsToRemove = new LinkedList<STEvent>();
      for(int i = 0; i<sc.getEventsCount();i++){
        STEvent ev = sc.getEvent(i);
        boolean isBetaChangeEventToRemove = ev.getClass().toString().endsWith("STBetaChangeEvent")&&ev.d.equals(this)&&(ev.getTp()>=tp-tol);
        boolean isOtherToRemove = !ev.getClass().toString().endsWith("STBetaChangeEvent")&&((ev.isPlacedAtWorldline && ev.d.equals(this)) || (ev.isFixedAtIntersection && (ev.d1.equals(this) || ev.d2.equals(this))))&&(ev.getTp()>=tp-tol);
        if(isBetaChangeEventToRemove || isOtherToRemove) {
          eventsToRemove.add(ev);
          if(Math.abs(ev.getXp()-getXpAtTp(tp))<1e-5 && Math.abs(ev.getTp()-tp)<1e-5 ){
            //if this event to be removed is positioned at the current event at the worldline
            snote = ev.getNote();
          }
        }
      }
      sc.removeBetaChangeEvents(eventsToRemove);
      
      
      //now create the new worldline record
      wrLast = (WorldlineRecord)worldlineData.getLast();
      xpLast = wrLast.getXp();
      tpLast = wrLast.getTp();
      betapLast = wrLast.getBetaPNew();

      WorldlineRecord wr = new WorldlineRecord(sc, xpLast+betapLast*(tp-tpLast), tp, betapLast, betap);
      worldlineData.add(wr);
      STEvent ev = new STBetaChangeEvent(sc, this, tp);
      sc.addEvent(ev);
      if(!snote.equals("")){
        ev.setNote(snote);
      }
    }
    
    //finally for ALL CASES we want to label the events
    relabelAllBetaChangeEvents();
    
    
     
  }
  
  public void relabelAllBetaChangeEvents(){
    LinkedList<STEvent> eventsToLabel = new LinkedList<STEvent>();
    for(int i = 0; i<sc.getEventsCount();i++){
      STEvent ev = sc.getEvent(i);
      if(ev.getClass().toString().endsWith("STBetaChangeEvent") && ev.d.equals(this)) {
        eventsToLabel.add(ev);
      }
    }
    
    for(int i = 0; i<eventsToLabel.size();i++){
      STEvent ev = (STEvent)eventsToLabel.get(i);
      ev.setLabel(this.getLabel().concat("-\u0394\u03B2").concat(sc.app.format0.format(i+1)));
      ev.setName("eDBeta".concat(ev.d.getName()).concat(sc.app.format0.format(i+1)));
      //ev.setNote("Velocity change ".concat(sc.app.format0.format(i+1)).concat(" at object ").concat(getLabel()));
    }
  }
  
  public double getPixWidth(){
    return pixW;
  }
  
  public double getPixHeight(){
    return pixH;
  }
  
  
  
  
  
}
