name := "flix-freaks"

version := "0.1"

scalaVersion := "2.13.3"

lazy val slickVersion = "3.3.2"
lazy val akkaVersion = "2.6.9"
val AkkaHttpVersion = "10.2.0"
val slickJodaMapperVersion = "2.4.2"
val scalaTestVersion = "3.2.0"
val mockitoScalaVersion = "1.13.6"
val newtypeVersion = "0.4.3"
val refinedVersion = "0.9.12"

libraryDependencies ++= Seq(
  // Spray
  "io.spray" %% "spray-json" % "1.3.5",

  "com.typesafe.slick" %% "slick" % slickVersion,
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.3.3",
  "org.postgresql" % "postgresql" % "42.2.16",

  //Akka
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "com.lightbend.akka" %% "akka-stream-alpakka-csv" % "2.0.2",
  "com.lightbend.akka" %% "akka-stream-alpakka-slick" % "2.0.2",
  "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,

  // Cats
  "org.typelevel" %% "cats-core" % "2.0.0",
  "com.rms.miu" %% "slick-cats" % "0.10.1",

  // Testin
  "org.scalactic" %% "scalactic" % scalaTestVersion,
  "org.scalatest" %% "scalatest" % scalaTestVersion % "test",
  "org.mockito" %% "mockito-scala" % mockitoScalaVersion,
  "org.mockito" %% "mockito-scala-scalatest" % mockitoScalaVersion,
  "org.mockito" %% "mockito-scala-specs2" % mockitoScalaVersion,
  "org.mockito" %% "mockito-scala-cats" % mockitoScalaVersion,
  "org.mockito" %% "mockito-scala-scalaz" % mockitoScalaVersion,

  // newtype
  "io.estatico" %% "newtype" % newtypeVersion,
  "eu.timepit" %% "refined" % refinedVersion,
  "be.venneborg" %% "slick-refined" % "0.5.0",


)

//scalacOptions += "-Ypartial-unification"
scalacOptions += "-Ymacro-annotations"
