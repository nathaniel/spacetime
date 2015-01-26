package spacetime;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.UIManager;



/**
 * Application: Spacetime
 * Author: Slavomir Tuleja
 * Work started: July 10, 2007
 * Contributors: Edwin F. Taylor
 * Description: The application is a Java version of famous SPACETIME program
 * by Edwin F. Taylor and Glen Myers
 */
public class SpacetimeApp extends JFrame implements ActionListener, KeyListener, FocusListener{
  String sVersionDate;
  Container cp;
  JMenuBar mainMenu;
  JMenu menuFile, menuEdit, menuZoom, menuHelp;
  JMenuItem menuItemRead, menuItemSave, menuItemNew, menuItemQuit;
  JMenuItem menuItemZoomIn, menuItemZoomOut;
  JMenuItem menuItemHelp, menuItemAbout;
  JMenuItem menuItemUndo, menuItemRedo;
  JPopupMenu popupOverHighway, popupOverObject, popupOverDecoration;
  JMenu menuTransform, menuCreate;
  JMenuItem menuItemClock, menuItemFlash;
  JMenuItem menuItemTransformUp, menuItemTransformDown, menuItemOriginalFrame;
  JMenuItem menuItemJump, menuItemDelete, menuItemProgram, menuItemSetBirth, menuItemSetTermination, menuItemCancelBirth, menuItemCancelTermination;
  JPopupMenu popupOverDiagram, popupOverEvent;
  JMenu menuConstructD, menuConstructE, menuTime;
  JMenuItem menuItemDecorationDelete;
  JMenuItem menuItemConstructEvent, menuItemStepForward, menuItemStepBack, menuItemResetTime, menuItemSetTime;
  JMenuItem menuItemConstructHyperbola, menuItemConstructLightCone, menuItemConstructIntervalTo, menuItemConstructIntersectionWith;
  JMenuItem menuItemRemoveEvent, menuItemRemoveObject;
  ObjectTable objectTable;
  EventTable eventTable;
  JTextArea textArea;
  JScrollPane areaScrollPane;
  JPanel pnlSouth, pnlView;
  DrawingPanelHighway pnlHighway;
  DrawingPanelDiagram pnlDiagram;
  JSplitPane splitPane, splitPaneT;
  JTabbedPane tabbedPane;
  HistoryWriter historyWriter;
  Locale defaultLocale = Locale.getDefault();
  //Locale defaultLocale = new Locale("sk", "SK");
  Locale enLocale = new Locale("en", "US");
  ResourceBundle bundle = ResourceBundle.getBundle("resources/i18n/SpacetimeBundle", defaultLocale);
  
  //Create a file chooser
  JFileChooser fileChooser;
  JPanel pnl1, pnl2;
  
  JLabel lblComment, lblTime;
  JComboBox cbView;
  DecimalFormat format9, format9en;
  DecimalFormat format4, format4en;
  DecimalFormat format3, format3en;
  DecimalFormat format2, format2en;
  DecimalFormat format1, format1en;
  DecimalFormat format0, format0en;
  GridBagConstraints c;
  Scenario sc;
  double t=0;  // Time measured in the current ref frame. No absolute time!
  double dt=0.1;
  int modKey=Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

  boolean shiftPressed=false;
  HelpFrame helpFrame = new HelpFrame(this);
  
  
  Calendar calendar = Calendar.getInstance(defaultLocale);
  final double TOLERANCE=0.01;//tolerance for events happening at the same time
  
  
  public void initialize(){

    File f = new File(".");
    try{
      String path = f.getCanonicalPath();
      fileChooser = new JFileChooser(path);
    }
    catch(IOException e){
      fileChooser = new JFileChooser();
    }

    fileChooser.setFileFilter(new ScenarioFileFilter());
    fileChooser.setAcceptAllFileFilterUsed(false);
    
    
    
    //frame title
    setTitle(bundle.getString("spacetimeUntitled"));
    
    //version date
    calendar.set(2007, Calendar.SEPTEMBER, 17);
    DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.DEFAULT, defaultLocale);
    sVersionDate = dateFormatter.format(calendar.getTime());
    
    //formats
    format9 = (DecimalFormat)NumberFormat.getNumberInstance(defaultLocale);
    format9.applyPattern("###.#########");
    format9en = (DecimalFormat)NumberFormat.getNumberInstance(enLocale);
    format9en.applyPattern("###.#########");
    
    format4 = (DecimalFormat)NumberFormat.getNumberInstance(defaultLocale);
    format4.applyPattern("0.0000");
    format4en = (DecimalFormat)NumberFormat.getNumberInstance(enLocale);
    format4en.applyPattern("0.0000");
    
    format3 = (DecimalFormat)NumberFormat.getNumberInstance(defaultLocale);
    format3.applyPattern("0.000");
    format3en = (DecimalFormat)NumberFormat.getNumberInstance(enLocale);
    format3en.applyPattern("0.000");
    
