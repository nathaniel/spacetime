package spacetime;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;


public class ObjectTable extends javax.swing.JComponent implements TableModelListener, MouseListener, MouseMotionListener {
  
  JTable table;
  MyTableModel model;
  Scenario sc;
  TableCellRenderer tcr ;
  
  
  public ObjectTable(Scenario sc){
    this.sc=sc;
    model = new MyTableModel();
    table = new JTable(model);
    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    
    //  Create the scroll pane and add the table to it.
    JScrollPane scrollPane = new JScrollPane(table);
    
    model.addTableModelListener(this);
    //table.setAutoCreateRowSorter(true);
    
    tcr = new ColoredTableCellRenderer();
    
    
    
    //Add the scroll pane to this panel.
    setLayout(new BorderLayout());
    add(scrollPane, BorderLayout.CENTER);
    
    setPreferredSize(new Dimension(0,0));
    
    table.setCellSelectionEnabled(false);
    table.setRowSelectionAllowed(true);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
    
    table.addMouseListener(this);
    table.addMouseMotionListener(this);
    

    updateTable();
  }

  
  
  

  public void updateTable(){
    model.fireTableStructureChanged();
    //  here we set custom column widths
    TableColumn column = null;
    for (int i = 0; i < model.getColumnCount(); i++) {
      column = table.getColumnModel().getColumn(i);
      
      column.setCellRenderer(tcr);
      if (i == 0) {
        column.setPreferredWidth(100);
      }
      else if (i == 5) {
        column.setPreferredWidth(250); 
      } 
      else if(i == 4){
        column.setPreferredWidth(100);
      }
      else column.setPreferredWidth(50);
    }
   
  }
  
  public double parseDouble(String s) throws ParseException{
    DecimalFormat df = (DecimalFormat)NumberFormat.getNumberInstance(sc.app.defaultLocale);
    double x=0;
    x = (df.parse(s)).doubleValue();
    return x;
  }
  
  public int parseInt(String s) throws ParseException{
    DecimalFormat df = (DecimalFormat)NumberFormat.getNumberInstance(sc.app.defaultLocale);
    int x=0;
    x = (df.parse(s)).intValue();
    return x;
  }
  
  
  public void tableChanged(TableModelEvent e) {
  
  }

  
  public void mouseClicked(MouseEvent e) {
    Point p = e.getPoint();
    int row = table.rowAtPoint(p);
    for(int i=0; i<sc.getObjectsCount(); i++){
      sc.getObject(i).setHighlighted(false);
    }
    sc.getObject(row).setHighlighted(true);
    //we turn off the programmed state in all the objects except the current one
    for(int i=0; i<sc.objects.size();i++){
      STObject di = ((STObject)sc.objects.get(i));
      if(!di.equals(sc.getObject(row))) di.setProgrammed(false);
    }
    sc.app.repaint();
    //NO sc.app.historyWriter.continueWriting();
  }

  public void mouseEntered(MouseEvent e) {}

  public void mouseExited(MouseEvent e) {
    if(!sc.app.popupOverObject.isVisible()){
      for(int i=0; i<sc.getObjectsCount(); i++){
        sc.getObject(i).setHighlighted(false);
      }
      sc.app.repaint();
      //NO sc.app.historyWriter.continueWriting();
    }
  }

