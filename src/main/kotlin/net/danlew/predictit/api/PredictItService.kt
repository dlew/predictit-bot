package net.danlew.predictit.api

import net.danlew.predictit.api.model.ApiMarket
import net.danlew.predictit.api.model.ApiMarketList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

internal interface PredictItService {

  @GET("all")
  fun allMarkets(): Call<ApiMarketList>

  @GET("markets/{marketId}")
  fun marketById(@Path("marketId") marketId: Long): Call<ApiMarket>

}