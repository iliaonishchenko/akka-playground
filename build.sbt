name := "akka-actors"

version := "0.1"

scalaVersion := "2.12.5"

val asyncHttp = "com.ning" % "async-http-client" % "1.9.40"


libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.11",
  asyncHttp
)