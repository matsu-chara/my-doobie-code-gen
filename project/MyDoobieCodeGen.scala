import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.io.Writer

import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.io.FileTemplateLoader

object MyDoobieCodeGen {
  def getTableFromClipBoard: Table = {
    val clipboard = Toolkit.getDefaultToolkit.getSystemClipboard
    val str =
      clipboard.getData(DataFlavor.stringFlavor).asInstanceOf[String]
    val itr = str.linesIterator.map(_.dropWhile(_.isWhitespace))
    val tableLine = itr.next()
    val restLines = itr.toList
    val fieldLines = restLines.takeWhile(!_.startsWith("PRIMARY KEY"))

    if (!tableLine.startsWith("CREATE TABLE")) {
      throw new IllegalArgumentException(
        "clipboard does not have create table statement")
    }
    if (!restLines.exists(_.startsWith("PRIMARY KEY"))) {
      throw new IllegalArgumentException(
        "clipboard does not have create table statement with primary key")
    }

    //CREATE TABLE table_name
    val table = tableLine.dropWhile(_.isWhitespace).split(' ').apply(2)

    //field_name data_type ~~,
    val fields = fieldLines.map(line => line.split(' ').apply(0))

    Table(table, fields)
  }

  def render(table: Table, writer: Writer, filePath: String): Unit = {
    import scala.collection.JavaConverters._

    val handlebars = new Handlebars(new FileTemplateLoader("/"))
    val template = handlebars.compile(filePath)
    template.apply(table.toMap.asJava, writer)
  }

  case class Table(name: String, fields: Seq[String]) {
    def toMap: Map[String, Any] = {
      Map(
        "table" -> name,
        "fields" -> fields.toArray
      )
    }
  }

}
