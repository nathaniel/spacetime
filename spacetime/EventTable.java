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


public class EventTable extends javax.swing.JComponent implements TableModelListener, MouseListener, MouseMotionListener {
  
  JTable table;
  MyTableModel model;
  Scenario sc;
  TableCellRenderer tcr ;
  
  
  public EventTable(Scenario sc){
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
    //here we set custom column widths
    TableColumn column = null;
    for (int i = 0; i < model.getColumnCount(); i++) {
      column = table.getColumnModel().getColumn(i);
      
      column.setCellRenderer(tcr);
      
      if (i == 0) {
        column.setPreferredWidth(100);
      }
      else if (i == 3) {
        column.setPreferredWidth(250); //one column is bigger
        
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
    for(int i=0; i<sc.getEventsCount(); i++){
      sc.getEvent(i).setHighlighted(false);
    }
    sc.getEvent(row).setHighlighted(true);
    sc.app.repaint();
    //NO app.historyWriter.continueWriting();
  }

  public void mouseEntered(MouseEvent e) {}

  public void mouseExited(MouseEvent e) {
    if(!sc.app.popupOverEvent.isVisible()){
      for(int i=0; i<sc.getEventsCount(); i++){
        sc.getEvent(i).setHighlighted(false);
      }
      sc.app.repaint();
      //NO app.historyWriter.continueWriting();
    }
  }

  public void mousePressed(MouseEvent e) {
    if(e.isPopupTrigger()) {

      int row = table.rowAtPoint(e.getPoint());
      STEvent ev = (STEvent)sc.events.get(row);
      ev.setHighlighted(true);
      for(int i=0; i<sc.events.size(); i++){
        STEvent evi = (STEvent)sc.events.get(i); 
        if(!ev.equals(evi)) evi.setHighlighted(false);
      }
      sc.app.repaint();
      
      sc.app.pnlDiagram.popupEventOver=ev;
      
      if(ev.getClass().toString().endsWith("STBetaChangeEvent")){
        sc.app.menuItemRemoveEvent.setVisible(false);
      }
      else{
        sc.app.menuItemRemoveEvent.setVisible(true);
      }
      sc.app.popupOverEvent.show(e.getComponent(),e.getX(), e.getY());
      
      
    }
  }

  public void mouseReleased(MouseEvent e) {
    mousePressed(e);
  }
  
  public void mouseDragged(MouseEvent e) {}

  public void mouseMoved(MouseEvent e) {
    if(!sc.app.popupOverEvent.isVisible()){
      Point p = e.getPoint();
      int row = table.rowAtPoint(p);
      for(int i=0; i<sc.getEventsCount(); i++){
        sc.getEvent(i).setHighlighted(false);
      }
      sc.getEvent(row).setHighlighted(true);
      sc.app.repaint();
      //NO app.historyWriter.continueWriting();
    }
  }


  //inner class implementing custom TableModel
  class MyTableModel extends AbstractTableModel {
    private String[] columnNames = new String[]{sc.app.bundle.getString("eventTableEvent"), sc.app.bundle.getString("eventTableX"), sc.app.bundle.getString("eventTableT"), sc.app.bundle.getString("eventTableNotes")};
    
   
    public MyTableModel(){
      super();
    }
    
    public int getColumnCount() {
      int count =0;
    
      try{
        count = columnNames.length;
      }
      catch(java.lang.NullPointerException e){
        System.err.println(e.getMessage());
      }
      return count;
    }

    public int getRowCount() {
      int count =0;
      try{
        count = sc.getEventsCount();
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
      try{
        STEvent ev = sc.getEvent(row);
        if(col==0){
          return ev.getLabel();
        }
        else if(col==1){
          return sc.app.format2.format(ev.getXp());
        }
        else if(col==2){
          return sc.app.format2.format(ev.getTp());
        }
        else {
          return ev.getNote();
        }
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
      if(col==0 || col==3) return true;
      else {
        STEvent ev = sc.getEvent(row);
        if(ev.isFixedAtIntersection()||ev.isPlacedAtWorldline()) return false;
        else return true;
      }
    }

    /*
     * Don't need to implement this method unless your table's
     * data can change.
     */
    public void setValueAt(Object value, int row, int col) {
      try{
        if(isCellEditable(row,col)) {
          STEvent ev = sc.getEvent(row);
          if(col==0) ev.setLabel((String)value);
          else if(col==1) {
            double tp = sc.app.t;
            double oldXp = ev.getXp();
            double xp;
            try{
              xp = parseDouble((String)value);
            }
            catch(ParseException e){
              System.err.println("ParseException:"+e.getMessage());
              xp = oldXp;
            }
            ev.setXpTp(xp, tp);
            
          }
          else if(col==2){
            double xp = ev.getXp();
            double oldTp = ev.getTp();
            double tp;
            try{
              tp = parseDouble((String)value);
            }
            catch(ParseException e){
              System.err.println("ParseException:"+e.getMessage());
              tp = oldTp;
            }
            ev.setXpTp(xp, tp);
          }
          else if(col==3) ev.setNote((String)value);
          
          
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
      
      if(sc.getEvent(row).isHighlighted()){
        c.setForeground(sc.getEvent(row).HIGHLIGHTED_COLOR); 
      }
      else c.setForeground(null);
      
      if (column <1  || column == 3)
        c.setBackground(null);
      else{
        if(model.isCellEditable(row, column)) c.setBackground(null);
        else c.setBackground(new Color(240,240,240));
      }
      return c;
    }
  }




}


