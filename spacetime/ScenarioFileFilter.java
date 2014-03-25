package spacetime;

import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.*;

public class ScenarioFileFilter extends FileFilter {

  //Accept all directories and all sce files.
  public boolean accept(File f) {
      if (f.isDirectory()) {
          return true;
      }

      String extension = getExtension(f);
      if (extension != null) {
          if (extension.equals("sce")) return true;
          else {
              return false;
          }
      }

      return false;
  }

  //The description of this filter
  public String getDescription() {
      return "*.sce";
  }
  
  
  private String getExtension(File f) {
    String ext = null;
    String s = f.getName();
    int i = s.lastIndexOf('.');

    if (i > 0 &&  i < s.length() - 1) {
      ext = s.substring(i+1).toLowerCase();
    }
    return ext;
  }
  
  
}