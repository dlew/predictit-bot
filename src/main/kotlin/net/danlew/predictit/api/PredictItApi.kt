package net.danlew.predictit.api

import net.danlew.predictit.api.model.MarketWithPrices

/**
 * Provides access to PredictIt's API.
 */
interface PredictItApi {

  /**
   * @return null if we couldn't load the markets
   */
  fun allMarkets(): Set<MarketWithPrices>?

}