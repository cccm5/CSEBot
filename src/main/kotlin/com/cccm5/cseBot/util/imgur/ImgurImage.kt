package com.cccm5.cseBot.util.imgur

import com.squareup.moshi.Json

data class ImgurImage(
        val id: String,
        val title: String? = null,
        val description: String? = null,
        val datetime: Int,
        val type: String,
        val animated: Boolean,
        val width: Int,
        val height: Int,
        val size: Int,
        val views: Int,
        val bandwith: Int,
        val deletehash: String? = null,
        val name: String? = null,
        val section: String? = null,
        val link: String,
        val gifv: String? = null,
        val mp4: String? = null,
        val mp4_size: String? = null,
        val looping: Boolean? = null,
        val favorite: Boolean,
        val nsfw: Boolean,
        val vote: Boolean? = null,
        val in_gallery: Boolean,
        val error: String?,
        val request: String?,
        val method: String?){

}