  public void mousePressed(MouseEvent e) {
    if(e.isPopupTrigger()) {
      
      int row = table.rowAtPoint(e.getPoint());
      STObject d = (STObject)sc.objects.get(row);
      d.setHighlighted(true);
      for(int i=0; i<sc.objects.size(); i++){
        STObject di = (STObject)sc.objects.get(i); 
        if(!d.equals(di)) di.setHighlighted(false);
      }
      sc.app.repaint();
      
      if(d.doesExist(sc.app.t)){
        sc.app.menuItemCancelTermination.setVisible(true);
        sc.app.menuItemCancelBirth.setVisible(true);
        sc.app.menuItemSetTermination.setVisible(true);
        sc.app.menuItemSetBirth.setVisible(true);
        sc.app.menuItemProgram.setVisible(true);
        sc.app.menuItemJump.setVisible(true);
        
        if(d.hasBirth()) {
          sc.app.menuItemCancelBirth.setVisible(true);
          sc.app.menuItemSetBirth.setVisible(false);
        }
        else{
          sc.app.menuItemCancelBirth.setVisible(false);
          sc.app.menuItemSetBirth.setVisible(true);
        }

        if(d.hasTermination()) {
          sc.app.menuItemCancelTermination.setVisible(true);
          sc.app.menuItemSetTermination.setVisible(false);
        }
        else{
          sc.app.menuItemCancelTermination.setVisible(false);
          sc.app.menuItemSetTermination.setVisible(true);
        }
      }
      else{
        sc.app.menuItemCancelTermination.setVisible(false);
        sc.app.menuItemCancelBirth.setVisible(false);
        sc.app.menuItemSetTermination.setVisible(false);
        sc.app.menuItemSetBirth.setVisible(false);
        sc.app.menuItemProgram.setVisible(false);
        sc.app.menuItemJump.setVisible(false);
      }
      
      sc.app.popupOverObject.show(e.getComponent(),e.getX(), e.getY());
      sc.app.pnlHighway.popupObjectOver=d;
      //we do not want to allow jumping to a photon
      String cl = d.getClass().toString();
      if(cl.endsWith("STFlash")) sc.app.menuItemJump.setEnabled(false);
      else sc.app.menuItemJump.setEnabled(true);
    }
  }

  public void mouseReleased(MouseEvent e) {
    mousePressed(e);
  }
  
  public void mouseDragged(MouseEvent e) {}

  public void mouseMoved(MouseEvent e) {
    if(!sc.app.popupOverObject.isVisible()){
      Point p = e.getPoint();
      int row = table.rowAtPoint(p);
      for(int i=0; i<sc.getObjectsCount(); i++){
        sc.getObject(i).setHighlighted(false);
      }
      sc.getObject(row).setHighlighted(true);
      sc.app.repaint();
      //NO sc.app.historyWriter.continueWriting();
    }
  }


  //inner class implementing custom TableModel
  class MyTableModel extends AbstractTableModel {
    private String[] columnNames = new String[]{sc.app.bundle.getString("objectTableObject"), sc.app.bundle.getString("objectTableX"), sc.app.bundle.getString("objectTableBeta"), sc.app.bundle.getString("objectTableGamma"), sc.app.bundle.getString("objectTableReading"), sc.app.bundle.getString("objectTableNotes") };
    
   
    public MyTableModel(){
      super();
    }
    
    public int getColumnCount() {
      return columnNames.length;
    }

    public int getRowCount() {
      int count =0;
      try{
        count = sc.getObjectsCount();
      }
      catch(java.lang.NullPointerException e){
        System.err.println(e.getMessage());
      }
      return count;
    }

    public String getColumnName(int col) {
      return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
      double eps = 1e-6;
      try{
        STObject d = sc.getObject(row);
        if(col==0){
          return d.getLabel();
        }
        else if(col==1){
          if(d.doesExist(sc.app.t)) return sc.app.format2.format(d.getXpAtTp(sc.app.t));
          else return "---";
        }
        else if(col==2){
          if(d.doesExist(sc.app.t)) return sc.app.format4.format(d.getBetaP(sc.app.t+eps));
          else return "---";
        }
        else if(col==3){
          if(d.doesExist(sc.app.t)) return sc.app.format4.format(d.getGammaP(sc.app.t+eps));
          else return "---";
        }
        else if(col==4){
          if(d.doesExist(sc.app.t)) return d.getReading(sc.app.t);
          else return "---";
        }
        else return d.getNote();
      }
      catch(java.lang.NullPointerException e){
        return "";
      }
      catch(java.lang.IndexOutOfBoundsException e){
        return "";
      }
    }

    public Class getColumnClass(int c) {
      try{
        return getValueAt(0, c).getClass();
      }
      catch(java.lang.IndexOutOfBoundsException e){
        return "".getClass();
      }
    }


