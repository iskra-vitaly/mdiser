package org.sc

object ImgTesting {
  def main(args:Array[String]):Unit = {
    import javax.imageio.ImageIO;
    ImageIO.getReaderFormatNames().foreach {println _}
    imgFloatTest()
    
  }
 
  def printMat(mat:Seq[Seq[Float]]) {
    mat foreach { row=> row foreach {format("%3.4f ", _)}; println(";") }
  }
  
  def unflatten(flat:Seq[Float], m:Int) = 
    for (i <- 0.until(flat.length, m) ) yield flat.slice(i, i+m)
  
  
  def imgFloatTest() {
    import java.awt.color.{ColorSpace}
    import java.awt.image._
    import java.awt.{Transparency}
    import java.awt.geom._
    
    val cs = ColorSpace.getInstance(ColorSpace.CS_GRAY)
    val cm = new ComponentColorModel(cs, false, true, Transparency.OPAQUE, DataBuffer.TYPE_FLOAT)
    println(cm)
    val raster = cm.createCompatibleWritableRaster(3, 2)
    val img = new BufferedImage(cm, raster, true, new java.util.Hashtable())
    raster.setDataElements(0, 0, 3, 2, Array(1f, 2f, 3f, 4f, 5f, 6f))
    println(img)
    val elems = raster.getDataElements(0, 0, 3, 2, null)
    var op = new AffineTransformOp(AffineTransform.getScaleInstance(1, 1), null)
    val r2 = op.createCompatibleDestRaster(raster)
    op.filter(raster, r2)
    r2.getDataElements(6, 4, null) match {
      case mat:Array[Float] => printMat(unflatten(mat, 6))
    }
  }
}
