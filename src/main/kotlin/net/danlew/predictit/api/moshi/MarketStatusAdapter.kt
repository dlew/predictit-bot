package net.danlew.predictit.api.moshi

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import net.danlew.predictit.model.MarketStatus

internal object MarketStatusAdapter {

  @ToJson
  @Suppress("UNUSED_PARAMETER")
  fun toJson(marketStatus: MarketStatus): String = throw UnsupportedOperationException()

  @FromJson
  fun fromJson(marketStatus: String): MarketStatus =
    when (marketStatus) {
      "Open" -> MarketStatus.OPEN
      "Closed" -> MarketStatus.CLOSED
      else -> throw IllegalArgumentException("Unknown market status: $marketStatus")
    }

}