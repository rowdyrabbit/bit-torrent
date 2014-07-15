import scala.io.Source

object TorrentClient {


  def main(args: Array[String]) {

    val filename = args(0)
    val torrentContents = readFile(filename)
    // Got the torrent file, now process it and do stuff with it.

  }

  private def readFile(filename: String): List[Any] = {

    val source = Source.fromFile(filename, "UTF-8")
    val is = source.reader()
    val bytes: Array[Char] = Stream.continually(is.read).takeWhile(-1 !=).map(_.toChar).toArray
    source.close()

    bdecode(bytes)
  }

  def bdecode(fileContents: Array[Char]): List[Any] = {
    loop(fileContents, List())._2
  }

  /**
   * The types used in this function are a bit gross. I think I need to implement separate data types for
   * each possible structure - like a dictionary, list, integer, string, rather than just have a Map of Strings to Any.
   *
   * @param fileContents
   * @param result
   * @return
   */
  def loop(fileContents: IndexedSeq[Char], result: List[Any]) : (IndexedSeq[Char], List[Any]) = {
    fileContents match {
      case s if (s.isEmpty) =>  (IndexedSeq[Char](), result)
      case ch =>  fileContents(0) match {
        case 'd' => {
          parseDictionary(fileContents, result)
        }
        case n if (n.isDigit) => {
          parseNumCharacters(fileContents, result)
        }
        case 'i' => {
          parseInteger(fileContents, result)
        }
        case 'l' => {
          parseList(fileContents, result)
        }
        case 'e' => (fileContents.drop(1), result)
      }
    }
  }

  def parseDictionary(fileContents: IndexedSeq[Char], result: List[Any]): (IndexedSeq[Char], List[Any]) = {
    val result2 = loop(fileContents.drop(1), List())
    val listOfVals = result2._2
    val map: Map[String, Any] = listOfVals.sliding(2, 2).collect { case List(a, b) => (a.toString, b)}.toMap

    loop(result2._1, result :+ map)
  }

  def parseNumCharacters(fileContents: IndexedSeq[Char], result: List[Any]): (IndexedSeq[Char], List[Any]) = {
    val strlen = fileContents.takeWhile(p => p != ':').mkString
    val (token, rest) = fileContents.drop(strlen.length + 1).splitAt(strlen.toInt)
    loop(rest, result :+ token.mkString)
  }

  def parseInteger(fileContents: IndexedSeq[Char], result: List[Any]): (IndexedSeq[Char], List[Any]) = {
    //if an int then just take everything until you reach the 'e' which signifies end of the int
    val intstr = fileContents.tail.takeWhile(p => p != 'e').mkString
    val (token, rest) = fileContents.drop(intstr.length + 2).splitAt(intstr.toInt)
    loop(fileContents.tail.splitAt(intstr.length + 1)._2, result :+ intstr.toInt)
  }

  def parseList(fileContents: IndexedSeq[Char], result: List[Any]): (IndexedSeq[Char], List[Any]) = {
    val result2 = loop(fileContents.drop(1), List())
    val listOfVals = result2._2
    loop(result2._1, result :+ listOfVals)
  }
}
