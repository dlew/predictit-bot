package net.danlew.predictit.api.moshi

import com.squareup.moshi.Moshi

internal val MOSHI = Moshi.Builder()
  .add(InstantAdapter)
  .add(MarketStatusAdapter)
  .add(ZonedDateTimeAdapter)
  .build()