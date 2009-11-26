package org.sc

object CastTest extends Application {
  val db:Double = 4e10
  val x = Math.round(db.asInstanceOf[Float])
  println(x)
}

  
