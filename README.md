# sql code gen

output sql fragment for doobie from clipboard DDL.
this is just sample code repository.


```sh
$ cat <<EOS | pbcopy
CREATE TABLE user(
    id int NOT NULL,  -- ユーザーID
    last_name varchar(255), -- 名
    first_name varchar(255), -- 姓
    PRIMARY KEY (id)
);
EOS

$ sbt genSqlFromClipBoard
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
```
