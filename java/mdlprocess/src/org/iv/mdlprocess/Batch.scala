package org.iv.mdlprocess

import java.io._
import engine._;

object Batch {
  val fileNamePat = """"(.*)\.xform-params""".r
  
  var w: Option[Int] = None
  var h: Option[Int] = None
  var count = 10;
  
  def processFile(figureFile:File, paramsFile:File):Unit={
    val in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(paramsFile)));
    try {
      in.readObject() match {
        case params:ProcessFile.Params => {
          w.foreach {params.w=_}
          h.foreach {params.h=_}
          println("Processing "+figureFile+" with "+params);
          ProcessFile.processXml(figureFile, params)
        }
        case _ => println("Unexpected object saved in params")
      }
    } finally {
      in.close()
    }
  }
  
  def processFolder(files:List[File]) {
    files.foreach( (f:File)=>f.getName() match {
      case fileNamePat(name) => processFile(f, new File(f.getParent()+"/"+name+".xml"))
      case _=>println(f+" is not processed");
    })
  }
  
  def main(args:Array[String]) {
    
  }
}
