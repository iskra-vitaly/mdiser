


import java.io._
import java.util.zip._

object ZCat {
  def main(args:Array[String]) {
    val ins = new FileInputStream("/media/e/bseu/test_ba.zip")
    val in = new ZipInputStream(ins)
    in.
    var entry = in.getNextEntry()
    while(entry != null) {
      println(entry.getName)
      entry = in.getNextEntry
    }
    ins.close
  }
}