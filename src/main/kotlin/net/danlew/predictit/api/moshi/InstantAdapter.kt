package net.danlew.predictit.api.moshi

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.time.*

internal object InstantAdapter {

  @ToJson
  @Suppress("UNUSED_PARAMETER")
  fun toJson(instant: Instant): ZonedDateTime = throw UnsupportedOperationException()

  @FromJson
  fun fromJson(zonedDateTime: ZonedDateTime): Instant = zonedDateTime.toInstant()

}