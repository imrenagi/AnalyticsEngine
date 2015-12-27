
import sbt.Keys._
import sbt._

object dependencies {
  object Version {
    val spray = "1.3.3"
    val phantom = "1.12.2"
  }

  lazy val Common = common ++ cassandra
  lazy val DataIngestion = common ++ kafka

  val spray = Seq(
    "io.spray"            %%  "spray-can"     % Version.spray,
    "io.spray"            %%  "spray-routing" % Version.spray,
    "io.spray"            %%  "spray-client"  % Version.spray,
    "io.spray"            %%  "spray-testkit" % Version.spray  % "test",
    "io.spray"            %%  "spray-json"    % "1.3.2"
  )

  val common = Seq(
    "org.specs2" %% "specs2-core" % "3.6.4-20150901013911-1f41c5e" % "test",
    "ch.qos.logback" % "logback-classic" % "1.1.3",
    "com.typesafe" % "config" % "1.2.1",
    "com.typesafe.play" % "play-json_2.11" % "2.4.0-M2"
  )

  val spark = Seq(
    "org.apache.spark" %% "spark-core" % "1.5.2"
  )

  val kafka = Seq(
    "org.apache.kafka" % "kafka_2.10" % "0.8.1"
      exclude("javax.jms", "jms")
      exclude("com.sun.jdmk", "jmxtools")
      exclude("com.sun.jmx", "jmxri")
  )

  val cassandra = Seq(
    "com.websudos" %% "phantom-dsl" % Version.phantom,
    "com.websudos" %% "phantom-testkit" % Version.phantom % "test, provided"
  )

}
