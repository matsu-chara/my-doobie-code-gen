lazy val root = (project in file("."))
  .settings(
    name := "my-doobie-code-gen",
    version := "0.1",
    scalaVersion := "2.13.1",
    libraryDependencies ++= Seq(
      "org.tpolecat" %% "doobie-core" % "0.8.8"
    )
  )
