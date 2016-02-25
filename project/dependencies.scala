
import sbt.Keys._
import sbt._

object dependencies {
  object Version {
    val spray = "1.3.3"
    val phantom = "1.12.2"
  }

  lazy val Common = common ++ cassandra ++ kafka ++ avro ++ confluent
  lazy val DataIngestion = common
  lazy val BatchProcessing = common ++ spark ++ hadoop
  lazy val StreamingProcessing = common ++ spark ++ sparkStreaming

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
    "com.typesafe.play" % "play-json_2.10" % "2.4.0-M2"
  )

  val confluent = Seq(
    "io.confluent" % "kafka-avro-serializer" % "2.0.0"
//    "io.confluent" % "confluent-platform-2.10" % "2.0.0"
  )

  val spark = Seq(
    "org.apache.spark" %% "spark-core" % "1.5.2",
    "org.apache.spark" %% "spark-sql" % "1.5.2",
    "org.apache.spark" %% "spark-mllib" % "1.5.2"
  )

  val avro = Seq(
    "com.databricks" %% "spark-avro" % "2.0.1"
//    "org.apache.avro" %% "avro" %" 1.7.7",
//    "org.codehaus.jackson" % "jackson-mapper-asl" % "1.9.13"
  )

  val sparkStreaming = Seq(
    "org.apache.spark" %% "spark-streaming" % "1.5.2"
  )

  val hadoop = Seq(
    "org.apache.hadoop" % "hadoop-client" % "2.7.1"
  )

  val kafka = Seq(
    "org.apache.spark" %% "spark-streaming-kafka" % "1.5.2",
    "org.apache.kafka" % "kafka_2.10" % "0.9.0.0"
  )

  val cassandra = Seq(
    "com.websudos" %% "phantom-dsl" % Version.phantom,
    "com.websudos" %% "phantom-testkit" % Version.phantom % "test, provided"
  )

}
