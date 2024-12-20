import scoverage.ScoverageKeys

name := "Resource-Management-System"
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
  "org.apache.pekko" %% "pekko-connectors-slick" % "1.1.0-M1",
  "io.spray" %% "spray-json" % "1.3.6",
  "ch.qos.logback" % "logback-classic" % "1.5.6",
  "com.typesafe" % "config" % "1.4.3",
  "org.tpolecat" %% "doobie-core"     % doobieVersion,
  "org.tpolecat" %% "doobie-postgres" % doobieVersion,
  "org.tpolecat" %% "doobie-specs2"   % doobieVersion,
  "org.scalatest" %% "scalatest" % "3.2.18" % Test,
  "org.scalamock" %% "scalamock" % "6.0.0" % Test
)

// Testing Setup
val E2ETest = config("e2e") extend Test

inConfig(E2ETest)(Defaults.testSettings)

Seq(
  E2ETest / scalaSource := baseDirectory.value / "src" / "e2e" / "scala",
  E2ETest / resourceDirectory := baseDirectory.value / "src" / "e2e" / "resources"
)

// sbt clean coverage test coverageReport -- Run unit tests only
// sbt clean coverage it:test coverageReport -- Run Integration test suite
// sbt clean coverage e2e:test coverageReport -- Run end to end tests only
// sbt clean coverage test it:test coverageReport -- Run inegration and unit tests together

ScoverageKeys.coverageMinimumBranchTotal := 80
ScoverageKeys.coverageFailOnMinimum := false
ScoverageKeys.coverageHighlighting := true

// Packaging to executable for linux
enablePlugins(JavaAppPackaging)

// sbt clean compile universal:packageBin -- Build universal package that outputs universal file