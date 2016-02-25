package com.imrenagi.analytics

import java.util.UUID

import com.imrenagi.analytics.database.Db
import com.imrenagi.analytics.database.model.{TrendingSearch, SearchHistory, User}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkContext, SparkConf}
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormatter, DateTimeFormat}

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.{Success, Failure}

/**
 * Created by imrenagi on 12/28/15.
 */
object BatchProcessingApp {

  def main(args: Array[String]) {

    val conf = new SparkConf().setAppName("Simple Application").setMaster("local")
    val sc = new SparkContext(conf)

    val x = Db.search_by_user_id.getAll().onComplete{
      case Success(a: Seq[SearchHistory]) =>
        sc.parallelize(a).map(searchHistory =>
          (searchHistory.queryText, 1L)).reduceByKey(_ + _)
          .sortByKey(true, 1) // 1st arg configures ascending sort, 2nd arg configures one tas
          .map(KVP =>
            new TrendingSearch(today(), KVP._1, KVP._2)
          ).foreach(Db.trending_search_by_date.store(_))
    }
  }

  def today(): Long = {
    val dt = new DateTime();
    val fmt = DateTimeFormat.forPattern("ddMMyyyy");
    val dtStr = fmt.print(dt);
    dtStr.toInt
  }


}
