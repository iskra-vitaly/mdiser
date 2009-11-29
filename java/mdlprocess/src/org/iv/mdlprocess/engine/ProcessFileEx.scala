package org.iv.mdlprocess.engine

import java.awt.image.BufferedImage;

object ProcessFileEx extends ProcessFile {
  import ProcessFile.{Figure, Params}
  import Util.{Ratio, forceRatio}
  import Math.round
  
  type NormProj = Array[Array[Float]]
  type Normals = Array[NormProj]
  
  
  def fullRender(figure:Figure, params:Params, ratioOption:Option[Ratio]):(BufferedImage, Normals) = {
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
    
    val normals = renderer.getNormals(cx, cy, cw, ch)
    ratioOption match {
      case Some(ratio) => {
        val paddedNormals = normals.map(forceRatio(ratio, _, 0f))
        (createImage(paddedNormals(2)), paddedNormals)
      }
      case None => (renderer.createImage(cx, cy, cw, ch),  normals)
    }
  }
  
  def createImage(chan: NormProj):BufferedImage = {
    val h = chan.length
    val w = chan(0).length
    val img = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY)
    val cspace = img.getColorModel.getColorSpace
    
    val colorMapper = JRenderer.getRGB(_:Float, cspace)
    
    for (i <- 0 until h) for (j <- 0 until w) 
      img.setRGB(j, i, colorMapper(chan(i)(j)))
    
    img
  }
}
