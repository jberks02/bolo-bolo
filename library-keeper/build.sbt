import scoverage.ScoverageKeys

name := "library-keeper"
maintainer := "justinwendellberkshire@gmail.com"
version := "0.1.0"
scalaVersion := "3.3.4"

val PekkoVersion = "1.1.1"
val PekkoHttpVersion = "1.1.0"
val doobieVersion = "1.0.0-RC5"

libraryDependencies ++= Seq(
  "org.apache.pekko" %% "pekko-actor-typed" % PekkoVersion,
  "org.apache.pekko" %% "pekko-stream" % PekkoVersion,
  "org.apache.pekko" %% "pekko-http" % PekkoHttpVersion,
  "org.apache.pekko" %% "pekko-slf4j" % PekkoVersion,
  "org.apache.pekko" %% "pekko-http-spray-json" % PekkoHttpVersion,
  "io.spray" %% "spray-json" % "1.3.6",
  "ch.qos.logback" % "logback-classic" % "1.5.6",
  "com.typesafe" % "config" % "1.4.3",
  "org.tpolecat" %% "doobie-core"     % doobieVersion,
  "org.tpolecat" %% "doobie-postgres" % doobieVersion,
  "org.tpolecat" %% "doobie-specs2"   % doobieVersion,
  "org.scalatest" %% "scalatest" % "3.2.18" % Test,
  "org.scalamock" %% "scalamock" % "6.0.0" % Test
)