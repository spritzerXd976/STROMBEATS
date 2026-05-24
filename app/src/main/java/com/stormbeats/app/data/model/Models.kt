package com.stormbeats.app.data.model

import com.google.gson.annotations.SerializedName

// Search Response
data class SearchResponse(
    @SerializedName("status") val status: String,
    @SerializedName("data") val data: SearchData?
)

data class SearchData(
    @SerializedName("results") val results: List<Song> = emptyList(),
    @SerializedName("total") val total: Int = 0
)

// Song
data class Song(
    @SerializedName("id") val id: String = "",
    @SerializedName("name") val name: String = "",
    @SerializedName("type") val type: String = "",
    @SerializedName("year") val year: String? = null,
    @SerializedName("releaseDate") val releaseDate: String? = null,
    @SerializedName("duration") val duration: Int? = null,
    @SerializedName("label") val label: String? = null,
    @SerializedName("explicitContent") val explicitContent: Boolean = false,
    @SerializedName("playCount") val playCount: Int? = null,
    @SerializedName("language") val language: String? = null,
    @SerializedName("hasLyrics") val hasLyrics: Boolean = false,
    @SerializedName("url") val url: String? = null,
    @SerializedName("copyright") val copyright: String? = null,
    @SerializedName("album") val album: SongAlbum? = null,
    @SerializedName("artists") val artists: Artists? = null,
    @SerializedName("image") val image: List<ImageQuality>? = null,
    @SerializedName("downloadUrl") val downloadUrl: List<DownloadUrl>? = null
) {
    fun getImageUrl(): String {
        return image?.find { it.quality == "500x500" }?.url
            ?: image?.lastOrNull()?.url
            ?: ""
    }

    fun getStreamUrl(): String {
        return downloadUrl?.find { it.quality == "320kbps" }?.url
            ?: downloadUrl?.lastOrNull()?.url
            ?: ""
    }

    fun getPrimaryArtist(): String {
        return artists?.primary?.firstOrNull()?.name ?: "Unknown Artist"
    }

    fun getDurationSeconds(): Long = duration?.toLong() ?: 0L
}

data class SongAlbum(
    @SerializedName("id") val id: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("url") val url: String? = null
)

data class Artists(
    @SerializedName("primary") val primary: List<Artist>? = null,
    @SerializedName("featured") val featured: List<Artist>? = null,
    @SerializedName("all") val all: List<Artist>? = null
)

data class Artist(
    @SerializedName("id") val id: String = "",
    @SerializedName("name") val name: String = "",
    @SerializedName("role") val role: String? = null,
    @SerializedName("image") val image: List<ImageQuality>? = null,
    @SerializedName("type") val type: String? = null,
    @SerializedName("url") val url: String? = null
)

data class ImageQuality(
    @SerializedName("quality") val quality: String = "",
    @SerializedName("url") val url: String = ""
)

data class DownloadUrl(
    @SerializedName("quality") val quality: String = "",
    @SerializedName("url") val url: String = ""
)

// Album
data class AlbumResponse(
    @SerializedName("status") val status: String,
    @SerializedName("data") val data: Album?
)

data class Album(
    @SerializedName("id") val id: String = "",
    @SerializedName("name") val name: String = "",
    @SerializedName("description") val description: String? = null,
    @SerializedName("year") val year: Int? = null,
    @SerializedName("type") val type: String? = null,
    @SerializedName("playCount") val playCount: Int? = null,
    @SerializedName("language") val language: String? = null,
    @SerializedName("explicitContent") val explicitContent: Boolean = false,
    @SerializedName("url") val url: String? = null,
    @SerializedName("image") val image: List<ImageQuality>? = null,
    @SerializedName("artists") val artists: Artists? = null,
    @SerializedName("songs") val songs: List<Song>? = null
)

// Trending / Charts
data class TrendingResponse(
    @SerializedName("status") val status: String,
    @SerializedName("data") val data: List<Song>?
)

// GitHub Release for auto-update
data class GitHubRelease(
    @SerializedName("tag_name") val tagName: String,
    @SerializedName("name") val name: String,
    @SerializedName("body") val body: String,
    @SerializedName("assets") val assets: List<GitHubAsset>
)

data class GitHubAsset(
    @SerializedName("name") val name: String,
    @SerializedName("browser_download_url") val downloadUrl: String,
    @SerializedName("size") val size: Long
)
