package chara.matsu.code_gen

object Main {
  def main(args: Array[String]): Unit = {
    println(sample2_employees_sql.selectFrom.toString())
  }
}

/**
  * sample result
  *
  * see genSqlFromClipBoard in build.sbt
  */
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
