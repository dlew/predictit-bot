package net.danlew.predictit.analyzer

import net.danlew.predictit.model.MarketStatus
import net.danlew.predictit.model.MarketWithPrices
import net.danlew.predictit.model.Notification
import net.danlew.predictit.test.basicContractPrices
import net.danlew.predictit.test.basicMarket
import org.junit.Assert.assertEquals
import org.junit.Test

class MarketClosedAnalyzerTest {

  @Test
  fun analyze() {
    val openMarket = basicMarket().copy(status = MarketStatus.OPEN)
    val openMarketWithPrices = MarketWithPrices(openMarket, basicContractPrices())
    val closedMarket = basicMarket().copy(status = MarketStatus.CLOSED)
    val closedMarketWithPrices = MarketWithPrices(closedMarket, basicContractPrices())

    val expectedNotification = Notification(
      marketId = openMarket.id,
      contractId = null,
      type = Notification.Type.MARKET_CLOSED
    )

    // Closing an open market - that's what we're looking for!
    assertEquals(
      setOf(expectedNotification),
      MarketClosedAnalyzer.analyze(openMarket, closedMarketWithPrices)
    )

    // Open markets aren't closed
    assertEquals(
      emptySet<Notification>(),
      MarketClosedAnalyzer.analyze(openMarket, openMarketWithPrices)
    )

    assertEquals(
      emptySet<Notification>(),
      MarketClosedAnalyzer.analyze(closedMarket, openMarketWithPrices)
    )

    // Already closed markets don't count
    assertEquals(
      emptySet<Notification>(),
      MarketClosedAnalyzer.analyze(closedMarket, closedMarketWithPrices)
    )

    // If we didn't know the market's state before, we don't declare it closed now
    assertEquals(
      emptySet<Notification>(),
      MarketClosedAnalyzer.analyze(null, openMarketWithPrices)
    )

    assertEquals(
      emptySet<Notification>(),
      MarketClosedAnalyzer.analyze(null, closedMarketWithPrices)
    )
  }

}