package net.danlew.predictit.api.moshi

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.time.*

internal object ZonedDateTimeAdapter {

  // PredictIt doesn't explicitly state the timezone, but I assume it's this one
  private val zoneId = ZoneId.of("America/New_York")

  @ToJson
  @Suppress("UNUSED_PARAMETER")
  fun toJson(zonedDateTime: ZonedDateTime): String = throw UnsupportedOperationException()

  @FromJson
  fun fromJson(localDateTime: String): ZonedDateTime = ZonedDateTime.of(LocalDateTime.parse(localDateTime), zoneId)

}