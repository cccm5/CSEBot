package com.cccm5.cseBot.util.imgur

import com.squareup.moshi.Json

data class ImgurRequest(
        @Json(name = "data")val image: ImgurImage,
        val status: Number,
        val success: Boolean)