    format2 = (DecimalFormat)NumberFormat.getNumberInstance(defaultLocale);
    format2.applyPattern("0.00");
    format2en = (DecimalFormat)NumberFormat.getNumberInstance(enLocale);
    format2en.applyPattern("0.00");
    
    format1 = (DecimalFormat)NumberFormat.getNumberInstance(defaultLocale);
    format1.applyPattern("0.0");
    format1en = (DecimalFormat)NumberFormat.getNumberInstance(enLocale);
    format1en.applyPattern("0.0");
    
    format0 = (DecimalFormat)NumberFormat.getNumberInstance(defaultLocale);
    format0.applyPattern("0");
    format0en = (DecimalFormat)NumberFormat.getNumberInstance(enLocale);
    format0en.applyPattern("0");
    
    
    
    addFocusListener(this);
    
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    // Closing is taken care of by the following WindowListener
    addWindowListener( new WindowAdapter() {
      // This window listener responds when the user
      // clicks the window's close box by giving the
      // user a chance to change his mind.
      public void windowClosing(WindowEvent evt) {
        quit();
      }
    } );
    
    
    
    addKeyListener(this);
    
    try {
      //UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      //UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
      //UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
      //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
      
    } catch (Exception e) {
      System.err.println("Couldn't set the desired look and feel...");
    }
    
    
    //  definitions of Menus
    mainMenu = new JMenuBar();
    menuFile = new JMenu(bundle.getString("menuFile"));
    menuTransform = new JMenu(bundle.getString("menuTransform"));
    menuCreate = new JMenu(bundle.getString("menuCreate"));
    menuHelp = new JMenu(bundle.getString("menuHelp"));
    menuConstructD = new JMenu(bundle.getString("menuConstruct"));
    menuConstructE = new JMenu(bundle.getString("menuConstruct"));
    menuTime = new JMenu(bundle.getString("menuTime"));
    menuZoom = new JMenu(bundle.getString("menuZoom"));
    menuEdit = new JMenu(bundle.getString("menuEdit"));
    
