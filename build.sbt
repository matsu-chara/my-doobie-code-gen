import java.io.PrintWriter

import scala.util.control.NonFatal

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
      try {
        val table = MyDoobieCodeGen.getTableFromClipBoard
        val writer = new PrintWriter(System.out)
        MyDoobieCodeGen.render(
          table,
          writer,
          (resourceDirectory in Compile).value.getAbsolutePath + "/templates/sql")
        writer.flush()
      } catch {
        case NonFatal(e) =>
          streams.value.log.error("error occurred. cause: " + e.toString)
          throw e
      }
    }
  )
