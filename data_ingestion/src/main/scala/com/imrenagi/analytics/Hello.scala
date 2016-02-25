package com.imrenagi.analytics

import java.io.ByteArrayOutputStream
import java.util

import com.imrenagi.analytics.events.avro.{SearchEvent}
import org.apache.avro.io.EncoderFactory
import org.apache.avro.specific.SpecificDatumWriter
import org.apache.kafka.clients.producer.{ProducerConfig, KafkaProducer, ProducerRecord}
import org.joda.time.DateTime

import scala.io.Source
import scala.util.Random

/**
 * Created by imrenagi on 12/27/15.
 */
object Hello {

  def main(args: Array[String]) {
    // Zookeeper connection properties
    val props = new util.HashMap[String, Object]()
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
      "org.apache.kafka.common.serialization.ByteArraySerializer") // Kafka avro message stream comes in as a byte array
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
      "org.apache.kafka.common.serialization.StringSerializer")

    val producer = new KafkaProducer[String, Array[Byte]](props)

    val lines = Source.fromFile("blog_tags.csv").getLines.toList
    var words = lines.map(lines => lines.split(",")(1)).map(_.trim.toLowerCase)

    // Send some kafka click events at 1 second intervals
    while (true) {

      var event = newRandomClickEvent
      val clickBytes = serializeClickEvent(event)
      val message = new ProducerRecord[String, Array[Byte]]("searchStreamingTest", null, clickBytes) // Create a new producer record to send the message in
      println(event.getUserId + ", "+ event.getQueryText + ", " + event.getTimestamp)

      producer.send(message)

      Thread.sleep(100)
    }

    // Generate a random click event
    def newRandomClickEvent: SearchEvent = {
      val userId = Random.nextInt(5) // We'll define 0..5 with valid mappings to real names
      val timestamp = new DateTime().getMillis//
      val queryText = words(Random.nextInt(words.length))
      new SearchEvent(userId, timestamp, queryText)
    }

    // Serialize a click event using Avro into a byte array to send through Kafka
    // https://cwiki.apache.org/confluence/display/AVRO/FAQ
    def serializeClickEvent(searchEvent: SearchEvent): Array[Byte] = {
      val out = new ByteArrayOutputStream()
      val encoder = EncoderFactory.get.binaryEncoder(out, null)
      val writer = new SpecificDatumWriter[SearchEvent](SearchEvent.getClassSchema)

      writer.write(searchEvent, encoder)
      encoder.flush
      out.close
      out.toByteArray
    }
  }

}
