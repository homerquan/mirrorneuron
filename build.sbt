name := "engine"

organization := "io.convospot"

version := "0.2.0"

scalaVersion := "2.12.3"

fork := true

resolvers ++= Seq(
  "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.6",
  "com.typesafe.akka" % "akka-contrib_2.12" % "2.5.6",
  "com.typesafe.akka" % "akka-testkit_2.12" % "2.5.6",
  "com.typesafe.akka" %% "akka-cluster" % "2.5.6",
  "com.typesafe.akka" %% "akka-cluster-sharding" % "2.5.6",
  "org.iq80.leveldb" % "leveldb" % "0.9",
  "org.scalaz" %% "scalaz-core" % "7.2.15",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2",
  "org.slf4j" % "slf4j-api" % "1.7.25",
  "org.slf4j" % "log4j-over-slf4j" % "1.7.25",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.github.scopt" %% "scopt" % "3.7.0",
  "org.specs2" % "specs2-core_2.12" % "4.0.0"
)

scalacOptions in Test ++= Seq("-Yrangepos")

mainClass in Compile := Some("io.convospot.engine.app.Main")