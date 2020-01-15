package chara.matsu.code_gen

object Main {
  def main(args: Array[String]): Unit = {
    println(user_sql.selectFrom.toString())
  }
}

/**
  * sample result
  *
  * see genSqlFromClipBoard in build.sbt
  */
object user_sql {
  import doobie.implicits.toSqlInterpolator
  import doobie.util.fragment.Fragment

  def selectFrom: Fragment = fr"""
                                 |SELECT
                                 |  `id`,
                                 |  `last_name`,
                                 |  `first_name`
                                 |FROM
                                 |  `user`
                                 |""".stripMargin

  def insert: Fragment = sql"""
                              |INSERT INTO `user` (
                              |  `id`,
                              |  `last_name`,
                              |  `first_name`
                              |)
                              |VALUES
                              |  (?,?,?)
                              |""".stripMargin
}
