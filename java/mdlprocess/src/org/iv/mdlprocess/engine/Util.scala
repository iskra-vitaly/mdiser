package org.iv.mdlprocess.engine

object Util {
  /**
   * a/b ratio
   */
  case class Ratio(val a:Int, val b:Int) extends Pair(a, b) with Ordered[Ratio] {
    import Math.signum
    
    assume(a != 0 || b != 0)

    override def compare(that:Ratio) = (a*that.b - that.a*b)*(signum(b*that.b) match {
      case s if (s>0)=> 1
      case s if (s<0)=> -1
      case 0 => (signum(b), signum(that.b)) match {
        case (0, 0) => this.a compare that.a
        case (0, sb2) => signum(a)
        case (sb1, 0) => -signum(that.a)
      }
    })
    
    override def equals(obj:Any) = obj match {
      case that:Ratio=>(this compare that) == 0
      case _=> false
    }
    
    def inverse = Ratio(b, a)
    def * (that:Ratio) = Ratio(this.a*that.a, this.b*that.b)
    def / (that:Ratio) = Ratio(this.a*that.b, this.b*that.a)
    def + (that:Ratio) = Ratio(this.a*that.b+that.a*this.b, this.b*that.b)
    def - (that:Ratio) = this + Ratio(-that.a, -that.b)
    def * (that:Int) = Ratio(a*that, b)
    def / (that:Int) = Ratio(a, b*that)
    def + (that:Int) = Ratio(a+that*b, b)
    def - (that:Int) = this + (-that)
    def toFloat = a.asInstanceOf[Float]/b;
    def toDouble = a.asInstanceOf[Double]/b;
  }
  
  implicit def ratio2Double(r:Ratio) = r.toDouble
  implicit def ratio2Float(r:Ratio) = r.toFloat
  
  def forceRatio[T](desiredWH:Ratio, a:Array[Array[T]], filler:T):Array[Array[T]] = {
    val aheight = a.length
    val awidth = a(0).length
    val ratioWH = Ratio(awidth, aheight)
    val appendColsRows:Pair[Int, Int] = if (desiredWH > ratioWH) 
      (Math.round((desiredWH*ratioWH.b-ratioWH.a)/2), 0)
    else 
      (0, Math.round((ratioWH.a*desiredWH.inverse - ratioWH.b)/2))
    val res = Array[Array[T]]()
    val w = ratioWH.a + appendColsRows._1*2;
    val h = ratioWH.b + appendColsRows._2*2;
    val resRows = for (val i <- 0 until h) 
      yield (for (val j <- 0 until w) 
        yield if (i<appendColsRows._2 
                  || i>=appendColsRows._2+ratioWH.b 
                  || j<appendColsRows._1 
                  || j>=appendColsRows._1+ratioWH.a) filler
              else a(i-appendColsRows._2)(j-appendColsRows._1)).toArray;
          
    resRows.toArray
  }
  
  def main(args:Array[String]) {
    val r12 = Ratio(1, 2)
    val r20 = Ratio(2, 0)
    val r34 = Ratio(3, 4)
    
    assume(r12 < r34)
    assume(r12 == Ratio(3, 6))
    assume(r34 > r12)
    assume(r20 > r12)
    assume(r12*5 == Ratio(30, 12))
    assume(!(r12*5 != Ratio(30, 12)))
    
    val m = Array(Array(1, 2), Array(3, 4))
    val res = forceRatio(Ratio(2, 5), m, 0)
    res.foreach(row=>{row.foreach(x=>print(x+" ")); println})
  }
}
