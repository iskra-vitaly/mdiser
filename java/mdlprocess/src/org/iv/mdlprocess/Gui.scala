package org.iv.mdlprocess

import javax.swing._;
import filechooser.FileFilter;
import java.io.{File, FilenameFilter};
import java.awt.event._;
import java.awt.{BorderLayout}
import org.iv.mdlprocess.engine._;

object Gui {
  def askFolder:File = {
    val dlg = new JFileChooser("/media/e/VDiser/ws/dbs")
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
    val files = folder.listFiles(new FilenameFilter {
                                   def accept(dir:File, name:String):Boolean = {
                                     return name.endsWith(".xml");
                                   } 
                                 }); 
    processFiles(files.toList);
  }
  
  def processFiles(files: List[File]):Unit = files match {
    case f :: tail => {
      val windowListener = new WindowAdapter {
        override def windowClosing (e:WindowEvent) {
          e.getWindow().dispose();
        }
        
        override def windowClosed (e:WindowEvent) {
          processFiles(tail);
        }
      } 
      showFileGui(f, windowListener)
    }
    case Nil => println("Done") 
  }
  
  def showFileGui(f:File, l:WindowListener) {
    val figure = ProcessFile.readXmlFile(new File("/media/e/VDiser/Zhang.review/work/cara1_abajo.xml"));
    val gui = new RenderGUI(800, 600, figure)
    var frame = new JFrame;
    frame.addWindowListener(l)
    val pane = frame.getContentPane()
    pane.setLayout(new BorderLayout)
    pane add gui.createDisplay
    frame.pack();
    frame.setVisible(true);
    
    val toolbar = RenderGUI.createToolFrame(gui);
    toolbar pack;
    toolbar.setVisible(true)
    toolbar.addWindowListener(l);
  } 
}
