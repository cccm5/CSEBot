package com.cccm5.cseBot.util.imgur

import com.cccm5.cseBot.keyMap
import com.cccm5.cseBot.logger
import com.github.scribejava.apis.LinkedInApi20
import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.model.OAuthRequest
import com.github.scribejava.core.model.Verb
import com.github.scribejava.core.oauth.OAuth20Service
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi

private val CLIENT_ID: String = keyMap["IMGUR_CLIENT_ID"]!!
private val API_SECRET: String = keyMap["IMGUR_API_SECRET"]!!
private val API_KEY: String = keyMap["IMGUR_API_KEY"]!!
private val adapter: JsonAdapter<ImgurRequest> = Moshi.Builder().add(KotlinJsonAdapterFactory()).build().adapter(ImgurRequest::class.java)
private val service: OAuth20Service = ServiceBuilder(API_KEY)
        .apiSecret(API_SECRET)
        .build<OAuth20Service>(LinkedInApi20.instance())

fun getImageFromId(id: String): ImgurImage?{
    val request = OAuthRequest(Verb.GET,"https://api.imgur.com/3/image/$id.json" )
    request.addHeader("Client-ID", CLIENT_ID)
    val response = service.execute(request) ?: return null
    if(!response.isSuccessful){
        logger.warn("Error on imgur query for $id ")
        return null
    }
    val imgurRequest = adapter.fromJson(response.message) ?: return null
    if(!imgurRequest.success){
        logger.warn("Imgur request Error: ${imgurRequest.status} ${imgurRequest.image.error}")
    }
    return imgurRequest.image
}