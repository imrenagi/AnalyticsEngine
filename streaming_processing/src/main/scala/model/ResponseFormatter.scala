package model

import com.imrenagi.analytics.database.play.api.json.DefaultJsonFormatter
import model.Response._
import play.api.libs.json.Reads._
import play.api.libs.json._

/**
 * Created by imrenagi on 2/25/16.
 */

object ResponseFormatter extends DefaultJsonFormatter {

  implicit val projectReads: Reads[Project] = (
    (JsPath \ "id").read[Long] and
      (JsPath \ "name" \ "string").readNullable[String] and
      (JsPath \ "slug").read[String] and
      (JsPath \ "description").read[String] and
      (JsPath \ "cover").read[String] and
      (JsPath \ "created_at").read[Long] and
      (JsPath \ "updated_at").read[Long] and
      (JsPath \ "deleted_at").readNullable[Long] and
      (JsPath \ "status").read[Int] and
      (JsPath \ "account_id").read[Int] and
      (JsPath \ "vendor_id").read[Int] and
      (JsPath \ "category_id").read[Int] and
      (JsPath \ "external_id").readNullable[Long] and
      (JsPath \ "external_type").readNullable[Int] and
      (JsPath \ "location").read[String] and
      (JsPath \ "cover_temp").read[String]
    )(Project.apply _)


}
