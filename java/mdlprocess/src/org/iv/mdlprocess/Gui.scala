package org.iv.mdlprocess

import javax.swing._;
import filechooser.FileFilter;
import java.io.File;

object Gui {
  def askFolder:File = {
    val dlg = new JFileChooser()
    dlg.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    dlg.showOpenDialog(null) match {
      case JFileChooser.APPROVE_OPTION=>dlg.getSelectedFile()
      case _ => askFolder 
    } 
  }
  
  def main(args : Array[String]) : Unit = {
    val folder = args.toList match {
      case List(path, _*)=>new File(path)
      case _ => askFolder
    }
    println(folder)
    
  }
}
