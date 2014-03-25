package spacetime;

import java.awt.Color;
import java.awt.Graphics2D;

public abstract class STDiagramDecoration {

  DrawingPanelDiagram pnlDiagram;
  Scenario sc;
   
  final Color HIGHLIGHTED_COLOR=new Color(132,24,168);
  final Color USUAL_COLOR=Color.pink;
  Color color=USUAL_COLOR;
  boolean highlighted=false;
  String name;
  STEvent ev, ev1, ev2;
  
  public STDiagramDecoration(Scenario sc){
    this.sc = sc;
    this.pnlDiagram = sc.app.pnlDiagram;
  }
  
 
  public abstract void drawInDiagram(Graphics2D g2);
  
  
  public abstract boolean isMouseOver(int iC, int jC);
  
  
  public void setName(String name){
    this.name = name;
  }
  
  public String getName(){
    return name;
  }
  
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
  
  
  
}
