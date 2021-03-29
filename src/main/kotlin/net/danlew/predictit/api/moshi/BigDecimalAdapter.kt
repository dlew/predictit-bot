package net.danlew.predictit.api.moshi

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.math.BigDecimal

internal object BigDecimalAdapter {

  @ToJson
  @Suppress("UNUSED_PARAMETER")
  fun toJson(bigDecimal: BigDecimal): String = throw UnsupportedOperationException()

  @FromJson
  fun fromJson(bigDecimal: String): BigDecimal = BigDecimal(bigDecimal)

}