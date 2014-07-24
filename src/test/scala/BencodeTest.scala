import org.scalatest.FlatSpec
import org.scalatest.Matchers

class BencodeTest extends FlatSpec with Matchers {

  "The 'bdecode' function" should "return a nested map" in {
    val input = "d8:announce40:http://thomasballinger.com:6969/announce10:created by13:uTorrent/16405:otherd8:announce18:http://smh.com.au/ee".toCharArray

    val output = Bencode.decode(input)(0)

    output match {
      case BDict(dict) => {
        dict.get(BStr("other")) match {
          case Some(nestedMap) => {
            nestedMap match {
              case BDict(nestedMap) => {
                val ann = nestedMap.get(BStr("announce"))
                ann should be(Some(BStr("http://smh.com.au/")))
              }
          }
        }
      }
    }
  }}



}
