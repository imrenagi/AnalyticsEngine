package com.imrenagi.analytics

import com.imrenagi.analytics.consumer.{SingleTopicConsumer, ChunkConsumer}

/**
 * Created by imrenagi on 12/27/15.
 */
object Hello {

  def main (args: Array[String]) {
    val topicNames = "test"
    val consumer = SingleTopicConsumer(topicNames)
    consumer.read().foreach(println)
  }
}
