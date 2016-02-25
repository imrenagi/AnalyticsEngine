
import com.imrenagi.analytics.database.Db
import com.imrenagi.analytics.database.model.SearchHistory
import com.imrenagi.analytics.events.avro.{SearchEvent, ClickEvent}

import kafka.serializer.DefaultDecoder
import org.apache.avro.io.DecoderFactory
import org.apache.avro.specific.SpecificDatumReader
import org.apache.spark._
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Minutes, Seconds, StreamingContext}
import org.joda.time.DateTime

/**
 * Created by imrenagi on 1/30/16.
 */
object SearchQueryStreaming {

  def main (args: Array[String]) {

    val Array(topics, numThreads) = Array("searchStreamingTest", "2")
    val sparkConf = new SparkConf().setAppName("WindowClickCount").setMaster("local[2]")

    // Slide duration of ReduceWindowedDStream must be multiple of the parent DStream, and we chose 2 seconds for the reduced
    // window stream
    val ssc = new StreamingContext(sparkConf, Seconds(2))

    // Because we're using .reduceByKeyAndWindow, we need to persist it to disk
//    ssc.checkpoint("./checkpointDir")

    val kafkaConf = Map(
      "metadata.broker.list" -> "localhost:9092", // Default kafka broker list location
      "zookeeper.connect" -> "localhost:2181", // Default zookeeper location
      "group.id" -> "kafka-spark-streaming-example",
      "zookeeper.connection.timeout.ms" -> "1000")

    val topicMap = topics.split(",").map((_, numThreads.toInt)).toMap

    val lines = KafkaUtils.createStream[String, Array[Byte], DefaultDecoder, DefaultDecoder](ssc, kafkaConf, topicMap, StorageLevel.MEMORY_ONLY_SER).map(_._2)

    val mappedSearchHistory = lines.transform{ rdd =>
      rdd.map{
        bytes => AvroUtil.searchEventDecode(bytes)
      }.map{ searchEvent =>
          SearchHistory(searchEvent.getUserId.longValue(),
            searchEvent.getQueryText.toString,
            new DateTime(searchEvent.getTimestamp))
      }
    }

    mappedSearchHistory.foreachRDD(rdd =>
      rdd.foreachPartition( partitionOfRecords =>
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
    val reader = new SpecificDatumReader[SearchEvent](SearchEvent.getClassSchema)

    def searchEventDecode(bytes: Array[Byte]): SearchEvent = {
      val decoder = DecoderFactory.get.binaryDecoder(bytes, null)
      reader.read(null, decoder)
    }
  }


}

