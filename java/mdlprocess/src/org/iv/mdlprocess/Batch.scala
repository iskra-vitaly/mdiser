package org.iv.mdlprocess

import java.io._
import engine._;

object Batch {
  val fileNamePat = """(.*)\.xml\.xform-params""".r
  
  var w: Option[Int] = None
  var h: Option[Int] = None
  var steps:Int = 4;
  var maxAngeDeg = +45.0;
  
  import ProcessFile.{Figure, Params, Transform}
  
  def processFile(figureFile:File, paramsFile:File, name:String):Unit={
    val in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(paramsFile)));
    try {
      in.readObject() match {
        case params:Params => {
          w.foreach {params.w=_}
          h.foreach {params.h=_}
          println("Processing "+figureFile+" with "+params);
          val dir = new File(figureFile.getParentFile(), name); 
          processFile(ProcessFile.readXmlFile(figureFile),  params, dir)
        }
        case _ => println("Unexpected object saved in params")
      }
    } finally {
      in.close()
    }
  }
  
  def processFile(figure:Figure, params:Params, dir:File):Unit = {
    dir.mkdirs()
    for (i <- -steps to steps) {
      val angle:Double = maxAngeDeg*i/steps;
      val oldxform = params.xform
      params.xform = oldxform add Transform.getRotate(1, angle*Math.Pi/180.0)
      format("Rotation by %1.0f deg\n", angle)
      val imgData = ProcessFileEx.fullRender(figure, params)
      params.xform = oldxform
      val fileName = String.format("%1.0f", new java.lang.Double(angle))
      javax.imageio.ImageIO.write(imgData._1, "jpeg", new File(dir, fileName + ".jpg"))
      saveNormals(imgData._2, new File(dir, fileName+".norm"))
    }
    System.gc()
  }
  
  @throws(classOf[IOException])
  def saveNormals(normals:Array[Array[Array[Float]]], file:File) {
    val out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)))
    try {
      out.writeObject(normals)
    } finally {
      out.close()
    }
  }

  @throws(classOf[IOException])
  def readNormals(file:File) = readObject(file) match {
    case n:Array[Array[Array[Float]]] => Some(n)
    case _ => None
  }
  
  @throws(classOf[IOException])
  def readObject(file:File):AnyRef = {
    val in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)))
    try {
      return in.readObject()
    } finally {
      in.close()
    }
  }
  
  def processFolder(files:Seq[File]) {
    println(fileNamePat)
    files.foreach( f=>f.getName() match {
      case fileNamePat(name) => processFile(new File(f.getParent()+"/"+name+".xml"), f, name)
      case _ => println(f.getName()+" is not processed")
    })
  }
  
  def main(args:Array[String]):Unit = {
    val folder = args.toList match {
      case path :: tail => Some(new File(path))
      case _ => None
    }
    folder.foreach(f=>processFolder(f.listFiles));
  }
}
	