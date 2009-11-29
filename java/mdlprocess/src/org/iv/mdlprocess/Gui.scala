package org.iv.mdlprocess

import javax.swing._;
import filechooser.FileFilter;
import java.io.{File, FilenameFilter, BufferedOutputStream, FileOutputStream};
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
    showFileGui(files.toList.sort {(f1,f2)=>f1.getName<f2.getName} );
  }
  
  def createCloseFrame(frame:java.awt.Window) = {
    val closeFrame = new JFrame
    val closeButton = new JButton("close")
    closeButton.addActionListener(
      new ActionListener{override def actionPerformed(e:ActionEvent) = frame.dispose}
    )
    
    frame.addWindowListener(
      new WindowAdapter{override def windowClosed(e:WindowEvent) = closeFrame.dispose}
    );
    frame.pack
    frame
  }
  
  def showFileGui(files:List[File]):Unit = files match {
    case f :: tail => {
	  println("Processing "+f);
	  val figure = ProcessFile.readXmlFile(f);
	  val gui = createNewGui()(figure)
	  val frame = new JFrame;
	  val pane = frame.getContentPane()
	  pane.setLayout(new BorderLayout)
	  pane add gui.createDisplay
	  frame.pack
	  frame.setVisible(true);
	  frame.setLocation(500, 10);

	  val toolbar = RenderGUI.createToolFrame(gui)
	  toolbar.pack
	  toolbar.setVisible(true)

	  toolbar.addWindowListener(
			  new WindowAdapter{
				  override def windowClosed(e:WindowEvent) = {
					saveSettings(f, gui)
					frame.dispose()
					showFileGui(tail)
				  }

				  override def windowClosing(e:WindowEvent) = toolbar.dispose
			  }
	  )

	  frame.addWindowListener(new WindowAdapter{override def windowClosing(e:WindowEvent) = toolbar.dispose})
    }
    case Nil => println("done");
  } 
  
  def saveSettings(figureFile:File, gui:RenderGUI) {
    gui.saveParams(new File(figureFile.getAbsolutePath() + ".xform-params"))
    params = Some(gui.params)
  }
  
  var params:Option[ProcessFile.Params] = None;
  
  def createNewGui() = params match {
    case Some(p:ProcessFile.Params) => new RenderGUI(_:ProcessFile.Figure, p)
    case None => new RenderGUI(150, 200, _:ProcessFile.Figure)
  }
}
