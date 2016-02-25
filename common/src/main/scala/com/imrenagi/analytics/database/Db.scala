package com.imrenagi.analytics.database

import com.imrenagi.analytics.database.model.{ConcreteTrendingSearch, ConcreteSearchHistory, ConcreteUser}
import com.websudos.phantom.connectors.{ContactPoints, KeySpaceDef, ContactPoint}
import com.websudos.phantom.db.DatabaseImpl

/**
 * Created by imrenagi on 2/6/16.
 */
object Default {
  val hosts = Seq("127.0.0.1")
  val Connector = ContactPoints(hosts).keySpace("my_keyspace")
}

class Db(val keyspace: KeySpaceDef) extends DatabaseImpl(keyspace) {
  object users extends ConcreteUser with keyspace.Connector
  object search_by_user_id extends ConcreteSearchHistory with keyspace.Connector
  object trending_search_by_date extends ConcreteTrendingSearch with keyspace.Connector
}

object Db extends Db(Default.Connector)
