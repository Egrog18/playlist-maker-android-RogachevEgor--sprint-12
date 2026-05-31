package com.example.playlist_maker_android_rogachevegor_.domain.api

import com.example.playlist_maker_android_rogachevegor_.data.dto.BaseResponse

interface NetworkClient {
    fun doRequest(dto: Any): BaseResponse
}