    /*
     * Don't need to implement this method unless your table's
     * editable.
     */
    public boolean isCellEditable(int row, int col) {
      //Note that the data/cell address is constant,
      //no matter where the cell appears onscreen.
      if(col==4) return false;
      else {
        if(((STObject)sc.objects.get(row)).doesExist(sc.app.t)) {
          if(((STObject)sc.objects.get(row)).isProgrammed()) {
            if((col==3||col==2||col==0||col==5))return true;
            else return false;
          }
          else return true;
        }
        else{
          if(col==0 || col==5) return true;
          else return false;
        }
      }
    }

    /*
     * Don't need to implement this method unless your table's
     * data can change.
     */
    public void setValueAt(Object value, int row, int col) {
      try{
        if(isCellEditable(row,col)) {
          STObject d = sc.getObject(row);
          if(col==0) {//label
            d.setLabel((String)value);
            d.relabelAllBetaChangeEvents();
          }
          else if(col==1) {//xp
            double tp = sc.app.t;
            double betap = d.getBetaP(sc.app.t);
            double oldXp = d.getXpAtTp(sc.app.t);
            double xp;
            try{
              xp = parseDouble((String)value);
            }
            catch(ParseException e){
              System.err.println("ParseException:"+e.getMessage());
              xp = oldXp;
            }
            d.setXpTpBetap(xp, tp, betap);
            sc.app.pnlHighway.removeEventsOnDraggedWorldline(d);
          }
          else if(col==2){//betap
            double xp = d.getXpAtTp(sc.app.t);
            double tp = sc.app.t;
            double oldBetap = d.getBetaP(sc.app.t);
            double betap;
            try{
              betap = parseDouble((String)value);
            }
            catch(ParseException e){
              System.err.println("ParseException:"+e.getMessage());
              betap = oldBetap;
            }
            if(d.isProgrammed()) {
              d.addWorldlineRecord(tp, betap);
              d.setProgrammed(false);
            }
            else {
              d.setXpTpBetap(xp, tp, betap);
              sc.app.pnlHighway.removeEventsOnDraggedWorldline(d);
            }
            
            
          }
          else if(col==3){//gammap
            double xp = d.getXpAtTp(sc.app.t);
            double tp = sc.app.t;
            double oldBetap = d.getBetaP(sc.app.t);
            double oldGammap = d.getGammaP(sc.app.t);
            double gammap;
            try{
               gammap = parseDouble((String)value);
            }
            catch(ParseException e){
              System.err.println("ParseException:"+e.getMessage());
              gammap = oldGammap;
            }
            if(gammap<1) gammap=1;
            double betap = Math.sqrt(1-1/(gammap*gammap));
            if(oldBetap<0) betap=-betap; 
            if(d.isProgrammed()) {
              d.addWorldlineRecord(tp, betap);
              d.setProgrammed(false);
            }
            else {
              d.setXpTpBetap(xp, tp, betap);
              sc.app.pnlHighway.removeEventsOnDraggedWorldline(d);
            }
            
            
          }
          else if(col==5) d.setNote((String)value); //note
          
          
          sc.app.repaint();
          sc.app.historyWriter.continueWriting();
          fireTableCellUpdated(row, col);
        }
      }
      catch(java.lang.NullPointerException e){
        
      }
      
    }
  } 

  
  class ColoredTableCellRenderer extends DefaultTableCellRenderer {

    public ColoredTableCellRenderer(){
      super();
    }
    
    public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focused, int row, int column) {
      Component c = super.getTableCellRendererComponent(table, value, selected, focused, row, column);
      c.setEnabled(table == null || table.isEnabled());
      
      if(sc.getObject(row).isHighlighted()){
        c.setForeground(sc.getObject(row).HIGHLIGHTED_COLOR); 
      }
      else c.setForeground(null);
      
      if (column==4)
        c.setBackground(new Color(240,240,240));
      else{
        if(model.isCellEditable(row, column)) c.setBackground(null);
        else c.setBackground(new Color(240,240,240));
      }
      
      
      //if(selected) c.setBackground(table.getSelectionBackground());
      return c;
    }
  }




}


