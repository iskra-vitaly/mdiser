package org.iv.mdlprocess

import engine._
import ProcessFileEx.{Normals, NormProj}

object BatchScale {
  
  def floor(x:Float):Int = {
    import Math.{floor => mfloor, round => mround}
    mround(mfloor(x).asInstanceOf[Float])
  }
  
  type Triangle = Triple[Triple[Float, Float, Float], Triple[Float, Float, Float], Triple[Float, Float, Float]]
  def plainInterpol(x:Float, y:Float, tri:Triangle):Float = {
    val x1 = tri._1._1; val y1 = tri._1._2; val z1 = tri._1._3 
    val x2 = tri._2._1; val y2 = tri._2._2; val z2 = tri._2._3 
    val x3 = tri._3._1; val y3 = tri._3._2; val z3 = tri._3._3 
    val nx = y1*(z2 - z3) + y2*(z3 - z1) + y3*(z1 - z2)
    val ny = z1*(x2 - x3) + z2*(x3 - x1) + z3*(x1 - x2)
    val nz = x1*(y2 - y3) + x2*(y3 - y1) + x3*(y1 - y2)

    val znz = (x1-x)*nx+(y1-y)*ny+z1*nz;
    val z = znz/nz; 
    if (z == Float.NaN) z3 else z
  }
  
  def rescale(width:Int, height:Int)(chan:NormProj) = {
    val srcW = chan(0).length
    val srcH = chan.length
    val factorW = (srcW - 1f)/(width - 1f)
    val factorH = (srcH - 1f)/(height- 1f)
    val res = new Array[Array[Float]](height, width)
    
    def safeIndex(idx:Int, limit:Int) = Math.max(Math.min(idx, limit-1), 0)
    def safeSrcVal(x:Int, y:Int) = chan(safeIndex(y, srcH))(safeIndex(x, srcW)) 
      
    def interpolate(y:Float, x:Float):Float = {
      val x1 = floor(x)
      val y1 = floor(y)
      val x2 = x1+1
      val y2 = y1+1
      val zz = Array(
        Array(safeSrcVal(x1, y1), safeSrcVal(x1, y2)), 
        Array(safeSrcVal(x2, y1), safeSrcVal(x2, y2))
      )
      val sqcoord = (x - x1, y - y1)
      if (sqcoord._1 > sqcoord._2)
        plainInterpol(x, y, Triple(
          Triple(x1, y1, zz(0)(0)),
          Triple(x2, y1, zz(1)(0)),
          Triple(x2, y2, zz(1)(1))
        ))
      else
        plainInterpol(x, y, Triple(
          Triple(x1, y1, zz(0)(0)),
          Triple(x1, y2, zz(0)(1)),
          Triple(x2, y2, zz(1)(1))
        ))
    }
    
    //res(y)(x) = interpolate(y*factorH, x*factorW)
    for (y <- 0 until height) for (x <- 0 until width)
      res(y)(x) = interpolate(y*factorH, x*factorW)
    res
  }

  def rescaleAll(width:Int, height:Int)(n:Normals):Normals = n.map(rescale(width, height)) 
  
  def main(args:Array[String]) {
    val a = 1f;
    val b = 0f;
    val c = a/b;
    println(c)
    
    c match {
      case _ if c == Float.NaN => println("NAN matched")
      case _ => println("not NAN")
    }
  }
}