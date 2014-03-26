package spacetime;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Properties;
import java.util.StringTokenizer;


public class Scenario {
  SpacetimeApp app;
  LinkedList<STObject> objects = new LinkedList<STObject>();
  LinkedList<STEvent> events = new LinkedList<STEvent>();
  LinkedList<STDiagramDecoration> decorations
      = new LinkedList<STDiagramDecoration>();
  double betaRel=0;
  int eventCounter=1;
  int clockCounter=1;
  int flashCounter=1;
  int decorationCounter=1;
  
  
  
  
  
  public Scenario(SpacetimeApp app){
    this.app = app;
    createInitialObjectsAndEvents();
  }
  
  public void createInitialObjectsAndEvents(){
    STObject ob = new STClock(this, 0, 0, 0);
    addObject(ob);
  }
  
  
  public void addObject(STObject d){
    String name="", label="", note="";
    if(d.getClass().toString().endsWith("STClock")){
      name="C".concat(app.format0.format(clockCounter));
      label=app.bundle.getString("clockLetter").concat(app.format0.format(clockCounter));
      note=app.bundle.getString("clockName").concat(" ").concat(app.format0.format(clockCounter));
      clockCounter++;      
    }
    else if(d.getClass().toString().endsWith("STFlash")){
      name="F".concat(app.format0.format(flashCounter));
      label=app.bundle.getString("flashLetter").concat(app.format0.format(flashCounter));
      note=app.bundle.getString("flashName").concat(" ").concat(app.format0.format(flashCounter));
      flashCounter++;      
    }
    d.setName(name);
    d.setLabel(label);
    d.setNote(note);
    objects.add(d);
    
    
  
    
  }
  
  public void removeObject(STObject d){
    objects.remove(d);
    
    
    
  }

  public int getObjectsCount(){
    return objects.size();
  }
  
  public STObject getObject(int i){
    return (STObject)objects.get(i);
  }
  
  public int getEventsCount(){
    return events.size();
  }
  
  public STEvent getEvent(int i){
    return (STEvent)events.get(i);
  }
  
  
  public void addEvent(STEvent ev){
    String name,label,note;
    if(ev.getClass().toString().endsWith("STBetaChangeEvent")){
      events.add(ev);
      
    }
    else{
      name = "E".concat(app.format0.format(eventCounter));
      label = app.bundle.getString("eventLetter").concat(app.format0.format(eventCounter));
      note=app.bundle.getString("eventName").concat(" ").concat(app.format0.format(eventCounter));
      eventCounter++;
      ev.setName(name);
      ev.setLabel(label);
      ev.setNote(note);
      events.add(ev);
    }
  }
  
  public void removeEvent(STEvent ev){
    if(ev.getClass().toString().endsWith("STBetaChangeEvent")){
      events.remove(ev);
    }
    else{
      events.remove(ev);
    }
  }
  
  public void removeBetaChangeEvents(Collection col){
    events.removeAll(col);
  }

  
  public void addDecoration(STDiagramDecoration d){
    String s="";
    s="D".concat(app.format0.format(decorationCounter));
    decorationCounter++;      
    d.setName(s);
    decorations.add(d);
  }
  
  public void removeDecoration(STDiagramDecoration d){
    decorations.remove(d);
  }
  
  public void setBetaRel(double betaRel){
    this.betaRel=betaRel;
  }
  
