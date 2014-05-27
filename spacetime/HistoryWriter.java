package spacetime;

import java.util.LinkedList;
import java.util.Properties;

/**
 * This class takes care of the Undo/Redo history.
 */
public class HistoryWriter implements Runnable{

  LinkedList<Properties> history;//this will contain Properties objects
  Scenario sc;
  Thread historyThread;  
  int currentIndex;

  public HistoryWriter(Scenario sc){
    this.sc=sc;
    history = new LinkedList<Properties>();
    history.add(sc.getProperties());
    currentIndex = history.size()-1;
    sc.app.menuItemUndo.setEnabled(false);
    sc.app.menuItemRedo.setEnabled(false);

    start();
  }

  public void checkForChange(){
    Properties propLast = (Properties)history.getLast();
    Properties prop = sc.getProperties();
    if(!propLast.equals(prop)){
      history.add(prop);
      currentIndex = history.size()-1;
      if(currentIndex==1) sc.app.menuItemUndo.setEnabled(true);
    }

  }


  public void undo(){
    if(historyThread!=null) stop();

    if(currentIndex>0){
      currentIndex--;
      sc.reconstructProgramState((Properties)history.get(currentIndex));
      if(currentIndex==0) sc.app.menuItemUndo.setEnabled(false);
      if(currentIndex==history.size()-2) sc.app.menuItemRedo.setEnabled(true);
    }
  }

  public void redo(){
    if(historyThread!=null) stop();
    if(currentIndex<history.size()-1){
      currentIndex++;
      sc.reconstructProgramState((Properties)history.get(currentIndex));
      if(currentIndex==1) sc.app.menuItemUndo.setEnabled(true);
      if(currentIndex==history.size()-1) sc.app.menuItemRedo.setEnabled(false);
    }
  }

  public void start() {
    if (historyThread != null) return; //already running
    historyThread = new Thread(this);
    historyThread.start();
    System.out.println("History Writer Started...");
  }

  public void stop() {
    Thread tempThread = historyThread; //temporary reference
    historyThread = null; //signal the thread to stop
    if (tempThread != null) {
      try {
        tempThread.interrupt(); //get out of the sleep state
        tempThread.join(); //wait for the thread to die
      } catch (InterruptedException e) {
        
      }
    }
    sc.app.menuItemRedo.setEnabled(false);
    
  }

  public void continueWriting(){
    if(historyThread==null){
      //first remove all Properties from the currentIndex+1 to the end of list
      LinkedList<Properties> propToRemove=new LinkedList<Properties>();
      for(int i = currentIndex+1; i<history.size(); i++){
        propToRemove.add(history.get(i));
      }

      history.removeAll(propToRemove);

      start();
      

    }
  }


  public void run() {
    while (historyThread == Thread.currentThread()) {
      try {
        Thread.sleep(1000);

        checkForChange();
      } catch (InterruptedException e) {
        System.out.println("History Writer Interrupted...");
      }


    }


  }

}
