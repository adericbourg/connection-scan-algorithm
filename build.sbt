name := "csa"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies += "com.github.tototoshi" %% "scala-csv" % "1.2.2"

// Solve conflicts
libraryDependencies += "org.scala-lang" % "scala-reflect" % "2.11.7"
libraryDependencies += "org.scala-lang.modules" % "scala-xml_2.11" % "1.0.4"

// Testing
libraryDependencies += "org.specs2" %% "specs2-core" % "3.6.5" % "test"