  public double getBetaRel(){
    return betaRel;
  }
  
  
  public void loadFromFile(File inFile){
    //load the Properties object from the file
    Properties prop = new Properties();
    
    try{
      FileInputStream inStream = new FileInputStream(inFile);
      try{
        prop.load(inStream);
      }
      catch(IOException e){
        System.out.println("error\n\n" + e.toString());
      }
      try{
        inStream.close();
      }
      catch(IOException e){
        System.out.println("error\n\n" + e.toString());
      }
    }
    catch(FileNotFoundException e){
      System.out.println("error\n\n" + e.toString());
    }
    
    reconstructProgramState(prop); 
    app.historyWriter.stop();
    app.historyWriter = new HistoryWriter(this);
    
  }
  
  
  public void saveToFile(File outFile){
    Properties prop = getProperties();
    
    //Now we save the properties to a file
    
    //here we ensure the file name has the right extension
    String fileName = outFile.getPath();
    if(!fileName.endsWith(".sce")) {
      fileName = fileName.concat(".sce");
      outFile = new File(fileName);
    }
    
    try{
      FileOutputStream outStream = new FileOutputStream(outFile);
      try{
        prop.store(outStream, "This is a scenario file for the program JAVA SPACETIME.");
      }
      catch(IOException e){
        System.out.println("error\n\n" + e.toString());
      }
      try{
      outStream.close();
      }
      catch(IOException e){
        System.out.println("error\n\n" + e.toString());
      }
    }
    catch(FileNotFoundException e){
      System.out.println("error\n\n" + e.toString());
    }
    
    
    
  }
  
  
  public Properties getProperties(){
    Properties prop = new Properties();
    
    //general information about the scenario    
    prop.setProperty("betaRel", app.format9en.format(betaRel));
    prop.setProperty("t", app.format9en.format(app.t));
    prop.setProperty("eventCounter", app.format0en.format(eventCounter));
    prop.setProperty("clockCounter", app.format0en.format(clockCounter));
    prop.setProperty("flashCounter", app.format0en.format(flashCounter));
    prop.setProperty("decorationCounter", app.format0en.format(decorationCounter));

    //now add the list on names of all objects
    String sn="";
    for(int k=0; k<objects.size(); k++){
      STObject d = (STObject)objects.get(k);
      sn=sn.concat(d.getName()).concat(" ");
    }
    prop.setProperty("objects", sn);
    
    //now add the list on names of all events
    sn="";
    for(int k=0; k<events.size(); k++){
      STEvent ev = (STEvent)events.get(k);
      sn=sn.concat(ev.getName()).concat(" ");
    }
    prop.setProperty("events", sn);
    
    //now add the list on names of all decorations
    sn="";
    for(int k=0; k<decorations.size(); k++){
      STDiagramDecoration d = (STDiagramDecoration)decorations.get(k);
      sn=sn.concat(d.getName()).concat(" ");
    }
    prop.setProperty("decorations", sn);
    
    
    //now we generate properties of all the objects in the scenario
    for(int i=0; i<getObjectsCount(); i++){
      STObject d = (STObject)objects.get(i);
      prop.setProperty(d.getName().concat(".name"), d.getName());
      prop.setProperty(d.getName().concat(".label"), d.getLabel());
      prop.setProperty(d.getName().concat(".note"), d.getNote());
      prop.setProperty(d.getName().concat(".class"), d.getClass().toString());
      prop.setProperty(d.getName().concat(".hasBirth"), d.hasBirth() ? "true" : "false");
      prop.setProperty(d.getName().concat(".hasTermination"), d.hasTermination() ? "true" : "false");
      
      //now save the worldlineData for this object
      String swd="";
      LinkedList wd = d.worldlineData;
      for(int k=0; k<wd.size(); k++){
        WorldlineRecord wr = (WorldlineRecord)wd.get(k);
        swd=swd.concat(app.format9en.format(wr.getXp())).concat(" ");
        swd=swd.concat(app.format9en.format(wr.getTp())).concat(" ");
        swd=swd.concat(app.format9en.format(wr.getBetaPOld())).concat(" ");
        swd=swd.concat(app.format9en.format(wr.getBetaPNew())).concat(" ");
      }
      
      prop.setProperty(d.getName().concat(".worldlineData"), swd);
      
    }
    
    //now we generate properties of all the events in the scenario
    for(int i=0; i<getEventsCount(); i++){
      STEvent ev = (STEvent)events.get(i);
      prop.setProperty(ev.getName().concat(".name"), ev.getName());
      prop.setProperty(ev.getName().concat(".label"), ev.getLabel());
      prop.setProperty(ev.getName().concat(".note"), ev.getNote());
      prop.setProperty(ev.getName().concat(".class"), ev.getClass().toString());
      prop.setProperty(ev.getName().concat(".x"), app.format9en.format(ev.getXp()));
      prop.setProperty(ev.getName().concat(".t"), app.format9en.format(ev.getTp()));
      prop.setProperty(ev.getName().concat(".isPlacedAtWorldline"), ev.isPlacedAtWorldline() ? "true" : "false");
      prop.setProperty(ev.getName().concat(".isFixedAtIntersection"), ev.isFixedAtIntersection() ? "true" : "false");
      
      try{
        prop.setProperty(ev.getName().concat(".d"), ev.d.getName());
      }
      catch (java.lang.NullPointerException e){
        prop.setProperty(ev.getName().concat(".d"), "");
      }
      try{
        prop.setProperty(ev.getName().concat(".d1"), ev.d1.getName());
      }
      catch (java.lang.NullPointerException e){
        prop.setProperty(ev.getName().concat(".d1"), "");
      }
      try{
        prop.setProperty(ev.getName().concat(".d2"), ev.d2.getName());
      }
      catch (java.lang.NullPointerException e){
        prop.setProperty(ev.getName().concat(".d2"), "");
      }
      
    }
    
    //now we generate properties of all the decorations in the scenario
    for(int i=0; i<decorations.size(); i++){
      STDiagramDecoration d = (STDiagramDecoration)decorations.get(i);
      prop.setProperty(d.getName().concat(".class"), d.getClass().toString());
      try{
        prop.setProperty(d.getName().concat(".ev"), d.ev.getName());
        prop.setProperty(d.getName().concat(".ev.x"), app.format9en.format(d.ev.getXp()));
        prop.setProperty(d.getName().concat(".ev.t"), app.format9en.format(d.ev.getTp()));
      }
      catch(NullPointerException e){
        prop.setProperty(d.getName().concat(".ev"), "");
      }
      try{
        prop.setProperty(d.getName().concat(".ev1"), d.ev1.getName());
        prop.setProperty(d.getName().concat(".ev1.x"), app.format9en.format(d.ev1.getXp()));
        prop.setProperty(d.getName().concat(".ev1.t"), app.format9en.format(d.ev1.getTp()));
        
      }
      catch(NullPointerException e){
        prop.setProperty(d.getName().concat(".ev1"), "");
      }
      try{
        prop.setProperty(d.getName().concat(".ev2"), d.ev2.getName());
        prop.setProperty(d.getName().concat(".ev2.x"), app.format9en.format(d.ev2.getXp()));
        prop.setProperty(d.getName().concat(".ev2.t"), app.format9en.format(d.ev2.getTp()));
        
      }
      catch(NullPointerException e){
        prop.setProperty(d.getName().concat(".ev2"), "");
      }
      
    }
    
    prop.setProperty("comments", app.textArea.getText());
    
    prop.setProperty("sx1", app.format9en.format(app.pnlDiagram.sx1));
    prop.setProperty("sx2", app.format9en.format(app.pnlDiagram.sx2));
    
    return prop;
  }
  
