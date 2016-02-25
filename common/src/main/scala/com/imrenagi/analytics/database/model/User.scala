package com.imrenagi.analytics.database.model

import com.imrenagi.analytics.database.Db
import com.websudos.phantom.CassandraTable
import com.websudos.phantom.connectors.RootConnector
import com.websudos.phantom.dsl._
import org.joda.time.DateTime
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/**
 * Created by imrenagi on 2/6/16.
 */
case class User(id: UUID,email: String, name: String, registrationDate: DateTime)

class Users extends CassandraTable[ConcreteUser, User] {
  object id extends UUIDColumn(this) with PartitionKey[UUID]
  object email extends StringColumn(this)
  object name extends StringColumn(this)
  object registrationDate extends DateTimeColumn(this)

  override def fromRow(r: Row): User = {
    User(
      id(r),
      email(r),
      name(r),
      registrationDate(r)
    )
  }
}

abstract class ConcreteUser extends Users with RootConnector {

  def createTable() {
    Await.ready(create.ifNotExists().future(), 3.seconds)
  }

  def store(user: User): Future[ResultSet] = {
    insert.value(_.id, user.id)
      .value(_.email, user.email)
      .value(_.name, user.name)
      .value(_.registrationDate, user.registrationDate)
      .future()
  }

  def getById(id: UUID): Future[Option[User]] = {
    select.where(_.id eqs id).one()
  }
}

