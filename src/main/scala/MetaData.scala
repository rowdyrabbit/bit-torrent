import scala.collection.immutable.ListMap
import scala.util.{Success, Try, Failure}

object MetaData {

  def parseTorrentFileStructure(structure: BValue): Try[TorrentMetadata] = {
    structure match {
      case BDict(dict) => {
        Try(getMetaData(dict))
      }
      case _ => Failure(new NoSuchElementException("could not find valid metadata in the decoded torrent file"))
    }
  }

  private def printPieces(bytes: Array[Byte]) = {
    //println("PIECES: " + new String(bytes))
//    for (b <- bytes) {
//      println("byte: " + b)
//    }
  }

  private def getMetaData(dict: ListMap[BValue, BValue]) : TorrentMetadata = {

    val announceUrlList = getAnnounceUrl(dict) ++ getAnnounceUrlList(dict)

    val infoDict = getInfoDict(dict) match {
      case Success(d) => d
      case Failure(ex) => throw ex
    }

    val infoDictStr = Bencode.encode(BDict(infoDict))


    val pieceLength = getPieceLength(infoDict)
    val pieces = getPieces(infoDict)
    printPieces(pieces)
    val name = getName(infoDict)

    val length: Option[Int] = getSingleFileLength(infoDict)

    val torrentData: TorrentMetadata = length match {
      case Some(length) => SingleFileTorrentMetadata(name, length, announceUrlList, pieceLength, pieces, infoDictStr)
      case None => {
        val fileList = getMultiFileList(infoDict) match {
          case Some(f) => f
          case None => throw new RuntimeException("could not map input file to a valid torrent data file")
        }
        MultiFileTorrentMetadata(name, fileList, announceUrlList, pieceLength, pieces, infoDictStr)
      }
    }
    torrentData
  }

  //TODO:
  //pattern matching on assignment
  //unapply

  private def getMultiFileList(info: ListMap[BValue, BValue]): Option[List[FileInfo]] = {
    info.get(BStr("files")) match {
      case Some(fileList) => {
        fileList match {
          case BList(l) => Some(getAllFileListInfo(fileList.asInstanceOf))
          case _ => None
        }
      }
      case _ => None
    }
  }

  def transformToFileInfo(file: BValue): FileInfo = {
    file match {
      case BDict(d) => {
        val length = d.get(BStr("length")) match {
          case Some(BInt(length)) => length
        }
        val filePath: List[String] = d.get(BStr("files")) match {
          case Some(BList(l)) => l.foldRight(List[String]()){(currStr, list) => currStr.asInstanceOf :: list}
        }
        FileInfo(length, filePath)
      }
    }
  }

  private def getAllFileListInfo(list: List[BValue]): List[FileInfo] = {
    list.foldRight(List[FileInfo]()){((currFile, fileList) => transformToFileInfo(currFile) :: fileList)}
  }

  private def getSingleFileLength(dict: ListMap[BValue, BValue]): Option[Int] = {
    dict.get(BStr("length")) match {
      case Some(length) => {
        length match {
          case BInt(i) => Some(i)
          case _ => None
        }
      }
      case _ => None
    }
  }


  private def getInfoDict(dict: ListMap[BValue, BValue]): Try[ListMap[BValue, BValue]] = {
    dict.get(BStr("info")) match {

      case Some(dict) => {
        dict match {
          case BDict(d) => Success(d)
          case _ => Failure(new NoSuchElementException("could not find an info value"))
        }
      }
      case None => Failure(new NoSuchElementException("could not find an info key"))
    }
  }

  private def getName(dict: ListMap[BValue, BValue]): String = {
    dict.get(BStr("name")) match {
      case Some(value) => value match {
        case BStr(str) => str
      }
    }
  }

  private def getPieceLength(dict: ListMap[BValue, BValue]): Int = {
    // Trying out a new way of pattern matching over the structure..
    val Some(BInt(pieceLength)) = dict.get(BStr("piece length"))
    pieceLength
  }

  private def getPieces(dict: Map[BValue, BValue]): Array[Byte] = {
    val Some(BStr(pieces)) = dict.get(BStr("pieces"))
    pieces.getBytes("UTF-8")
  }

  private def getAnnounceUrlList(dict: ListMap[BValue, BValue]): List[String] = {
    dict.get(BStr("announce-list")) match {
      case Some(value) => value match {
        case BList(urls) => urls.foldRight(List[String]()){(currVal, newList) =>  currVal match {
          case BStr(str) => str :: newList
          case _ => newList
        }}
        case _ => List()
      }
      case _ => List()
    }
  }

  private def getAnnounceUrl(dict: ListMap[BValue, BValue]): List[String] = {
    dict.get(BStr("announce")) match {
      case Some(value) => value match {
        case BStr(str) => List(str)
        case _ => List()
      }
      case _ => List()
    }
  }


}
