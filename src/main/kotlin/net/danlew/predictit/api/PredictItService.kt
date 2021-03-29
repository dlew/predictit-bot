package net.danlew.predictit.api

import net.danlew.predictit.api.model.ApiMarketList
import retrofit2.Call
import retrofit2.http.GET

internal interface PredictItService {

  @GET("all")
  fun allMarkets(): Call<ApiMarketList>

}