
class TorrentMetadata(announceUrls: List[String], pieceLength: Int, pieces: String)

case class SingleFileTorrentMetadata(filename: String, length: Int, announceUrls: List[String], pieceLength: Int, pieces: String) extends TorrentMetadata(announceUrls, pieceLength, pieces)
case class MultiFileTorrentMetadata(dirname: String, files: List[FileInfo], announceUrls: List[String], pieceLength: Int, pieces: String) extends TorrentMetadata(announceUrls, pieceLength, pieces)

case class FileInfo(length: Int, path: List[String])
