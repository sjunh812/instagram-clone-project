package org.sjhstudio.instagramclone.network

import okhttp3.ResponseBody
import org.sjhstudio.instagramclone.model.PushDTO
import org.sjhstudio.instagramclone.util.Val
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface PushApi {

    @Headers("Authorization: key=${Val.PUSH_SERVER_KEY}", "Content-Type: ${Val.PUSH_CONTENT_TYPE}")
    @POST("/fcm/send")
    suspend fun sendMessage(@Body push: PushDTO): Response<ResponseBody>

}