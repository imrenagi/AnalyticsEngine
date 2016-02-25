
import java.io.{ByteArrayOutputStream, PrintStream}
import java.util.Properties

import _root_.io.confluent.kafka.serializers.KafkaAvroDecoder
import com.imrenagi.analytics.database.Db
import com.imrenagi.analytics.database.model.SearchHistory
import io.confluent.kafka.formatter.AvroMessageFormatter
import kafka.serializer.{DefaultDecoder, StringDecoder}
import kafka.utils.VerifiableProperties
import model.Response.Project
import model.ResponseFormatter
import org.apache.spark._
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.joda.time.DateTime
import play.api.libs.json._

/**
 * Created by imrenagi on 1/30/16.
 */
object ProjectStreaming {

  def main(args: Array[String]) {

    val Array(topics, numThreads) = Array("mysql-projects", "2")
    val sparkConf = new SparkConf().setAppName("ProjectStreamCount").setMaster("local[2]")

    val ssc = new StreamingContext(sparkConf, Seconds(2))

    val kafkaConf = Map(
      "metadata.broker.list" -> "localhost:9092",
      "zookeeper.connect" -> "localhost:2181",
      "group.id" -> "2",
      "zookeeper.connection.timeout.ms" -> "1000",
      "schema.registry.url" -> "http://localhost:8081"
    )

    val topicMap = topics.split(",").map((_, numThreads.toInt)).toMap

    val lines = KafkaUtils.createStream[String, Array[Byte], StringDecoder, DefaultDecoder](ssc, kafkaConf, topicMap, StorageLevel.MEMORY_ONLY_SER).map(_._2)

    val mappedProject = lines.transform { rdd =>
      rdd.map { bytes =>
        AvroUtil.projectStreamDecode(bytes)
      }.map { stream =>
        SearchHistory(1000,
          stream.toString.trim,
          new DateTime())
      }
    }

    mappedProject.foreachRDD(rdd =>
      rdd.foreachPartition(partitionOfRecords =>
        partitionOfRecords.foreach(
          Db.search_by_user_id.store(_)
        )
      )
    )

    ssc.start()
    ssc.awaitTermination()
  }

  //AVRO Deserialized
  object AvroUtil {
    val formatter = new AvroMessageFormatter()

    val properties = new Properties()
    properties.put("metadata.broker.list", "localhost:9092")
    properties.put("zookeeper.connect", "localhost:2181")
    properties.put("group.id", "2")
    properties.put("schema.registry.url", "http://localhost:8081")

    var verifiableProperties = new VerifiableProperties(properties)
    var valueDecoder = new KafkaAvroDecoder(verifiableProperties)

    def projectStreamDecode(bytes: Array[Byte]): Project = {
      val baos = new ByteArrayOutputStream()
      val ps = new PrintStream(baos)

      formatter.init(properties)
      formatter.writeTo(null, bytes, ps)
      //      println("=========================================")
      //      println(baos.toString)
      //      println("****************************************")

      //      Json.parse(baos.toString).validate[Project](ResponseFormatter.projectReads) match {
      //        case s: JsSuccess[Project] => {
      //          val project: Project = s.get
      //          println(project.name.getOrElse(""))
      //        }
      //        case e: JsError => {
      //          println(e)
      //        }
      //      }

      Json.parse(baos.toString).validate[Project](ResponseFormatter.projectReads).get
    }
  }

}



