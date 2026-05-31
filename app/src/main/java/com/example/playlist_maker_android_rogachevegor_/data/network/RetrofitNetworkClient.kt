package com.example.playlist_maker_android_rogachevegor_.data.network

import com.example.playlist_maker_android_rogachevegor_.data.dto.BaseResponse
import com.example.playlist_maker_android_rogachevegor_.data.dto.TrackDto
import com.example.playlist_maker_android_rogachevegor_.data.dto.TracksSearchRequest
import com.example.playlist_maker_android_rogachevegor_.data.dto.TracksSearchResponse
import com.example.playlist_maker_android_rogachevegor_.domain.api.NetworkClient
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import org.json.JSONObject

class RetrofitNetworkClient : NetworkClient {

    override fun doRequest(dto: Any): BaseResponse {
        if (dto !is TracksSearchRequest) {
            return BaseResponse().apply { resultCode = 400 }
        }

        var connection: HttpURLConnection? = null

        return try {
            val encodedExpression = URLEncoder.encode(dto.expression, Charsets.UTF_8.name())
            val url = URL("$ITUNES_SEARCH_URL?term=$encodedExpression&entity=song")
            connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = CONNECTION_TIMEOUT
                readTimeout = READ_TIMEOUT
            }

            val responseCode = connection.responseCode
            if (responseCode in HTTP_SUCCESS_CODES) {
                val responseText = connection.inputStream.bufferedReader().use { reader ->
                    reader.readText()
                }
                TracksSearchResponse(parseTracks(responseText)).apply {
                    resultCode = responseCode
                }
            } else {
                BaseResponse().apply { resultCode = responseCode }
            }
        } catch (exception: IOException) {
            throw exception
        } catch (exception: Exception) {
            BaseResponse().apply { resultCode = 500 }
        } finally {
            connection?.disconnect()
        }
    }

    private fun parseTracks(responseText: String): List<TrackDto> {
        val results = JSONObject(responseText).optJSONArray("results") ?: return emptyList()
        return buildList {
            for (index in 0 until results.length()) {
                val item = results.optJSONObject(index) ?: continue
                add(
                    TrackDto(
                        trackId = item.optionalLong("trackId"),
                        trackName = item.optionalString("trackName"),
                        artistName = item.optionalString("artistName"),
                        trackTimeMillis = item.optionalLong("trackTimeMillis"),
                        artworkUrl100 = item.optionalString("artworkUrl100")
                    )
                )
            }
        }
    }

    private fun JSONObject.optionalString(name: String): String? {
        return if (has(name) && !isNull(name)) optString(name) else null
    }

    private fun JSONObject.optionalLong(name: String): Long? {
        return if (has(name) && !isNull(name)) optLong(name) else null
    }

    companion object {
        private const val ITUNES_SEARCH_URL = "https://itunes.apple.com/search"
        private const val CONNECTION_TIMEOUT = 10_000
        private const val READ_TIMEOUT = 10_000
        private val HTTP_SUCCESS_CODES = 200..299
    }
}
