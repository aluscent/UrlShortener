ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "UrlShortener-Snapptrip",
  )

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.5.0",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.5.0",

  "com.typesafe.akka" %% "akka-actor" % "2.8.0",
  "com.typesafe.akka" %% "akka-stream" % "2.8.0",

  "com.github.scredis" %% "scredis" % "2.4.3",
  "org.mongodb.scala" %% "mongo-scala-driver" % "4.9.1",

  "org.scalatest" %% "scalatest" % "3.2.15",
  "io.aeron" % "aeron-driver" % "1.40.0",
  "io.aeron" % "aeron-client" % "1.40.0",
  "org.slf4j" % "slf4j-simple" % "2.0.6",
  "org.slf4j" % "slf4j-api" % "2.0.6"
)
