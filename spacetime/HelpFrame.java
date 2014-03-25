package spacetime;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

public class HelpFrame extends JFrame implements HyperlinkListener{
  JEditorPane editorPane;
  JScrollPane scrollPane;
  java.net.URL helpURL;
  SpacetimeApp app;
  
  public HelpFrame(SpacetimeApp app){
    this.app = app;
    String loc = app.defaultLocale.toString();
    helpURL = SpacetimeApp.class.getResource("/resources/html/index_".concat(loc).concat(".html"));
    editorPane = new JEditorPane();
    editorPane.setEditable(false);
    editorPane.addHyperlinkListener(this);
    try {
      editorPane.setPage(helpURL);
    }
    catch (IOException e) {
      System.err.println("Attempted to read a bad URL. Reading English Help instead.");
      
      try{
        editorPane.setPage(SpacetimeApp.class.getResource("/resources/html/index_en_US.html"));
      }
      catch(IOException ee){
        System.err.println("English Help not found . . .");
      }
    }

    
    scrollPane = new JScrollPane(editorPane);
    editorPane.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    add(scrollPane, BorderLayout.CENTER);
    
    setTitle(app.bundle.getString("helpFrameTitle"));
    setSize(800,600);
    this.setLocation(new Point(100,100));
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    // Closing is taken care of by the following WindowListener
    addWindowListener( new WindowAdapter() {
      // This window listener responds when the user
      // clicks the window's close box by giving the
      // user a chance to change his mind.
      public void windowClosing(WindowEvent evt) {
        setVisible(false);
      }
    } );
  }

  
  public void hyperlinkUpdate(HyperlinkEvent e) {
    if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
      JEditorPane pane = (JEditorPane) e.getSource();
      if (e instanceof HTMLFrameHyperlinkEvent) {
        HTMLFrameHyperlinkEvent evt = (HTMLFrameHyperlinkEvent) e;
        HTMLDocument doc = (HTMLDocument) pane.getDocument();
        doc.processHTMLFrameHyperlinkEvent(evt);
      }
      else {
        try {
          pane.setPage(e.getURL());
        }
        catch (Throwable t) {
          t.printStackTrace();
        }
      }
    }
  }
}
