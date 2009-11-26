package org.iv.mdlprocess.engine

import java.awt.image.BufferedImage;

object ProcessFileEx extends ProcessFile {
  import ProcessFile.{Figure, Params}
  def fullRender(figure:Figure, params:Params):(BufferedImage, Array[Array[Array[Float]]]) = {
    import Math.round
    
    var pts = new Array[Array[Double]](figure.points.getLength)
    for (val i <- 0 until figure.points.getLength)
      pts(i) = Array(figure.points.getX(i), figure.points.getY(i), figure.points.getZ(i), 1)
    val pointsTransformed = new Array3D(params.xform(pts))
    
    val renderer = new JRenderer(pointsTransformed, figure.meshes)
    renderer.ensureNornamls()
    renderer.renderBuffer(params.w, params.h)
    val cx = round( (params.crop.getX()*params.w).asInstanceOf[Float] )
    val cy = round( (params.crop.getY()*params.h).asInstanceOf[Float] )
    val cw = round( (params.crop.getWidth()*params.w).asInstanceOf[Float] )
    val ch = round( (params.crop.getHeight()*params.h).asInstanceOf[Float] )
    
    (renderer.createImage(cx, cy, cw, ch), renderer.getNormals(cx, cy, cw, ch))
  }
}
