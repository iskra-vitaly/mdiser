package org.iv.mdlprocess

import java.io._
import engine._
import ProcessFileEx.{NormProj, Normals}

object Norm2Octa {
  object SubjectDirFilter extends FileFilter {
    override def accept(file:File) = file.isDirectory && file.getName.matches("""\d{2}""")
  } 

  object NormalsFilter extends FileFilter {
    override def accept(file:File) = file.getName.endsWith(".norm")
  } 
  
  @throws(classOf[IOException])
  def saveNormOctave(norm:Normals, axis:Int, dir:File) {
    val axisId = axis match {
      case 0 => "normX"
      case 1 => "normY"
      case 2 => "normZ"
    }
    val out = new PrintWriter(new BufferedWriter(new FileWriter(new File(dir, axisId+".octa"))))
    try {
      norm(axis) foreach {row=>out.println(row.mkString(" ")+";")}
    } finally {
      out.close()
    }
  }
  
  @throws(classOf[IOException])
  def processSubject(dir:File) {
    println("Processing "+dir+"...")
    val subject = dir.getName
    val normFiles = dir.listFiles(NormalsFilter).toList.sort(fileSorter)
    normFiles foreach {
      f=>{
        val normals = Batch.readNormals(f)	
        val octDir = new File(dir, f.getName.replaceAll(".norm$", ".mat"));
        println(f.getName+"-->"+octDir.getName)
        0 to 2 foreach {saveNormOctave(normals.get, _, octDir)} 
      }
    }
  }
  
  def fileSorter(f1:File, f2:File) = f1.getName < f2.getName
  
  @throws(classOf[IOException])
  def processDir(dir:File) = dir.listFiles(SubjectDirFilter).toList.sort(fileSorter).foreach(processSubject)
  
  def main(args:Array[String]) {
    processDir(new File("/media/e/VDiser/ws/tmp/gavab"))
  }
}
