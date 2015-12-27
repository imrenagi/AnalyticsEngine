package com.imrenagi.analytics.topic

/**
 * Created by imrenagi on 12/27/15.
 */
import java.util.Properties
import com.imrenagi.analytics.utils.KafkaConfig
import kafka.utils.ZKStringSerializer
import kafka.producer.{ Producer => KafkaProducer }
import org.I0Itec.zkclient.ZkClient
import org.I0Itec.zkclient.serialize.ZkSerializer

object ZookeeperUtils {

  def createClient(
                    config: Properties = KafkaConfig(),
                    sessTimeout: Int = 10000,
                    connTimeout: Int = 10000,
                    serializer: ZkSerializer = ZKStringSerializer): ZkClient = {
    val host = config.getProperty("zookeeper.connect")
    new ZkClient(host, sessTimeout, connTimeout, serializer)
  }
}
