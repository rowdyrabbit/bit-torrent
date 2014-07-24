import scala.io.Source

object BTClient {

//  val input = "d8:announce40:http://thomasballinger.com:6969/announce10:created by13:uTorrent/16405:otherd8:announce18:http://smh.com.au/ee".toCharArray
  val input = "d8:announce40:http://thomasballinger.com:6969/announce10:created by13:uTorrent/164013:creation datei1350935447e8:encoding5:UTF-84:infod6:lengthi1277987e4:name8:flag.jpg12:piece lengthi16384e6:pieces1580:eñ›ÓGtﬁ{œ-”Ÿ\u0003ú√⁄ß©YáœwŸ\u0018ªAÕ1ßÌ•Ï\u0001UÜ¿ÕÒÔÏ¿=@_ì¶u√‰B≥è5usJ¢Äˇ\u0003∞\tˆÕMƒ\n™ç:¡˚˚Ê¶\u0001≥R†à\t,‰ÿıÂ\u0006Q\u000EµüAæÅ•œVUdU_üäŸŒÓN\u0004”¬LU∞\u001DQf\u0015\u0001´\u001D\u0014-¢1⁄`©\t/ô±¶ﬂê’#\u0007ﬁÓ[πq5≈¥˝\u0006‰?Œ-Ω¨}NFÿ\u0001+=á7x\u0018æ\\aíñ§®äMp‘,»@Oêqp\bl\u001BN®¯≈¥óôå+‡\ná\u000F„>(JŒDqKt¿úKgpÅ\t“ìÖum∑≠˘$ú'Xb8$˚6z\f õíyê\b\u000E(ñ(∫%\t\u007F‡JtBì\u0003ØzåL\f⁄\u001E˚.û|“,|\t&¬ÉaN¸|\u0013∆ûı?+\u001DÚF\u0014µ4÷T¢9·h%]Ï»Z‚∆œ=3O'SŸñŒvAª\u0016«!&vÈY\"¿rkV¨¡ıHÃ‹ux73®(Eÿ0'Ó\u0019PhE´\u007F÷0°–ÿ\u001F·æÚª\u001CêiL\u0000¬ÍQ\b-Î®É∆Öäé´Ó:∫\u00197ˇ,‚+eWqCB∞¡ñeGIµ/\u0013ª¡\"5ï‚\u001B¸á≠J”\u0002?x≥,TH{ãÊ!ƒC˝ÄZ∂;}≥\u0006sÜp»\u00165e,_ÒÆö\u0002Yì*À[\u001DE÷ä‡§£mÂaC®në9õÓ0£j æ®\u0016\u0013ÂK“0¥Ë√è\u0002\u0014RÌêÒt\u001E\u001A¢\u001Bœ^»E\u000E≈Æü˝Cﬁ)°=YcrDI%/£VÂî+π#˝\nâŸ\u0019\u001Ay$Æ÷L¸RPØˆ≠ÍôÎüå\u0011 x?wE\u0005\"ë\u0000@\u0017O’≤©È\u0003˘\u001DËÿ’µM±ßﬁ≥ó9\u0003úl±O≤óàµ{‰19âdk\n±ü¸g§\u0012%Få\u0019‹[÷˚ÙÌ\u0017•–b⁄·\u0017fYxˇ?ÓﬁQ\"ÚX&ˆ\u0014\u0004¨ª*\u0017d*√ı\\\u0018\u001Aƒnr\u0014\u0018rå\u0010°∆Ô\\6\u000EV\f\u0005z)”ó.'êÉ€m≤Ê Îd§ö[q3yheN0b˘\u0013\u0013\\ísÒ¢ôò˙AÊY—∞Í€Ì†û˜dÑ-\u0005Ê\u000Fﬂ–gL\u001E\u0016ç\bã’;˘3;uë“Ë\f$QrÒ\u0012û\u001DÚÀ\u0011ãíw“4\u001EcıœöTXz\u0014⁄‘îG¯Z(˛Lé0¢;\u0016ß∆g\u0014Usx\u001DQR>%\u0015ñ8Cy\u001Ct\u0015⁄≈äÈiÆÊØÍ\tï?ÑûàYº´s‰f@ƒz◊\"⁄\u001AU«å1√*_Û\u0011\ft\u0004ë¿\u001Bõ»=>Y§_Ωå·\u0014}U[=±¶qø\u0000{\u001Eñl\u000E\u0013@=QN‚∞\u000E5¿†õ∑oå\u0003ƒÎÀ\u0019õ±8i°\u0006\u0015\u000B”\u0013Øù`Qq<Ù€≠BEˇÜÔ‰\u001Ca•V\"@¯Xí,•\u000E\u0019Ìg≥ \u000Eæ„p·†A…¨\u001Båˇ1S«˚~Y¢ü°\u0006∆¡¸ò‚›∫Ωß2ï^C\u0007\u0000A„\u0000≠1„Ó\n\u0015\u0012töRó\u001DÎƒ÷\u0004˘ô\u001E™gB*7∫\u0010’\t∑M~[4«y\u001EvYò-\u0007¡c2#µ“Ùµâ\u0007$ù%Û`™∫≤c\u0019]p\u001BJå\\ﬂJT\u0018~i€Ó:KZgé¶⁄Òç,È%∏Zt9°32\u001DI»ôêÕ\u0001ä2†\u0005_§∑\u0006\u0006L4^LS• \u0005Å7Ÿú'≈\u0017\u0006Ò©\u000Ep#î!uÕÎh=à≥ª‹µ\u0001\tJ|+|TÖù¿>\nL\u0011ﬂÿÒæÛ\u0010ƒIyt ö…RÌ\u0011è%Ôˇ9∞›ï0ıô8æJ‹éºçõ/‰'R\u000Bævª°¯Ç[ÿ(Ω\u0015∫¬∂Hd\u001DêYô∏ÁΩÜ≥?Wì…\u007FAÂ˘Cû\bfŒ?Ä®Ñ.≤≈S\u0018’XÔcz/\u0001\b«$@\u000B\u000E[p®πìx¥ZXÊW=b◊!b\u001C·˝¡ˇëgûOà‰0^Òø≠ûÒ£ø{a\u000FâÉ—vÇ^Y¶;6∫QUΩ‹ø¯‹{˛ßxé„ù\u0015ï\u0001#ﬁÀgel2 @•›ûB\u0015™˙AP¡bHFü7∫ú›-\n3z$¡qB˚êo\u001Eüzƒ\"E∆—“4ÿ7'Ÿ\u000B¬ƒˇ3üÖù6M¬¥Â◊æ®+$\u000FÖæ\u001FÓÅ\b(R\u0007>\u0014›D¸ﬁˇ\u0010â}ÀqñQ&\u0015àñáÅ©ú⁄Æƒ∫Ï\u001FéÑ7\u0010øπ§…à\u0013)ÊG\u0007\u000Fö“'„éá@T\u0015Bh(Q}Ù¨€ÎkŒù\"ÁKJ\u0007u±ÿ¯„P\u0017Ãˇ˝§êˇ›G`jkR˙]≠¯/˝\u001EÕ>ÚTguÄπb/bgß*VJ–\u0013:ùè~2é$“q˜‰≤u\u00059'ÜiLUÊÜg\b\b\u0000ƒ⁄¬≥ø¥Ô˝®.1\u001Ev\u001B4'™+\u0001Èm°\u001AOBGW\u000F\u0016èD)?\u001F~8±8\u0013‰_Íñ\u0016ñ\u0001G—á\ti\f’Àâ#Æ\u001ALm–\u0014G˝á@\bÕ§ûee".toCharArray


  def main(args: Array[String]) {
    // take a command line filename arg as the torrent file
    val bencodeContents = args.length match {
      case 0 => getBencodeFromString()
      case 1 => getBencodeFromFile(args(0))
    }

    //given the file contents, pattern match over it and extract out the data we want, storing it in a metadata data type
    val torrentMetaData = MetaData.parseTorrentFileStructure(bencodeContents)
    println("Torrent metadata: " + torrentMetaData)
  }

  private def getBencodeFromString(): BValue = {
    Bencode.decode(input)(0)
  }

  private def getBencodeFromFile(filename: String): BValue = {
    readFile(filename)
  }


  private def readFile(filename: String): BValue = {
    val source = Source.fromFile(filename, "UTF-8")
    val is = source.reader()
    val bytes: Array[Char] = Stream.continually(is.read).takeWhile(-1 !=).map(_.toChar).toArray
    source.close()

    Bencode.decode(bytes)(0)
  }



}
