package net.danlew.predictit.api.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class ApiMarketList(
  val markets: List<ApiMarket>
)