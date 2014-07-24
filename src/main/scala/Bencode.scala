import scala.io.Source

abstract class BValue
case class BInt(num: Int) extends BValue
case class BStr(str: String) extends BValue
case class BList(item: List[BValue]) extends BValue
case class BDict(map: Map[BValue, BValue]) extends BValue


object Bencode {


  def decode(fileContents: Array[Char]): List[BValue] = {
    loop(fileContents, List())._2
  }

  /**
   *
   * @param fileContents
   * @param result
   * @return
   */
  def loop(fileContents: IndexedSeq[Char], result: List[BValue]) : (IndexedSeq[Char], List[BValue]) = {
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

  def parseDictionary(fileContents: IndexedSeq[Char], result: List[BValue]): (IndexedSeq[Char], List[BValue]) = {
    val result2 = loop(fileContents.drop(1), List[BValue]())
    val listOfVals = result2._2
    val map = BDict(listOfVals.sliding(2, 2).collect { case List(a, b) => (a, b)}.toMap)

    loop(result2._1, result :+ map)
  }

  def parseNumCharacters(fileContents: IndexedSeq[Char], result: List[BValue]): (IndexedSeq[Char], List[BValue]) = {
    val strlen = fileContents.takeWhile(p => p != ':').mkString
    val (token, rest) = fileContents.drop(strlen.length + 1).splitAt(strlen.toInt)
    loop(rest, result :+ BStr(token.mkString))
  }

  def parseInteger(fileContents: IndexedSeq[Char], result: List[BValue]): (IndexedSeq[Char], List[BValue]) = {
    //if an int then just take everything until you reach the 'e' which signifies end of the int
    val intstr = fileContents.tail.takeWhile(p => p != 'e').mkString
    val (token, rest) = fileContents.drop(intstr.length + 2).splitAt(intstr.toInt)
    loop(fileContents.tail.splitAt(intstr.length + 1)._2, result :+ BInt(intstr.toInt))
  }

  def parseList(fileContents: IndexedSeq[Char], result: List[BValue]): (IndexedSeq[Char], List[BValue]) = {
    val result2 = loop(fileContents.drop(1), List[BValue]())
    val listOfVals = result2._2
    loop(result2._1, result ++ listOfVals)
  }
}
