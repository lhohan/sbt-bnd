organization := "eu.lhoest"

name := "sbt-bnd"

version := "0.2.1-SNAPSHOT"

sbtPlugin := true

libraryDependencies ++= Seq(
  "biz.aQute" % "bndlib" % "2.0.0.20130123-133441",
  "joda-time" % "joda-time" % "2.1",
  "org.joda" % "joda-convert" % "1.2"
)

releaseSettings
