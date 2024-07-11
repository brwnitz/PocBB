package com.example.poc_bb.Interfaces

import com.example.poc_bb.Models.PixChargeRequest
import com.example.poc_bb.Models.PixChargeResponse
import com.example.poc_bb.Models.TokenResponse
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
        @Query("gw-dev-app-key") gwDevAppKey: String,
        @Header("Authorization") authorization: String,
        @Header("Content-Type") contentType: String = "application/json",
        @Body pixChargeRequest: PixChargeRequest) : Call<PixChargeResponse>

    @POST("oauth/token")
    @FormUrlEncoded
    fun getToken(
        @Header("Authorization") authorization: String,
        @Field("grant_type") grantType: String = "client_credentials",
        @Field("scope") scope: String = "cob.write&20cob.read",
    ): Call<TokenResponse>

}