import com.typesafe.sbt.SbtNativePackager.packageArchetype
import sbt._
import NativePackagerKeys._

packageArchetype.java_application

name := "facebook-calendar"

organization := "in.ashwanthkumar"

version := "1.0"

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
  "com.twitter" % "finagle-http_2.10" % "6.18.0",
  "com.joestelmach" % "natty" % "0.9",
  "com.fasterxml.jackson.core" % "jackson-core" % "2.4.2",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.4.2",
  "com.fasterxml.jackson.module" % "jackson-module-scala_2.10" % "2.4.2"
)

resolvers += Resolver.mavenLocal

