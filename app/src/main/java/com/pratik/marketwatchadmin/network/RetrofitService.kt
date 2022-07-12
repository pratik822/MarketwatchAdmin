package com.pratik.marketwatchadmin.network

import com.pratik.marketwatchadmin.data.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.*


interface RetrofitService {
    @GET("getcreateData.php")
    suspend fun getPost(): Response<ResponseData>

    @Headers("Accept: application/json")
    @POST("createData.php")
    fun createPost(@Body addPostData: AddPostData): Call<Req>

    @Headers("Accept: application/json")
    @POST("updatecreateData.php")
    fun updateCreatePost(@Body addPostData: AddPostData): Call<Req>

    @FormUrlEncoded
    @POST("deletecreateData.php")
    fun deleteCreatePost(@Field("id")id:String): Call<Req>
}


