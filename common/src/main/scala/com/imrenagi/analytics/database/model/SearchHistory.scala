package com.imrenagi.analytics.database.model

import com.websudos.phantom.CassandraTable
import com.websudos.phantom.column.{DateTimeColumn, PrimitiveColumn}
import com.websudos.phantom.connectors.RootConnector
import com.websudos.phantom.dsl._
import scala.concurrent.duration._
import org.joda.time.DateTime

import scala.concurrent.{Future, Await}

/**
 * Created by imrenagi on 2/10/16.
 */
case class SearchHistory(userID: Long, queryText: String, searchDate: DateTime)

class SearchHistories extends CassandraTable[ConcreteSearchHistory, SearchHistory] {

  object user_id extends LongColumn(this) with PartitionKey[Long]
  object query_text extends StringColumn(this)
  object search_date extends DateTimeColumn(this) with ClusteringOrder[DateTime] with Ascending

  override def fromRow(row: Row): SearchHistory = {
    SearchHistory(
      user_id(row),
      query_text(row),
      search_date(row)
    )
  }
}

abstract class ConcreteSearchHistory extends SearchHistories with RootConnector {

  def createTable() {
    Await.ready(create.ifNotExists().future(), 3.seconds)
  }

  def store(search: SearchHistory): Future[ResultSet] = {
    insert.value(_.user_id, search.userID)
      .value(_.query_text, search.queryText)
      .value(_.search_date, search.searchDate)
      .future()
  }

  def getByUserID(id: Long): Future[Seq[SearchHistory]] = {
    select.where(_.user_id eqs id).fetch()
  }

  def getAll(): Future[Seq[SearchHistory]] = {
    select.fetch()
  }
}


