name := """simple-rest-scala"""

version := "1.0-SNAPSHOT"

lazy val root = project.in(file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
    "mysql" % "mysql-connector-java" % "5.1.34",
    "com.google.code.gson" % "gson" % "2.2.4",
    "com.typesafe.play" %% "play-slick" % "2.0.2",
    "com.typesafe.play" %% "play-slick-evolutions" % "2.0.2"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

