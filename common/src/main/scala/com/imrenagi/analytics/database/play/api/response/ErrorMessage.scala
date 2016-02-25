package com.imrenagi.analytics.database.play.api.response

/**
 * Created by imrenagi on 2/25/16.
 */
import java.util.UUID

case class ErrorMessage(id: UUID = UUID.randomUUID(), status: Int, message: String, error_code: Int)

