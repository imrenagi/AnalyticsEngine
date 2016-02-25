package model

import com.imrenagi.analytics.database.play.api.json.DefaultJsonFormatter

/**
 * Created by imrenagi on 2/25/16.
 */

object Response {

  case class Project(id: Long,
                    name: Option[String],
                    slug: String,
                    description: String,
                    cover: String,
                    created_at: Long,
                    updated_at: Long,
                    deleted_at: Option[Long],
                    status: Int,
                    account_id: Int,
                    vendor_id: Int,
                    category_id: Int,
                    external_id: Option[Long],
                    external_type: Option[Int],
                    location: String,
                    cover_temp: String
                      )
}


