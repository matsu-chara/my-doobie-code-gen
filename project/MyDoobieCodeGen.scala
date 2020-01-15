import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.io.Writer

import com.github.jknack.handlebars.Handlebars
import sbt.io.Using

import scala.io.Source

object MyDoobieCodeGen {
  def getClipBoard(): Seq[String] = {
    val clipboard = Toolkit.getDefaultToolkit.getSystemClipboard
    val str = clipboard.getData(DataFlavor.stringFlavor).asInstanceOf[String]
    str.linesIterator.toList
  }

  def parseCreateTable(rawLines: Seq[String]): Table = {
    if (rawLines.size <= 1) {
      throw new IllegalArgumentException(
        s"args size (${rawLines.size}) is too small.")
    }

    val lines = rawLines.map(_.dropWhile(_.isWhitespace))
    val tableLine = lines.head
    val restLines = lines.tail

    if (!tableLine.startsWith("CREATE TABLE")) {
      throw new IllegalArgumentException(
        "clipboard does not have create table statement")
    }
    if (!restLines.exists(_.startsWith("PRIMARY KEY"))) {
      throw new IllegalArgumentException(
        "clipboard does not have create table statement with primary key")
    }

    //CREATE TABLE table_name
    val table =
      tableLine.dropWhile(_.isWhitespace).split(' ').apply(2).split('(')(0)

    //field_name data_type ~~,
    val fieldLines = restLines.takeWhile(!_.startsWith("PRIMARY KEY"))
    val fields = fieldLines.map(line => line.split(' ').apply(0))

    Table(table, fields)
  }

  def render(table: Table, writer: Writer): Unit = {
    val using = Using.resource { str: String =>
      Source.fromURL(getClass.getResource(str))
    }

    val handlebars = new Handlebars()
    val templateStr = using("/templates/sql.hbs")(_.mkString)
    val template = handlebars.compileInline(templateStr)

    import scala.collection.JavaConverters._
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
