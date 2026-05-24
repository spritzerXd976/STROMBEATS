package com.stormbeats.app.data.repository

import com.stormbeats.app.data.api.ApiClient
import com.stormbeats.app.data.model.Album
import com.stormbeats.app.data.model.GitHubRelease
import com.stormbeats.app.data.model.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MusicRepository {

    private val saavnApi = ApiClient.saavnApi
    private val githubApi = ApiClient.githubApi

    suspend fun searchSongs(query: String, page: Int = 1): Result<List<Song>> = withContext(Dispatchers.IO) {
        try {
            val response = saavnApi.searchSongs(query, page = page)
            if (response.isSuccessful) {
                val songs = response.body()?.data?.results ?: emptyList()
                Result.success(songs)
            } else {
                Result.failure(Exception("Search failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchAlbums(query: String): Result<List<Song>> = withContext(Dispatchers.IO) {
        try {
            val response = saavnApi.searchAlbums(query)
            if (response.isSuccessful) {
                val results = response.body()?.data?.results ?: emptyList()
                Result.success(results)
            } else {
                Result.failure(Exception("Album search failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAlbum(id: String): Result<Album> = withContext(Dispatchers.IO) {
        try {
            val response = saavnApi.getAlbum(id)
            if (response.isSuccessful) {
                val album = response.body()?.data
                if (album != null) Result.success(album)
                else Result.failure(Exception("Album not found"))
            } else {
                Result.failure(Exception("Failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getLatestRelease(owner: String, repo: String): Result<GitHubRelease> = withContext(Dispatchers.IO) {
        try {
            val response = githubApi.getLatestRelease(owner, repo)
            if (response.isSuccessful) {
                val release = response.body()
                if (release != null) Result.success(release)
                else Result.failure(Exception("No release found"))
            } else {
                Result.failure(Exception("GitHub API failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
