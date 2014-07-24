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

  private def getMetaData(dict: Map[BValue, BValue]) : TorrentMetadata = {
    val announceUrl = getAnnounceUrl(dict)

    val announceUrlList = getAnnounceUrlList(dict) // Use this later.

    val infoDict = getInfoDict(dict) match {
      case Success(d) => d
      case Failure(ex) => throw ex
    }

    val pieceLength = getPieceLength(infoDict)
    val pieces = getPieces(infoDict)
    val name = getName(infoDict)
    val length: Option[Int] = getSingleFileLength(infoDict)

    val torrentData: TorrentMetadata = length match {
      case Some(length) => SingleFileTorrentMetadata(name, length, announceUrl, pieceLength, pieces)
      case None => {
        val fileList = getMultiFileList(infoDict) match {
          case Some(f) => f
          case None => throw new RuntimeException("could not map input file to a valid torrent data file")
        }
        MultiFileTorrentMetadata(name, fileList, announceUrl, pieceLength, pieces)
      }
    }
    torrentData
  }

  private def getMultiFileList(info: Map[BValue, BValue]): Option[List[FileInfo]] = {
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

  private def getSingleFileLength(dict: Map[BValue, BValue]): Option[Int] = {
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


  private def getInfoDict(dict: Map[BValue, BValue]): Try[Map[BValue, BValue]] = {
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

  private def getName(dict: Map[BValue, BValue]): String = {
    dict.get(BStr("name")) match {
      case Some(value) => value match {
        case BStr(str) => str
      }
    }
  }


  // TODO: Implement me
  private def getPieceLength(dict: Map[BValue, BValue]): Int = {
    100
  }

  // TODO: Implement me
  private def getPieces(dict: Map[BValue, BValue]): String = {
    "abcabcabc"
  }

  private def getAnnounceUrlList(dict: Map[BValue, BValue]): List[String] = {
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

  private def getAnnounceUrl(dict: Map[BValue, BValue]): List[String] = {
    dict.get(BStr("announce")) match {
      case Some(value) => value match {
        case BStr(str) => List(str)
        case _ => List()
      }
      case _ => List()
    }
  }


}
