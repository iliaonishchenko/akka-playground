name := "akka-actors"

version := "0.1"

scalaVersion := "2.12.5"

resolvers += Resolver.jcenterRepo

libraryDependencies ++= Seq(
//  "com.typesafe.akka" %% "akka-actor" % "2.5.11"
  "com.typesafe.akka" %% "akka-typed-experimental" % "2.5-M1",
  "org.scalaz" %% "scalaz-core" % "7.2.23"
)