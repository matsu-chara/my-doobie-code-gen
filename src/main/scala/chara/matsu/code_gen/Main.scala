package chara.matsu.code_gen

import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.io.{PrintWriter, Writer}

import com.github.jknack.handlebars.Handlebars

object Main {
  private val clipboard = Toolkit.getDefaultToolkit.getSystemClipboard

  def main(args: Array[String]): Unit = {
    val table = getTable

    val writer = new PrintWriter(System.out)
    render(table, writer)
    writer.flush()
  }

  private def getTable: Table = {
    val str = clipboard.getData(DataFlavor.stringFlavor).asInstanceOf[String]
    val itr = str.linesIterator
    val tableLine = itr.next()
    val fieldLines = itr.toList.takeWhile(s => !s.contains("PRIMARY KEY"))

    //CREATE TABLE table_name
    val table = tableLine.dropWhile(_.isWhitespace).split(' ').apply(2)

    //field_name data_type NOT NULL
    val fields =
      fieldLines.map(line => line.dropWhile(_.isWhitespace).split(' ').apply(0))

    Table(table, fields)
  }

  private def render(table: Table, writer: Writer): Unit = {
    import scala.jdk.CollectionConverters._

    val handlebars = new Handlebars
    val template = handlebars.compile("templates/sql")
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

object sample2_employees_sql {
  import doobie.implicits.toSqlInterpolator
  import doobie.util.fragment.Fragment

  def selectFrom: Fragment = fr"""
                                 |SELECT
                                 |  `List(no, department_no, last_name, first_name, );)`,

                                 |FROM
                                 |  `sample2_employees`
                                 |""".stripMargin

  def insert: Fragment = sql"""
                              |INSERT INTO
                              |  `sample2_employees` (
                              |  `List(no, department_no, last_name, first_name, );)`,

                              |)
                              |VALUES
                              |  (?,)
                              |FROM
                              |  sample2_employees
                              |""".stripMargin
}
