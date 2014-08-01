sealed trait TorrentMetadata {
  def announceUrls: List[String]
  def pieceLength: Int
  def pieces: Array[Byte]
  def infoHash: String
}

case class SingleFileTorrentMetadata(filename: String, length: Int, announceUrls: List[String], pieceLength: Int, pieces: Array[Byte], infoHash: String) extends TorrentMetadata
case class MultiFileTorrentMetadata(dirname: String, files: List[FileInfo], announceUrls: List[String], pieceLength: Int, pieces: Array[Byte], infoHash: String) extends TorrentMetadata

case class FileInfo(length: Int, path: List[String])
