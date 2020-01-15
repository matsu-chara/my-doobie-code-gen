lazy val root = (project in file("."))
  .settings(
    name := "my-doobie-code-gen",
    version := "0.1",
    scalaVersion := "2.13.1",
    libraryDependencies ++= Seq(
      "com.github.jknack" % "handlebars" % "4.1.2",
      "org.tpolecat" %% "doobie-core"      % "0.8.8"
    ),
  )
