package com.stormbeats.app.data.api

import com.stormbeats.app.data.model.AlbumResponse
import com.stormbeats.app.data.model.ArtistResponse
import com.stormbeats.app.data.model.ArtistSongsResponse
import com.stormbeats.app.data.model.GitHubRelease
import com.stormbeats.app.data.model.SearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SaavnApiService {

    @GET("api/search/songs")
    suspend fun searchSongs(
        @Query("query") query: String,
        @Query("limit") limit: Int = 20,
        @Query("page")  page: Int  = 1
    ): Response<SearchResponse>

    @GET("api/search/albums")
    suspend fun searchAlbums(
        @Query("query") query: String,
        @Query("limit") limit: Int = 10
    ): Response<SearchResponse>

    @GET("api/search/artists")
    suspend fun searchArtists(
        @Query("query") query: String,
        @Query("limit") limit: Int = 10
    ): Response<ArtistSearchResponse>

    @GET("api/albums")
    suspend fun getAlbum(
        @Query("id") id: String
    ): Response<AlbumResponse>

    @GET("api/songs/{id}")
    suspend fun getSong(
        @Path("id") id: String
    ): Response<SearchResponse>

    @GET("api/artists/{id}")
    suspend fun getArtist(
        @Path("id") id: String
    ): Response<ArtistResponse>

    @GET("api/artists/{id}/songs")
    suspend fun getArtistSongs(
        @Path("id") id: String,
        @Query("limit") limit: Int = 10
    ): Response<ArtistSongsResponse>
}

interface GitHubApiService {
    @GET("repos/{owner}/{repo}/releases/latest")
    suspend fun getLatestRelease(
        @Path("owner") owner: String,
        @Path("repo")  repo:  String
    ): Response<GitHubRelease>
}
