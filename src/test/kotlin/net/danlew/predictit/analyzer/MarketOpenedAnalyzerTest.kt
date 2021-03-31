package net.danlew.predictit.analyzer

import net.danlew.predictit.model.*
import net.danlew.predictit.test.basicContractPrices
import net.danlew.predictit.test.basicMarket
import org.junit.Assert.*
import org.junit.Test

class MarketOpenedAnalyzerTest {

  @Test
  fun analyze() {
    val openMarket = basicMarket().copy(status = MarketStatus.OPEN)
    val openMarketWithPrices = MarketWithPrices(openMarket, basicContractPrices())
    val closedMarket = basicMarket().copy(status = MarketStatus.CLOSED)
    val closedMarketWithPrices = MarketWithPrices(closedMarket, basicContractPrices())

    val expectedNotification = Notification(
      marketId = openMarket.id,
      contractId = null,
      type = Notification.Type.MARKET_OPENED
    )

    // Closed or previously unseen markets can be open
    assertEquals(
      setOf(expectedNotification),
      MarketOpenedAnalyzer.analyze(null, openMarketWithPrices)
    )

    assertEquals(
      setOf(expectedNotification),
      MarketOpenedAnalyzer.analyze(closedMarket, openMarketWithPrices)
    )

    // An already-open Market has not just opened
    assertEquals(
      emptySet<Notification>(),
      MarketOpenedAnalyzer.analyze(openMarket, openMarketWithPrices)
    )

    // A closed Market is never open
    assertEquals(
      emptySet<Notification>(),
      MarketOpenedAnalyzer.analyze(closedMarket, closedMarketWithPrices)
    )

    assertEquals(
      emptySet<Notification>(),
      MarketOpenedAnalyzer.analyze(openMarket, closedMarketWithPrices)
    )

    assertEquals(
      emptySet<Notification>(),
      MarketOpenedAnalyzer.analyze(null, closedMarketWithPrices)
    )
  }

}