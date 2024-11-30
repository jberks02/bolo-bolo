name := "Resource-Management-System"

version := "0.1.0"
scalaVersion := "3.3.4"

val PekkoVersion = "1.1.1"
val PekkoHttpVersion = "1.1.0"

libraryDependencies ++= Seq(
  "org.apache.pekko" %% "pekko-actor-typed" % PekkoVersion,
  "org.apache.pekko" %% "pekko-stream" % PekkoVersion,
  "org.apache.pekko" %% "pekko-http" % PekkoHttpVersion,
  "org.apache.pekko" %% "pekko-slf4j" % PekkoVersion,
  "org.apache.pekko" %% "pekko-http-spray-json" % PekkoHttpVersion,
  "org.apache.pekko" %% "pekko-connectors-slick" % "1.1.0-M1",
  "io.spray" %% "spray-json" % "1.3.6",
  "ch.qos.logback" % "logback-classic" % "1.5.6",
  "org.mariadb.jdbc" % "mariadb-java-client" % "3.4.1",
  "com.typesafe" % "config" % "1.4.3",
  "com.fasterxml.jackson.core" % "jackson-core" % "2.17.2",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.13.5",
  "com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310" % "2.13.5",
  "org.tpolecat" %% "doobie-core"     % "1.0.0-RC5",
  "org.tpolecat" %% "doobie-hikari"   % "1.0.0-RC5", 
  "org.tpolecat" %% "doobie-mariadb"  % "1.0.0-RC2"
)
