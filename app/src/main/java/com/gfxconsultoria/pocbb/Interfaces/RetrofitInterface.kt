package com.gfxconsultoria.pocbb.Interfaces

import com.gfxconsultoria.pocbb.Models.PixChargeRequest
import com.gfxconsultoria.pocbb.Models.PixChargeResponse
import com.gfxconsultoria.pocbb.Models.TokenResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface RetrofitInterface {
    @POST("pix/v2/cob")
    fun createPix(
        @Query("gw-dev-app-key") gwDevAppKey: String?,
        @Header("Authorization") authorization: String,
        @Header("Content-Type") contentType: String = "application/json",
        @Body pixChargeRequest: PixChargeRequest
    ) : Call<PixChargeResponse>

    @POST("oauth/token")
    @FormUrlEncoded
    fun getToken(
        @Header("Authorization") authorization: String,
        @Field("content-type") contentType: String = "application/x-www-form-urlencoded",
        @Field("grant_type") grantType: String = "client_credentials",
    ): Call<TokenResponse>

}