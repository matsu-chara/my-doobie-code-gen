import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.io.{PipedInputStream, PipedOutputStream, PrintWriter, Writer}
import com.github.jknack.handlebars.io.FileTemplateLoader

import com.github.jknack.handlebars.Handlebars
import sbt.io.Using

lazy val genSqlFromClipBoard = taskKey[Unit]("generate sql from clip board")

lazy val root = (project in file("."))
  .settings(
    name := "my-doobie-code-gen",
    version := "0.1",
    scalaVersion := "2.13.1",
    libraryDependencies ++= Seq(
      "org.tpolecat" %% "doobie-core" % "0.8.8"
    ),
    genSqlFromClipBoard := {
      val clipboard = Toolkit.getDefaultToolkit.getSystemClipboard
      def getTable: Table = {
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

      def render(table: Table, writer: Writer): Unit = {
        import scala.collection.JavaConverters._

        val handlebars = new Handlebars(new FileTemplateLoader("/"))
        val template = handlebars.compile(
          (resourceDirectory in Compile).value.getAbsolutePath + "/templates/sql"
        )
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

      val table = getTable
      val writer = new PrintWriter(System.out)
      render(table, writer)
      writer.flush()
    }
  )
