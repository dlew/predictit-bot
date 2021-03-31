package net.danlew.predictit.api

import net.danlew.predictit.model.MarketId
import net.danlew.predictit.model.MarketWithPrices

/**
 * Provides access to PredictIt's API.
 */
interface PredictItApi {

  /**
   * @return null if we couldn't load the markets
   */
  fun allMarkets(): Set<MarketWithPrices>?

  /**
   * @return null if we couldn't load the market
   */
  fun marketById(id: MarketId): MarketWithPrices?

}