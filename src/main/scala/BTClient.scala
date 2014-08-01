import java.net.URLEncoder
import java.security.MessageDigest
import scala.io.Source
import scala.util.{Failure, Success, Try}
import scalaj.http.Http

object BTClient {

  def main(args: Array[String]) {
    val bencodeContents = args.length match {
      case 0 => throw new Exception("You must provide a torrent file")
      case 1 => getBencodeFromFile(args(0))
    }

    //TODO: Connect to the tracker, and pull down the info we need to start connecting to peers etc.
    val response = for {
      data <- MetaData.parseTorrentFileStructure(bencodeContents)
      dataHash = hexStringURLEncode(bytes2hex(sha1(data.infoHash)))

      params = Map("port" -> "63211", "uploaded" -> "0", "downloaded" -> "0", "left" -> "1277987")
      peerIdParam = s"peer_id=${dataHash}"
      infoSHAParam = s"info_hash=${dataHash}"
      encodedParams = (for ((k, v) <- params) yield URLEncoder.encode(k) + "=" + URLEncoder.encode(v)).mkString("&")
      allParams =  s"?${infoSHAParam}&${peerIdParam}&${encodedParams}"
      (responseCode, headersMap, resultString) = Http.get(data.announceUrls.head + allParams).asHeadersAndParse(Http.readString)

    } yield (responseCode, headersMap, resultString)


    val output = response match {
      case Success(resp) => Bencode.decode(resp._3.toCharArray)
      case Failure(ex) => throw new RuntimeException("could not connect to tracker", ex)
    }
    println("response: " + output)

    val peers = output(0).asInstanceOf[BDict].map.get(BStr("peers"))
    peers match {
      case Some(BStr(p)) => parsePeers(p)
      case None => throw new RuntimeException("could not find any peers")
    }
  }

  def sha1(s: String): Array[Byte] = {
    MessageDigest.getInstance("SHA-1").digest(s.getBytes("ISO-8859-1"))
  }

  def parsePeers(peers: String) {
    val bytes = peers.getBytes.grouped(6).toList
    bytes
  }

  def hexStringURLEncode(x: String) = {
    x.grouped(2).toList.map("%" + _).mkString("")
  }

  def bytes2hex(bytes: Array[Byte], separator: Option[String] = None): String = {
    separator match {
       case None => bytes.map("%02x".format(_)).mkString
       case _ => bytes.map("%02x".format(_)).mkString(separator.get)
     }
   }

  private def getBencodeFromFile(filename: String): BValue = {
    readFile(filename)
  }

  private def readFile(filename: String): BValue = {
    val source = scala.io.Source.fromFile(filename, "ISO-8859-1")
    val torrentData = source.mkString
    source.close()
    Bencode.decode(torrentData.toCharArray)(0)
  }

//  def peersToIp(allPeers: String) = {
//    val peers = allPeers.getBytes.grouped(6).toList.map(_.map(0xFF & _))
//    peers.foreach(x => println(x.mkString(".")))
//    val ips = peers.map(x => x.slice(0, 4).mkString("."))
//    val ports = peers.map { x => (x(4) << 8) + x(5) } //convert 2 bytes to an int
//    ips zip ports
//  }

}
