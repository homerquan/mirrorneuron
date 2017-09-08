name := "engine"

organization := "io.convospot"

version := "0.2.0"

scalaVersion := "2.11.8"

fork := true

resolvers ++= Seq(
  "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4.8",
  "com.typesafe.akka" % "akka-contrib_2.11" % "2.4.8",
  "com.typesafe.akka" % "akka-testkit_2.11" % "2.4.8",
  "org.apache.flink" % "flink-core" % "1.0.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.4.0",
  "org.specs2" % "specs2-core_2.11" % "3.8",
  "io.spray" %% "spray-json" % "1.3.2",
  "io.spray" %% "spray-client" % "1.3.2",
  "io.spray" %% "spray-httpx" % "1.3.2"
)

scalacOptions in Test ++= Seq("-Yrangepos")

mainClass in Compile := Some("io.convospot.app.Main")

Revolver.settings