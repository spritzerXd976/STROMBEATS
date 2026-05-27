package com.stormbeats.app.data.repository

import com.stormbeats.app.data.api.ApiClient
import com.stormbeats.app.data.model.Album
import com.stormbeats.app.data.model.ArtistDetail
import com.stormbeats.app.data.model.ArtistResult
import com.stormbeats.app.data.model.GitHubRelease
import com.stormbeats.app.data.model.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MusicRepository {
    private val saavn  = ApiClient.saavnApi
    private val github = ApiClient.githubApi

    suspend fun searchSongs(query: String, limit: Int = 20, page: Int = 1): Result<List<Song>> =
        withContext(Dispatchers.IO) {
            runCatching {
                val r = saavn.searchSongs(query, limit, page)
                if (r.isSuccessful) r.body()?.data?.results ?: emptyList()
                else throw Exception("Search failed: ${r.code()}")
            }
        }

    suspend fun searchAlbums(query: String, limit: Int = 10): Result<List<Song>> =
        withContext(Dispatchers.IO) {
            runCatching {
                val r = saavn.searchAlbums(query, limit)
                if (r.isSuccessful) r.body()?.data?.results ?: emptyList()
                else throw Exception("Album search failed: ${r.code()}")
            }
        }

    suspend fun searchArtists(query: String, limit: Int = 10): Result<List<ArtistResult>> =
        withContext(Dispatchers.IO) {
            runCatching {
                val r = saavn.searchArtists(query, limit)
                if (r.isSuccessful) r.body()?.data?.results ?: emptyList()
                else throw Exception("Artist search failed: ${r.code()}")
            }
        }

    suspend fun getAlbum(id: String): Result<Album> =
        withContext(Dispatchers.IO) {
            runCatching {
                val r = saavn.getAlbum(id)
                if (r.isSuccessful) r.body()?.data ?: throw Exception("Album not found")
                else throw Exception("Failed: ${r.code()}")
            }
        }

    suspend fun getArtist(id: String): Result<ArtistDetail> =
        withContext(Dispatchers.IO) {
            runCatching {
                val r = saavn.getArtist(id)
                if (r.isSuccessful) r.body()?.data ?: throw Exception("Artist not found")
                else throw Exception("Failed: ${r.code()}")
            }
        }

    suspend fun getArtistSongs(id: String, limit: Int = 10): Result<List<Song>> =
        withContext(Dispatchers.IO) {
            runCatching {
                val r = saavn.getArtistSongs(id, limit)
                if (r.isSuccessful) r.body()?.data?.songs ?: emptyList()
                else throw Exception("Failed: ${r.code()}")
            }
        }

    suspend fun getLatestRelease(owner: String, repo: String): Result<GitHubRelease> =
        withContext(Dispatchers.IO) {
            runCatching {
                val r = github.getLatestRelease(owner, repo)
                if (r.isSuccessful) r.body() ?: throw Exception("No release found")
                else throw Exception("GitHub API failed: ${r.code()}")
            }
        }
}
