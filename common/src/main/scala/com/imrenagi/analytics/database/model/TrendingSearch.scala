package com.imrenagi.analytics.database.model

import com.websudos.phantom.CassandraTable
import com.websudos.phantom.column.{DateTimeColumn, PrimitiveColumn}
import com.websudos.phantom.connectors.RootConnector
import com.websudos.phantom.dsl._
import scala.concurrent.duration._
import org.joda.time.DateTime

import scala.concurrent.{Future, Await}

/**
 * Created by imrenagi on 2/22/16.
 */
case class TrendingSearch(date: Long, queryText: String, counter: Long)

class TrendingSearches extends CassandraTable[ConcreteTrendingSearch, TrendingSearch] {

  object date extends LongColumn(this) with PartitionKey[Long]
  object counter extends LongColumn(this) with ClusteringOrder[Long] with Descending
  object query_text extends StringColumn(this) with ClusteringOrder[String] with Ascending

  override def fromRow(row:Row): TrendingSearch = {
    TrendingSearch(
      date(row),
      query_text(row),
      counter(row)
    )
  }

}

abstract class ConcreteTrendingSearch extends TrendingSearches with RootConnector {

  def createTable(): Unit = {
    Await.ready(create.ifNotExists().future(), 3.seconds)
  }

  def store(search: TrendingSearch): Future[ResultSet] = {
    insert.value(_.date, search.date)
    .value(_.counter, search.counter)
    .value(_.query_text, search.queryText)
    .future()
  }

}
