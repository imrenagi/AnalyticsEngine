package com.imrenagi.analytics.consumer

/**
 * Created by imrenagi on 12/27/15.
 */

import com.imrenagi.analytics.utils.KafkaConfig
import kafka.consumer.{ Consumer => KafkaConsumer }
import kafka.consumer._

abstract class Consumer(topics: List[String]) {

  protected val kafkaConfig = KafkaConfig()
  protected val config = new ConsumerConfig(kafkaConfig)

  def read(): Iterable[String]
}