  public double parseDouble(String s){
    DecimalFormat df = (DecimalFormat)NumberFormat.getNumberInstance(app.enLocale);
    double x=0;
    try {
      x = (df.parse(s)).doubleValue();
    } catch (ParseException e) {
      System.err.println("Bad input: " + s);
    }
    return x;
  }
  
  public int parseInt(String s){
    DecimalFormat df = (DecimalFormat)NumberFormat.getNumberInstance(app.enLocale);
    int x=0;
    try {
      x = (df.parse(s)).intValue();
    } catch (ParseException e) {
      System.err.println("Bad input: " + s);
    }
    return x;
  }
  
  public void reconstructProgramState(Properties prop){
    //now reconstruct the scenario state and all the objects it should contain
    //first clear everything 
    this.objects.removeAll(objects);
    betaRel=0;
    eventCounter=1;
    clockCounter=1;
    flashCounter=1;
    decorationCounter=1;
    betaRel = parseDouble(prop.getProperty("betaRel"));
    app.t = parseDouble(prop.getProperty("t"));
    clockCounter = parseInt(prop.getProperty("clockCounter"));
    flashCounter = parseInt(prop.getProperty("flashCounter"));
    
    
    
    String objects = prop.getProperty("objects");
    StringTokenizer stOb = new StringTokenizer(objects);
    while(stOb.hasMoreTokens()){
      String objectName = stOb.nextToken();
      STObject d;
      if(prop.getProperty(objectName.concat(".class")).endsWith("STClock")){
        d = new STClock(this,0,0,0);
      }
      else{//STFlash
        d = new STFlash(this,0,0,0); 
      }
      d.setName(objectName);
      d.setLabel(prop.getProperty(objectName.concat(".label")));
      d.setNote(prop.getProperty(objectName.concat(".note")));
      d.setHasBirth(prop.getProperty(objectName.concat(".hasBirth")).equals("true") ? true : false);
      d.setHasTermination(prop.getProperty(objectName.concat(".hasTermination")).equals("true") ? true : false);
      
      //now load the worldline data of that object
      d.worldlineData.removeAll(d.worldlineData);
      
      String numbers = prop.getProperty(objectName.concat(".worldlineData"));
      StringTokenizer stW = new StringTokenizer(numbers);
      while(stW.hasMoreTokens()){
        double xp = parseDouble(stW.nextToken());
        double tp = parseDouble(stW.nextToken());
        double betapOld = parseDouble(stW.nextToken());
        double betapNew = parseDouble(stW.nextToken());
        WorldlineRecord wr = new WorldlineRecord(this, xp, tp, betapOld, betapNew);
        d.worldlineData.add(wr);
      }
      
      this.objects.add(d);
      
    }
    
    
    //now reconstruct all events
    this.events.removeAll(events);
    eventCounter = parseInt(prop.getProperty("eventCounter"));
    
    String events = prop.getProperty("events");
    StringTokenizer stEv = new StringTokenizer(events);
    while(stEv.hasMoreTokens()){
      String eventName = stEv.nextToken();
      STEvent ev;
      if(prop.getProperty(eventName.concat(".class")).endsWith("STEvent")){
        ev = new STEvent(this);
      }
      else{//STBetaChangeEvent
        ev = new STBetaChangeEvent(this); 
      }
      ev.setXpTp(parseDouble(prop.getProperty(eventName.concat(".x"))), parseDouble(prop.getProperty(eventName.concat(".t"))));
      
      ev.setName(eventName);
      ev.setLabel(prop.getProperty(eventName.concat(".label")));
      ev.setNote(prop.getProperty(eventName.concat(".note")));
      ev.isPlacedAtWorldline = prop.getProperty(eventName.concat(".isPlacedAtWorldline")).equals("true") ? true : false;
      ev.isFixedAtIntersection = prop.getProperty(eventName.concat(".isFixedAtIntersection")).equals("true") ? true : false;
      
      String sd = prop.getProperty(eventName.concat(".d"));
      String sd1 = prop.getProperty(eventName.concat(".d1"));
      String sd2 = prop.getProperty(eventName.concat(".d2"));
      
      if(!sd.equals("")){
        STObject d=null;
        for(int i=0; i<this.objects.size(); i++){
          if(((STObject)this.objects.get(i)).getName().equals(sd)) {
            d= (STObject)this.objects.get(i);
            break;
          }
        }
        ev.d=d;
      }
      
      if(!sd1.equals("")){
        STObject d1=null;
        for(int i=0; i<this.objects.size(); i++){
          if(((STObject)this.objects.get(i)).getName().equals(sd1)) {
            d1= (STObject)this.objects.get(i);
            break;
          }
        }
        ev.d1=d1;
      }
      
      if(!sd2.equals("")){
        STObject d2=null;
        for(int i=0; i<this.objects.size(); i++){
          if(((STObject)this.objects.get(i)).getName().equals(sd2)) {
            d2= (STObject)this.objects.get(i);
            break;
          }
        }
        ev.d2=d2;
      }
      
      this.events.add(ev);
    }
    
    //now reconstruct all decorations
    this.decorations.removeAll(this.decorations);
    
    String decorations = prop.getProperty("decorations");
    StringTokenizer stDec = new StringTokenizer(decorations);
    while(stDec.hasMoreTokens()){
      String decorationName = stDec.nextToken();
      STDiagramDecoration d;
      
      if(prop.getProperty(decorationName.concat(".class")).endsWith("STLightCone")){
        String sEvName = prop.getProperty(decorationName.concat(".ev"));
        double xp = parseDouble(prop.getProperty(decorationName.concat(".ev.x")));
        double tp = parseDouble(prop.getProperty(decorationName.concat(".ev.t")));
        STEvent ev=new STEvent(this,xp,tp);
        for(int i=0; i<this.events.size(); i++){
          STEvent evi = (STEvent)this.events.get(i);
          if(evi.getName().equals(sEvName)) {
            ev=evi;
            break;
          }
        }
        d = new STLightCone(this, ev);
      }
      else if(prop.getProperty(decorationName.concat(".class")).endsWith("STHyperbola")){//STHyperbola
        String sEvName = prop.getProperty(decorationName.concat(".ev"));
        double xp = parseDouble(prop.getProperty(decorationName.concat(".ev.x")));
        double tp = parseDouble(prop.getProperty(decorationName.concat(".ev.t")));
        STEvent ev=new STEvent(this,xp,tp);
        for(int i=0; i<this.events.size(); i++){
          STEvent evi = (STEvent)this.events.get(i);
          if(evi.getName().equals(sEvName)) {
            ev=evi;
            break;
          }
        }
        d = new STHyperbola(this, ev); 
      }
      else {//STInterval
        String sEvName1 = prop.getProperty(decorationName.concat(".ev1"));
        double xp1 = parseDouble(prop.getProperty(decorationName.concat(".ev1.x")));
        double tp1 = parseDouble(prop.getProperty(decorationName.concat(".ev1.t")));
        STEvent ev1=new STEvent(this,xp1,tp1);
        for(int i=0; i<this.events.size(); i++){
          STEvent evi = (STEvent)this.events.get(i);
          if(evi.getName().equals(sEvName1)) {
            ev1=evi;
            break;
          }
        }
        
        String sEvName2 = prop.getProperty(decorationName.concat(".ev2"));
        double xp2 = parseDouble(prop.getProperty(decorationName.concat(".ev2.x")));
        double tp2 = parseDouble(prop.getProperty(decorationName.concat(".ev2.t")));
        STEvent ev2=new STEvent(this,xp2,tp2);
        for(int i=0; i<this.events.size(); i++){
          STEvent evi = (STEvent)this.events.get(i);
          if(evi.getName().equals(sEvName2)) {
            ev2=evi;
            break;
          }
        }
        d = new STInterval(this, ev1,ev2); 
      }
      
      d.setName(decorationName);
      
      
      this.decorations.add(d);
      
    }
    
    decorationCounter = parseInt(prop.getProperty("decorationCounter"));
    
    
    app.textArea.setText(prop.getProperty("comments"));
    
    double sx1 = parseDouble(prop.getProperty("sx1"));
    double sx2 = parseDouble(prop.getProperty("sx2"));
    app.pnlDiagram.sx1=sx1;
    app.pnlDiagram.sx2=sx2;
    app.pnlHighway.sx1=sx1;
    app.pnlHighway.sx2=sx2;
    
    app.pnlDiagram.cY=app.t;
    app.printTimeInfo();
    
    app.repaint();
    app.objectTable.updateTable();
    app.eventTable.updateTable();
  }
  
  public void clearEverything(){
    betaRel = 0;
    eventCounter = 1;
    clockCounter = 1;
    flashCounter = 1;
    decorationCounter=1;
    events.removeAll(events);
    objects.removeAll(objects);
    decorations.removeAll(decorations);
    app.t=0;
    app.pnlDiagram.cY=app.t;
    app.textArea.setText("You can type your comments for the current scenario here . . .");
    app.pnlHighway.setPreferredMinMaxXY(-5, 5, -1.25, 1.25);
    app.pnlDiagram.setPreferredMinMaxX(-5, 5);
    app.printTimeInfo();
    
    createInitialObjectsAndEvents();
    
    app.repaint();
    app.historyWriter.stop();
    app.historyWriter = new HistoryWriter(this);
    app.objectTable.updateTable();
    app.eventTable.updateTable();
  }
  
  
  
  
}
