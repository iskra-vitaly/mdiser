package org.iv.mdlprocess

import java.io._
import java.awt.geom.Rectangle2D;
import engine._
import Util.Ratio
import ProcessFile.{Figure, Params, Transform}
import ProcessFileEx.{createImage, Normals, NormProj}
import javax.imageio.ImageIO

object Batch {
  val fileNamePat = """(.*)\.xml\.xform-params""".r
  
  var w: Option[Int] = None
  var h: Option[Int] = None
  var steps:Int = 4
  var maxAngeDeg = +45.0
  
  val rotationDist = 0.15;
  
  var ratioOption:Option[Util.Ratio] = None
  
  var minWidth = Integer.MAX_VALUE
  var maxWidth = Integer.MIN_VALUE
  
  var fileNames:List[String] = Nil
  
  def processFile(figureFile:File, paramsFile:File, name:String):Unit={
    val in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(paramsFile)))
    try {
      in.readObject() match {
        case params:Params => {
          w.foreach {params.w=_}
          h.foreach {params.h=_}
          println("Processing "+figureFile+" with "+params);
          val dir = new File(figureFile.getParentFile(), name);
          fileNames = name :: fileNames
          processFile(ProcessFile.readXmlFile(figureFile),  params, dir)
        }
        case _ => println("Unexpected object saved in params")
      }
    } finally {
      in.close()
    }
  }
  
  def safeMatFiles(n:Normals, dir:File) {
    dir.mkdirs()
    saveMat(n(0), new File(dir, "x.mat"))
    saveMat(n(1), new File(dir, "y.mat"))
    saveMat(n(2), new File(dir, "z.mat"))
  }
  
  @throws(classOf[IOException])
  def saveMat(n:NormProj, file:File) {
    val out = new PrintWriter(new FileWriter(file))
    try {
      out.println("[")
      n.foreach(row=>{
        out.print(row.mkString(" "))
        out.println(";");
      })
      out.println("]")
    } finally {
      out.close
    }
  }
  
  def processFile(figure:Figure, params:Params, dir:File):Unit = {
    dir.mkdirs()
    val basexform  = params.xform
    val basecrop = params.crop
    for (i <- -steps to steps) {
      val angle:Double = maxAngeDeg*i/steps
      val alpha = angle*Math.Pi/180.0
      params.xform = basexform add Transform.getRotate(1, alpha)
      val cropCorrection = Math.sin(alpha)*rotationDist;
      val left = Math.max(basecrop.getX-cropCorrection, 0)
      val right = Math.min(left + basecrop.getWidth, params.w)
      params.crop = new Rectangle2D.Double(left, basecrop.getY, right-left, basecrop.getHeight)
      format("Rotation by %1.0f deg\n", angle)
      val imgData = ProcessFileEx.fullRender(figure, params, ratioOption)
      if (i==0) {
        val wh = (imgData._1.getWidth, imgData._1.getHeight)        
        if (wh._1 < minWidth) minWidth = wh._1
        if (wh._2 > maxWidth) maxWidth = wh._2
      }
      val fileName = String.format("%03.0f", new java.lang.Double(angle+90))
      javax.imageio.ImageIO.write(imgData._1, "jpeg", new File(dir, fileName + ".jpg"))
      saveNormals(imgData._2, new File(dir, fileName+".norm"))
    }
    params.xform = basexform
    params.crop = basecrop
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
      case _ => {}
    })
  }
  
  val normPat = """^(.*)\.norm$""".r
  def postProcessSubject(dir:File) {
    dir.listFiles filter {_.getName.endsWith(".norm")} foreach {f=>f.getName match {
      case normPat(name) => {
        println("PostProcessing "+dir.getName+"/"+name+"...")
        val norms = BatchScale.rescaleAll(200, 200*ratioOption.get.b/ratioOption.get.a)(readNormals(f).get)
        saveNormals(norms, f)
        safeMatFiles(norms, new File(dir, name+".mat"))
        var idx = 0
        val axises = Array('x'->norms(0), 'y'->norms(1), 'z'->norms(2))
        axises foreach {axis=>ImageIO.write(createImage(axis._2), "jpeg", new File(dir, name+"."+axis._1+".jpg"))} 
      }
      case _ => {}
    }}
  }
  
  def postProcessFolder(files:Seq[File]) =
    files filter {f=>(f.isDirectory && !f.getName.startsWith("."))} foreach postProcessSubject 
  
  def main(args:Array[String]):Unit = {
    val folder = args.toList match {
      case path :: tail => Some(new File(path))
      case _ => None
    }
    ratioOption = Some(Ratio(1, 1))
    folder.foreach(f=>{
      processFolder(f.listFiles)
      saveMinMaxWidth(new File(f, "minFrontalWidth.int"), new File(f, "maxFrontalWidth.int"))
      postProcessFolder(f.listFiles)
    })
  }
  
  @throws(classOf[IOException])
  def saveMinMaxWidth(minFile:File, maxFile:File) {
    saveTextData(minWidth, minFile)
    saveTextData(maxWidth, maxFile)
  }
  
  @throws(classOf[IOException])
  def saveTextData[T](x:T, file:File) {
    val out = new PrintWriter(new FileWriter(file))
    try {
      out.println(x)
    } finally {
      out.close()
    }
  }
  
  @throws(classOf[IOException])
  def readIntFile(file:File):Int = {
    val scanner = new java.util.Scanner(new FileReader(file))
    try {
      scanner.nextInt
    } finally {
      scanner.close()
    }
  }
}
	