    //items for menu FILE
    menuItemRead = makeMenuItem("load", "read", bundle.getString("menuItemRead.hint"), bundle.getString("menuItemRead"));
    menuItemRead.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, modKey));
    menuItemSave = makeMenuItem("save", "save", bundle.getString("menuItemSave.hint"), bundle.getString("menuItemSave"));
    menuItemSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, modKey));
    menuItemNew = makeMenuItem("new", "new", bundle.getString("menuItemNew.hint"), bundle.getString("menuItemNew"));
    menuItemNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, modKey));
    menuItemQuit = makeMenuItem("", "quit", bundle.getString("menuItemQuit.hint"), bundle.getString("menuItemQuit"));
    menuItemQuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, modKey));

    menuFile.add(menuItemNew);
    menuFile.add(menuItemRead);
    menuFile.add(menuItemSave);
    
    menuFile.add(menuItemQuit);
    
    //items for menu EDIT
    menuItemUndo = makeMenuItem("undo", "undo", bundle.getString("menuItemUndo.hint"), bundle.getString("menuItemUndo"));
    menuItemUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, modKey));
    menuItemRedo = makeMenuItem("redo", "redo", bundle.getString("menuItemRestore.hint"), bundle.getString("menuItemRestore"));
    menuItemRedo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, modKey));
    
    menuItemUndo.setEnabled(false);
    menuItemRedo.setEnabled(false);
    
    menuEdit.add(menuItemUndo);
    menuEdit.add(menuItemRedo);
    
    //items for menu ZOOM
    menuItemZoomIn = makeMenuItem("zoomin", "zoom in", bundle.getString("menuItemZoomIn.hint"), bundle.getString("menuItemZoomIn"));
    menuItemZoomIn.setAccelerator(KeyStroke.getKeyStroke('+'));
    menuItemZoomOut = makeMenuItem("zoomout", "zoom out", bundle.getString("menuItemZoomOut.hint"), bundle.getString("menuItemZoomOut"));
    menuItemZoomOut.setAccelerator(KeyStroke.getKeyStroke('-'));
    
    menuZoom.add(menuItemZoomIn);
    menuZoom.add(menuItemZoomOut);
       
    //items for menu Help
    menuItemHelp = makeMenuItem("help", "help", bundle.getString("menuItemHelp.hint"), bundle.getString("menuItemHelp"));
    menuItemHelp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, modKey));
    
    menuItemAbout = makeMenuItem("", "about", bundle.getString("menuItemAbout.hint"), bundle.getString("menuItemAbout"));

    menuHelp.add(menuItemHelp);
    menuHelp.add(menuItemAbout);

    //add everything to the main menu
    mainMenu.add(menuFile);
    mainMenu.add(menuEdit);
    mainMenu.add(menuZoom);
    mainMenu.add(menuHelp);
    setJMenuBar(mainMenu);

   
    // bottom bar of window, with current time and keyboard info
    lblTime = new JLabel(" ");
    lblTime.setFont(new Font("SansSerif", Font.BOLD, 12));
    lblTime.setForeground(Color.black);
    lblTime.setBorder(BorderFactory.createTitledBorder(""));
    
    // Comment area just above bottom bar of window,
    // with notes generated by last action.
    lblComment = new JLabel(" ");
    lblComment.setFont(new Font("SansSerif", Font.BOLD, 12));
    lblComment.setForeground(Color.black);
    lblComment.setBorder(BorderFactory.createTitledBorder(""));
    
  
    // Panel to hold lblComment and lblTime
    pnlSouth = new JPanel();
    pnlSouth.setBackground(new Color(250,250,250));
    pnlSouth.setLayout(new GridBagLayout());
    c = new GridBagConstraints();
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 0.5;
    c.gridx = 0;
    c.gridy = 0;
    c.ipady = 0;
    pnlSouth.add(lblComment,c);
    c.weightx = 0.5;
    c.gridx = 0;
    c.gridy = 1;
    pnlSouth.add(lblTime,c);
    

    pnlHighway = new DrawingPanelHighway(this);
    pnlHighway.setBorder(BorderFactory.createTitledBorder(bundle.getString("pnlHighway")));
    pnlHighway.setSquareAspect(false);
    // Set desired X and Y(=beta) axis ranges
    pnlHighway.setPreferredMinMaxXY(-5, 5, -1.25, 1.25);
    
    
    pnlDiagram = new DrawingPanelDiagram(this);
    pnlDiagram.setBorder(BorderFactory.createTitledBorder(bundle.getString("pnlDiagram")));
    // Set desired X range for Spacetime diagram, with XY aspect ratio = 1
    pnlDiagram.setPreferredMinMaxX(-5, 5);
    
    sc = new Scenario(this);
    
    
    textArea = new JTextArea(bundle.getString("textArea"));
    textArea.setFont(new Font("Serif", Font.PLAIN, 12));
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);
    textArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    
    areaScrollPane = new JScrollPane(textArea);
    areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    areaScrollPane.setPreferredSize(new Dimension(0, 0));
        
    tabbedPane = new JTabbedPane();
    objectTable = new ObjectTable(sc);
    eventTable = new EventTable(sc);
    tabbedPane.add(bundle.getString("tabbedPane.ObjectTable"), objectTable);
    tabbedPane.add(bundle.getString("tabbedPane.EventTable"), eventTable);
    tabbedPane.add(bundle.getString("tabbedPane.Comments"),areaScrollPane);
    
    splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pnlHighway, pnlDiagram);
    splitPane.setOneTouchExpandable(true);
    splitPane.setResizeWeight(0.5);
    splitPane.setDividerLocation(0.5);
    
    splitPaneT = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, splitPane, tabbedPane);
    splitPaneT.setOneTouchExpandable(true);
    splitPaneT.setResizeWeight(0.725);
    splitPaneT.setDividerLocation(0.725);
    
    //popup menus
    
    popupOverHighway = new JPopupMenu();
    pnlHighway.add(popupOverHighway);
    menuItemClock = makeMenuItem("", "clock", bundle.getString("menuItemClock.hint"), bundle.getString("menuItemClock"));
    menuItemFlash = makeMenuItem("", "flash", bundle.getString("menuItemFlash.hint"), bundle.getString("menuItemFlash"));
    //  items for menu Transform
    menuItemTransformUp = makeMenuItem("", "transform up", bundle.getString("menuItemTransformUp.hint"), bundle.getString("menuItemTransformUp")); 
    menuItemTransformDown = makeMenuItem("", "transform down", bundle.getString("menuItemTransformDown.hint"), bundle.getString("menuItemTransformDown"));
    menuItemOriginalFrame = makeMenuItem("", "original frame", bundle.getString("menuItemOriginalFrame.hint"), bundle.getString("menuItemOriginalFrame"));
    menuTransform.add(menuItemTransformUp);
    menuTransform.add(menuItemTransformDown);
    menuTransform.add(menuItemOriginalFrame);
    
    
    menuCreate.add(menuItemClock);
    menuCreate.add(menuItemFlash);
    
    popupOverHighway.add(menuCreate);
    popupOverHighway.add(menuTransform);
    
    popupOverObject = new JPopupMenu();
    pnlHighway.add(popupOverObject);
    menuItemJump = makeMenuItem("", "jump", bundle.getString("menuItemJump.hint"), bundle.getString("menuItemJump"));
    menuItemProgram = makeMenuItem("", "program", bundle.getString("menuItemProgram.hint"), bundle.getString("menuItemProgram"));
    menuItemDelete = makeMenuItem("", "delete object", bundle.getString("menuItemDelete.hint"), bundle.getString("menuItemDelete"));
    menuItemSetBirth = makeMenuItem("", "set birth", bundle.getString("menuItemSetBirth.hint"), bundle.getString("menuItemSetBirth"));
    menuItemSetTermination = makeMenuItem("", "set termination", bundle.getString("menuItemSetTermination.hint"), bundle.getString("menuItemSetTermination"));
    menuItemCancelBirth = makeMenuItem("", "cancel birth", bundle.getString("menuItemCancelBirth.hint"), bundle.getString("menuItemCancelBirth"));
    menuItemCancelTermination = makeMenuItem("", "cancel termination", bundle.getString("menuItemCancelTermination.hint"), bundle.getString("menuItemCancelTermination"));
    
    menuItemCancelBirth.setVisible(false);
    menuItemCancelTermination.setVisible(false);
    
    popupOverObject.add(menuItemJump);
    popupOverObject.add(menuItemProgram);
    popupOverObject.add(menuItemSetBirth);
    popupOverObject.add(menuItemCancelBirth);
    popupOverObject.add(menuItemSetTermination);
    popupOverObject.add(menuItemCancelTermination);
    popupOverObject.add(menuItemDelete);
    
    
    
    popupOverDiagram = new JPopupMenu();
    pnlDiagram.add(popupOverDiagram);
    menuItemConstructEvent = makeMenuItem("","construct event", bundle.getString("menuItemConstructEvent.hint"),bundle.getString("menuItemConstructEvent"));
    menuItemRemoveObject = makeMenuItem("","delete worldline", bundle.getString("menuItemRemoveObject.hint"),bundle.getString("menuItemRemoveObject"));
    menuItemConstructIntersectionWith = makeMenuItem("","construct intersection", bundle.getString("menuItemConstructIntersectionWith.hint"), bundle.getString("menuItemConstructIntersectionWith"));
    
    menuItemStepBack = makeMenuItem("", "step back", bundle.getString("menuItemStepBack.hint"), bundle.getString("menuItemStepBack"));
    menuItemStepForward = makeMenuItem("", "step forward", bundle.getString("menuItemStepForward.hint"), bundle.getString("menuItemStepForward"));
    menuItemResetTime = makeMenuItem("", "reset time", bundle.getString("menuItemResetTime.hint"), bundle.getString("menuItemResetTime"));
    menuItemSetTime = makeMenuItem("", "set time", bundle.getString("menuItemSetTime.hint"), bundle.getString("menuItemSetTime"));
    menuConstructD.add(menuItemConstructEvent);
    menuConstructD.add(menuItemConstructIntersectionWith);
    menuTime.add(menuItemStepForward);
    menuTime.add(menuItemStepBack);
    menuTime.add(menuItemResetTime);
    menuTime.add(menuItemSetTime);
    popupOverDiagram.add(menuConstructD);
    popupOverDiagram.add(menuTime);
    popupOverDiagram.add(menuItemRemoveObject);
    
    popupOverEvent = new JPopupMenu();
    pnlDiagram.add(popupOverEvent);
    menuItemConstructLightCone = makeMenuItem("","lightcone", bundle.getString("menuItemConstructLightCone.hint"), bundle.getString("menuItemConstructLightCone"));
    
    menuItemConstructHyperbola = makeMenuItem("","hyperbola", bundle.getString("menuItemConstructHyperbola.hint"), bundle.getString("menuItemConstructHyperbola"));
    
    menuItemConstructIntervalTo = makeMenuItem("","interval", bundle.getString("menuItemConstructIntervalTo.hint"), bundle.getString("menuItemConstructIntervalTo"));
    
    menuItemRemoveEvent = makeMenuItem("","delete event", bundle.getString("menuItemRemoveEvent.hint"), bundle.getString("menuItemRemoveEvent"));
    
    menuConstructE.add(menuItemConstructLightCone);
    menuConstructE.add(menuItemConstructHyperbola);
    menuConstructE.add(menuItemConstructIntervalTo);
    popupOverEvent.add(menuConstructE);
    popupOverEvent.add(menuItemRemoveEvent);
    
    
    popupOverDecoration = new JPopupMenu();
    pnlDiagram.add(popupOverDecoration);
    menuItemDecorationDelete = makeMenuItem("", "delete decoration", bundle.getString("menuItemDecorationDelete.hint"), bundle.getString("menuItemDecorationDelete"));
    popupOverDecoration.add(menuItemDecorationDelete);
    
    
    printTimeInfo();
    
    
    //  everything added to content pane...
    cp = getContentPane();
    cp.removeAll();
    cp.setLayout(new BorderLayout());
    cp.add(pnlSouth,BorderLayout.SOUTH);
    cp.add(splitPaneT,BorderLayout.CENTER);
    
    
    cp.validate();
    cp.repaint();
    
    historyWriter = new HistoryWriter(sc);
    
  }


  protected JMenuItem makeMenuItem(String imageName, String actionCommand, String toolTipText, String altText) {
    //Look for the image.
    String imgLocation = "/resources/images/"+ imageName+ ".png";
    URL imageURL = SpacetimeApp.class.getResource(imgLocation);

    //Create and initialize the button.
    JMenuItem menuItem = new JMenuItem(altText);
    menuItem.setActionCommand(actionCommand);
    menuItem.setToolTipText(toolTipText);
    menuItem.addActionListener(this);

    if(imageName!="") {
      if (imageURL != null) {//image found
        menuItem.setIcon(new ImageIcon(imageURL, altText));
      } else {//no image found
        menuItem.setText(altText);
        System.err.println("Resource not found: "
            + imgLocation);
      }
    }

    return menuItem;
  }


  
  protected JButton makeButton(String imageName, String actionCommand, String toolTipText, String altText) {
    //Look for the image.
    String imgLocation = "/resources/images/"+ imageName+ ".png";
    URL imageURL = SpacetimeApp.class.getResource(imgLocation);

    //Create and initialize the button.
    JButton button = new JButton();
    button.setActionCommand(actionCommand);
    button.setToolTipText(toolTipText);
    button.addActionListener(this);

    if(imageName!="") {
      if (imageURL != null) {//image found
        button.setIcon(new ImageIcon(imageURL, altText));
      } else {//no image found
        button.setText(altText);
        System.err.println("Resource not found: "
            + imgLocation);
      }
    }
    else{
      button.setText(altText);
    }

    return button;
  }


  public void stepForward(){
    boolean eventFound=false;
    double tEnd = 0.1*Math.round(10*(t));
    if(tEnd<=t+1e-6) tEnd+=dt;
    double tMin=tEnd;
    
    for(int i=0; i<sc.events.size(); i++){
      STEvent tempE =(STEvent)(sc.events.get(i)); 
      double tpE=tempE.getTp();
      if(t<tpE && tpE<tEnd && tpE<tMin){
        tMin=tpE;
        eventFound=true;
      }
    }
    if(eventFound ) {
      t=tMin;//value for t just for now 
    }
    else t=tEnd;//value for t just for now
    
    //find all the events within TOLERANCE of t
    //and find the event with largest time within closeEvents
    //then set time to the time of that event
    double tMax = t;
    for(int i=0; i<sc.events.size(); i++){
      STEvent tempE =(STEvent)(sc.events.get(i)); 
      double tpE=tempE.getTp();
      if(Math.abs(t-tpE)<TOLERANCE){
        if(tpE>tMax) tMax=tpE;
      }
    }
    t=tMax;
    
    pnlDiagram.cY=t;
    printTimeInfo();
    repaint();
    historyWriter.continueWriting();
  }
  
  public void stepBack(){
    boolean eventFound=false;
    double tBeg = 0.1*Math.round(10*(t));
    if(tBeg>=t-1e-6) tBeg-=dt;
    double tMax=tBeg;
    
    for(int i=0; i<sc.events.size(); i++){
      STEvent tempE =(STEvent)(sc.events.get(i)); 
      double tpE=tempE.getTp();
      if(tBeg<tpE && tpE<t && tpE>tMax){
        tMax=tpE;
        eventFound=true;
      }
    }
    if(eventFound ) {
      t=tMax;//value for t just for now
    }
    else t=tBeg;//value for t just for now
    //find all the events within TOLERANCE of t
    //and find the event with largest time within closeEvents
    //then set time to the time of that event
    double tMin = t;
    for(int i=0; i<sc.events.size(); i++){
      STEvent tempE =(STEvent)(sc.events.get(i)); 
      double tpE=tempE.getTp();
      if(Math.abs(t-tpE)<TOLERANCE){
        if(tpE<tMin) tMin=tpE;
      }
    }
    t=tMin;
    
    pnlDiagram.cY=t;
    printTimeInfo();
    repaint();
    historyWriter.continueWriting();
  }
  
  
  public void actionPerformed(ActionEvent e) {
    if(e.getActionCommand().equals("step forward")){
      stepForward();
    }
    else if(e.getActionCommand().equals("step back")){
      stepBack();
    }
    else if(e.getActionCommand().equals("reset time")){
      t=0;
      pnlDiagram.cY=t;
      printTimeInfo();
      repaint();
      historyWriter.continueWriting();
      objectTable.updateTable();
    }
    else if(e.getActionCommand().equals("set time")){
      String response = JOptionPane.showInputDialog(bundle.getString("setTimeDialogPromptMessage"));
     
    
      
      try{
        t= (format9.parse(response)).doubleValue();
        pnlDiagram.cY=t;
        printTimeInfo();
        repaint();
        historyWriter.continueWriting();
        objectTable.updateTable();
      }
      catch(NullPointerException ex){
        //do nothing
      }
      catch (ParseException ex) {
        JOptionPane.showMessageDialog(this, bundle.getString("setTimeDialogParseExceptionMessage"));
        System.err.println("Bad input: " + response);
      }


      
      
    }
    else if(e.getActionCommand().equals("save")){
      int returnVal = fileChooser.showSaveDialog(this);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        File outFile = fileChooser.getSelectedFile();
        sc.saveToFile(outFile);
        String fileName = outFile.getName();
        if(!fileName.endsWith(".sce")) {
          fileName = fileName.concat(".sce");
        }
        this.setTitle("Spacetime - ".concat(fileName));
        
        
      } else {
        System.out.println("Save command cancelled by user.");
      }
    }
    else if(e.getActionCommand().equals("read")){
      if(historyWriter.history.size()==1){
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
          File inFile = fileChooser.getSelectedFile();
          sc.loadFromFile(inFile);
          this.setTitle(bundle.getString("spacetime").concat(inFile.getName()));
        } else {
          System.out.println("Load command cancelled by user.");
        }
      }
      else{
        int res = JOptionPane.showConfirmDialog(this, bundle.getString("saveConfirmationMessage"),bundle.getString("saveConfirmationMessageTitle"),JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        if(res == JOptionPane.NO_OPTION) {
          int returnVal = fileChooser.showOpenDialog(this);
          if (returnVal == JFileChooser.APPROVE_OPTION) {
            File inFile = fileChooser.getSelectedFile();
            sc.loadFromFile(inFile);
            this.setTitle("Spacetime - ".concat(inFile.getName()));
          } else {
            System.out.println("Load command cancelled by user.");
          }
        }
        else if(res == JOptionPane.YES_OPTION) {
          int returnVal = fileChooser.showSaveDialog(this);
          if (returnVal == JFileChooser.APPROVE_OPTION) {
            File outFile = fileChooser.getSelectedFile();
            sc.saveToFile(outFile);
          } else {
            System.out.println("Save command cancelled by user.");
          }

          returnVal = fileChooser.showOpenDialog(this);
          if (returnVal == JFileChooser.APPROVE_OPTION) {
            File inFile = fileChooser.getSelectedFile();
            sc.loadFromFile(inFile);
            this.setTitle(bundle.getString("spacetime").concat(inFile.getName()));
          } else {
            System.out.println("Load command cancelled by user.");
          }
        }
      }  
    }
    else if(e.getActionCommand().equals("new")){
      if(historyWriter.history.size()==1){
        sc.clearEverything();
        this.setTitle(bundle.getString("spacetimeUntitled"));
      }
      else{
        int res = JOptionPane.showConfirmDialog(this, bundle.getString("newConfirmationMessage") ,bundle.getString("newConfirmationMessageTitle"),JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if(res == JOptionPane.NO_OPTION) {
          sc.clearEverything();
          this.setTitle(bundle.getString("spacetimeUntitled"));
        }
        if(res == JOptionPane.YES_OPTION) {
          int returnVal = fileChooser.showSaveDialog(this);
          if (returnVal == JFileChooser.APPROVE_OPTION) {
            File outFile = fileChooser.getSelectedFile();
            sc.saveToFile(outFile);
          } else {
            System.out.println("Save command cancelled by user.");
          }
        }
      }
    }
    else if(e.getActionCommand().equals("quit")){
      quit();
    }
    else if(e.getActionCommand().equals("undo")){
      historyWriter.undo();
      repaint();
      objectTable.updateTable();
      eventTable.updateTable();
    }
    else if(e.getActionCommand().equals("redo")){
      historyWriter.redo();
      repaint();
      objectTable.updateTable();
      eventTable.updateTable();
    }
    else if(e.getActionCommand().equals("transform up")){
      double deltaBetap=0.1;
      double newBetaRel = (sc.getBetaRel()+deltaBetap)/(1+sc.getBetaRel()*deltaBetap);  // velocity transform formula
      sc.setBetaRel(newBetaRel);
      repaint();
      historyWriter.continueWriting();
      objectTable.updateTable();
      eventTable.updateTable();
    }
    else if(e.getActionCommand().equals("transform down")){
      double deltaBetap=-0.1;
      double newBetaRel = (sc.getBetaRel()+deltaBetap)/(1+sc.getBetaRel()*deltaBetap);  // velocity transform formula
      sc.setBetaRel(newBetaRel);
      repaint();
      historyWriter.continueWriting();
      objectTable.updateTable();
      eventTable.updateTable();
    }
    else if(e.getActionCommand().equals("original frame")){
      sc.setBetaRel(0);
      repaint();
      historyWriter.continueWriting();
      objectTable.updateTable();
      eventTable.updateTable();
    }
    else if(e.getActionCommand().equals("clock")){
      pnlHighway.createObject(pnlHighway.popupCurX, pnlHighway.popupCurY, "clock");
      repaint();
      historyWriter.continueWriting();
      objectTable.updateTable();
    }
    else if(e.getActionCommand().equals("flash")){
      pnlHighway.createObject(pnlHighway.popupCurX, pnlHighway.popupCurY, "flash");
      repaint();
      historyWriter.continueWriting();
      objectTable.updateTable();
    }
    else if(e.getActionCommand().equals("jump")){
      pnlHighway.jumpToObject(pnlHighway.popupObjectOver);
      repaint();
      historyWriter.continueWriting();
      objectTable.updateTable();
      eventTable.updateTable();
    }
    else if(e.getActionCommand().equals("program")){
      //we turn off the programmed state in all the objects
      for(int i=0; i<sc.objects.size();i++){
        ((STObject)sc.objects.get(i)).setProgrammed(false);
      }
      pnlHighway.popupObjectOver.setProgrammed(true);
      pnlHighway.popupObjectOver.setHasTermination(false);
      
      repaint();
      historyWriter.continueWriting();
      objectTable.updateTable();
      eventTable.updateTable();
    }
    else if(e.getActionCommand().equals("delete object")){
      pnlHighway.removeObject(pnlHighway.popupObjectOver);
      repaint();
      historyWriter.continueWriting();
      objectTable.updateTable();
      eventTable.updateTable();
    }
    else if(e.getActionCommand().equals("delete decoration")){
      sc.removeDecoration(pnlDiagram.popupDecorationOver);
      repaint();
      historyWriter.continueWriting();
    }
    else if(e.getActionCommand().equals("lightcone")){
      STDiagramDecoration d = new STLightCone(sc,pnlDiagram.popupEventOver);
      sc.addDecoration(d);
      repaint();
      historyWriter.continueWriting();
    }
    else if(e.getActionCommand().equals("hyperbola")){
      STDiagramDecoration d = new STHyperbola(sc,pnlDiagram.popupEventOver);
      sc.addDecoration(d);
      repaint();
      historyWriter.continueWriting();
    }
    else if(e.getActionCommand().equals("interval")){
      pnlDiagram.ev1=pnlDiagram.popupEventOver;
      pnlDiagram.choosingSecondEvent=true;
      lblComment.setText(bundle.getString("clickOnAnotherEvent"));
      repaint();
      historyWriter.continueWriting();
    }
    else if(e.getActionCommand().equals("set birth")){
      pnlHighway.popupObjectOver.setHasBirth(true);
      repaint();
      historyWriter.continueWriting();
      objectTable.updateTable();
      eventTable.updateTable();
    }
    else if(e.getActionCommand().equals("set termination")){
      pnlHighway.popupObjectOver.setHasTermination(true);
      repaint();
      historyWriter.continueWriting();
      objectTable.updateTable();
      eventTable.updateTable();
    }
    else if(e.getActionCommand().equals("cancel birth")){
      pnlHighway.popupObjectOver.setHasBirth(false);
      repaint();
      historyWriter.continueWriting();
      objectTable.updateTable();
      eventTable.updateTable();
    }
    else if(e.getActionCommand().equals("cancel termination")){
      pnlHighway.popupObjectOver.setHasTermination(false);
      repaint();
      historyWriter.continueWriting();
      objectTable.updateTable();
      eventTable.updateTable();
    }
    else if(e.getActionCommand().equals("delete worldline")){
      pnlHighway.removeObject(pnlDiagram.popupObjectOver);//this is NOT mistake!
      repaint();
      historyWriter.continueWriting();
      objectTable.updateTable();
      eventTable.updateTable();
    }
    else if(e.getActionCommand().equals("construct event")){
      pnlDiagram.createEvent(pnlDiagram.popupCurX, pnlDiagram.popupCurY);
      repaint();
      historyWriter.continueWriting();
      eventTable.updateTable();
    }
    else if(e.getActionCommand().equals("construct intersection")){
      pnlDiagram.d1=(STObject)pnlDiagram.objectsOver.get(0);
      pnlDiagram.choosingSecondObject=true;
      lblComment.setText(bundle.getString("clickOnAnotherWorldline"));
      repaint();
      historyWriter.continueWriting();
      eventTable.updateTable();
    }
    else if(e.getActionCommand().equals("delete event")){
      pnlDiagram.removeEvent(pnlDiagram.popupEventOver);
      repaint();
      historyWriter.continueWriting();
      eventTable.updateTable();
    }
    else if(e.getActionCommand().equals("zoom in")){
      double x1Old = pnlHighway.sx1;
      double x2Old = pnlHighway.sx2;
      double x2New = x2Old - 0.1*(x2Old-x1Old);
      double x1New = x1Old + 0.1*(x2Old-x1Old);
      pnlDiagram.sx1=x1New;
      pnlDiagram.sx2=x2New;
      pnlHighway.sx1=x1New;
      pnlHighway.sx2=x2New;
      repaint();
      historyWriter.continueWriting();
    }
    else if(e.getActionCommand().equals("zoom out")){
      double x1Old = pnlHighway.sx1;
      double x2Old = pnlHighway.sx2;
      double x2New = x2Old + 1./12.*(x2Old-x1Old);
      double x1New = x1Old - 1./12.*(x2Old-x1Old);
      pnlDiagram.sx1=x1New;
      pnlDiagram.sx2=x2New;
      pnlHighway.sx1=x1New;
      pnlHighway.sx2=x2New;
      repaint();
      historyWriter.continueWriting();
    }
    else if(e.getActionCommand().equals("help")){
      helpFrame.setVisible(true);
    }
    else if(e.getActionCommand().equals("about")){
      //after selecting menu About->About this program
      JOptionPane.showMessageDialog(this, bundle.getString("aboutMessage").concat(sVersionDate) ,
          bundle.getString("aboutMessageTitle"),
          JOptionPane.INFORMATION_MESSAGE);
      repaint();
      historyWriter.continueWriting();
    }
  }
  
  public void quit(){
    if(historyWriter.history.size()==1){
      dispose();
      System.exit(0);
    }
    else{
      int res = JOptionPane.showConfirmDialog(this, bundle.getString("quitConfirmationMessage"),bundle.getString("quitConfirmationMessageTitle"),JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
      if(res == JOptionPane.NO_OPTION) {
        dispose();
        System.exit(0);
      }
      if(res == JOptionPane.YES_OPTION) {
        int returnVal = fileChooser.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
          File outFile = fileChooser.getSelectedFile();
          sc.saveToFile(outFile);
        } else {
          System.out.println("Save command cancelled by user.");
        }
      }
    }
  }

 
  public void keyPressed(KeyEvent evt){
    int key = evt.getKeyCode();  // keyboard code for the pressed key
    
    if (key == KeyEvent.VK_LEFT) {  // shift everything to the left
      double x1Old = pnlHighway.sx1;
      double x2Old = pnlHighway.sx2;
      double x2New = x2Old + 0.05*(x2Old-x1Old);
      double x1New = x1Old + 0.05*(x2Old-x1Old);
      pnlDiagram.sx1=x1New;
      pnlDiagram.sx2=x2New;
      pnlHighway.sx1=x1New;
      pnlHighway.sx2=x2New;
      repaint();
      historyWriter.continueWriting();
    }
    else if (key == KeyEvent.VK_RIGHT) {  // shift everything to the right
      double x1Old = pnlHighway.sx1;
      double x2Old = pnlHighway.sx2;
      double x2New = x2Old - 0.05*(x2Old-x1Old);
      double x1New = x1Old - 0.05*(x2Old-x1Old);
      pnlDiagram.sx1=x1New;
      pnlDiagram.sx2=x2New;
      pnlHighway.sx1=x1New;
      pnlHighway.sx2=x2New;
      repaint();
      historyWriter.continueWriting();
    }
    else if (key == KeyEvent.VK_UP) {  // shift time one step forward
      if(!shiftPressed){
	stepForward();
        repaint();
        historyWriter.continueWriting();
      }
      else {
        double deltaBetap=0.1;
        double newBetaRel = (sc.getBetaRel()+deltaBetap)/(1+sc.getBetaRel()*deltaBetap);  // velocity transform formula
        sc.setBetaRel(newBetaRel);
        repaint();
        historyWriter.continueWriting();
        objectTable.updateTable();
        eventTable.updateTable();
      }
      
    }
    else if (key == KeyEvent.VK_DOWN) {  // shift time one step forward
      if(!shiftPressed){
        stepBack();
        repaint();
        historyWriter.continueWriting();
      }
      else {
        double deltaBetap=-0.1;
        double newBetaRel = (sc.getBetaRel()+deltaBetap)/(1+sc.getBetaRel()*deltaBetap);  // velocity transform formula
        sc.setBetaRel(newBetaRel);
        repaint();
        historyWriter.continueWriting();
        objectTable.updateTable();
        eventTable.updateTable();
      }
    }
    else if (key == KeyEvent.VK_SHIFT) {  // shift 
      shiftPressed=true;
    }
  }
  
  public void keyReleased(KeyEvent evt){
    int key = evt.getKeyCode();  // keyboard code for the pressed key
    if(key == KeyEvent.VK_SHIFT) shiftPressed=false;
  }
  
  public void keyTyped(KeyEvent evt){
    
  }
  
  
  public void printObjectInfo(STObject d){
    double eps = 1e-6;
    lblComment.setText(d.getLabel().concat(":   x = ").concat(format2.format(0.01*Math.round(100*d.getXpAtTp(t)))).concat("    \u03B2 = ".concat(format4.format(0.0001*Math.round(10000*d.getBetaP(t+eps))))).concat("    \u03B3 = ".concat(gammaToString(d.getGammaP(t+eps)))).concat(" . . . ").concat(d.getNote()));
  }
  
  public void printEventInfo(STEvent ev){
    lblComment.setText(ev.getLabel().concat(":   x = ").concat(format2.format(0.01*Math.round(100*ev.getXp()))).concat("    t = ").concat(format2.format(0.01*Math.round(100*ev.getTp()))).concat(" . . . ").concat(ev.getNote()));
  }
  
  public void printTimeInfo(){
    String info =bundle.getString("timeInfo").concat(format3.format(0.001*Math.round(1000*t))).concat(bundle.getString("keyboardInfo")) ; 
    lblTime.setText(info);
    lblTime.setToolTipText(info);
  }

  public String gammaToString(double gammap){
    Double g = new Double(gammap);
    if(g.equals(new Double(Double.POSITIVE_INFINITY))){
      return "\u221e";
    }
    else return format4.format(0.0001*Math.round(10000*gammap));
  }

  public void focusGained(FocusEvent arg0) {
    repaint();
  }


  public void focusLost(FocusEvent e) {
    repaint();
  }
  

  /**
   * @param args
   */
  public static void main(String[] args) {
    //Apple global menu
    System.setProperty("apple.laf.useScreenMenuBar", "true");
    
    SpacetimeApp frame = new SpacetimeApp();
    frame.setSize(1000,700);
    frame.initialize();
    frame.setVisible(true);
  }
  
  
}
