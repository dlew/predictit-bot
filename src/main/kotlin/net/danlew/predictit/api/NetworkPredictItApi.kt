package net.danlew.predictit.api

import net.danlew.predictit.api.model.ApiMarket
import net.danlew.predictit.api.model.ApiMarketList
import net.danlew.predictit.model.MarketWithPrices
import net.danlew.predictit.api.moshi.MOSHI
import net.danlew.predictit.model.MarketId
import org.slf4j.LoggerFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Makes actual network calls to PredictIt's API.
 *
 * Do not use in tests.
 */
class NetworkPredictItApi : PredictItApi {

  private val retrofit = Retrofit.Builder()
    .baseUrl("https://www.predictit.org/api/marketdata/")
    .addConverterFactory(MoshiConverterFactory.create(MOSHI))
    .build()

  private val service = retrofit.create(PredictItService::class.java)

  private val logger = LoggerFactory.getLogger(NetworkPredictItApi::class.java)

  override fun allMarkets(): Set<MarketWithPrices>? {
    logger.debug("Querying allMarkets()")

    val response: Response<ApiMarketList>
    try {
      response = service.allMarkets().execute()
    } catch (ex: Exception) {
      logger.warn("Could not retrieve all markets", ex)
      return null
    }

    if (!response.isSuccessful) {
      logger.warn("Could not retrieve all markets, status=${response.code()}")
      return null
    }

    return response.body()!!.markets.map(ApiMarket::toMarketWithPrices).toSet()
  }

  override fun marketById(id: MarketId): MarketWithPrices? {
    logger.debug("Querying marketById($id)")

    val response: Response<ApiMarket>
    try {
      response = service.marketById(id.id).execute()
    } catch (ex: Exception) {
      logger.warn("Could not retrieve market $id", ex)
      return null
    }

    if (!response.isSuccessful) {
      logger.warn("Could not retrieve market $id, status=${response.code()}")
      return null
    }

    return response.body()!!.toMarketWithPrices()
  }
}