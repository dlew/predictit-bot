package net.danlew.predictit

import net.danlew.predictit.api.NetworkPredictItApi

object Main {
  @JvmStatic
  fun main(args: Array<String>) {
    val predictItApi = NetworkPredictItApi()

    println(predictItApi.allMarkets())
  }
}
