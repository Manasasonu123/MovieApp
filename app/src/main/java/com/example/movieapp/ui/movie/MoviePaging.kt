package com.example.movieapp.ui.movie

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.movieapp.data.Movie
import com.example.movieapp.remote.MovieInterface
import com.example.movieapp.utils.Constants

class MoviePaging(
    private val query: String?,
    private val year: String?,
    private val movieInterface: MovieInterface
) : PagingSource<Int, Movie>() {

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let {
            val anchorPage = state.closestPageToPosition(it)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val page = params.key ?: 1
        return try {
            val response = if (year.isNullOrEmpty()) {
                movieInterface.getAllMovies(query.orEmpty(), page, Constants.API_KEY)
            } else {
                movieInterface.getAllMoviesYear(query.orEmpty(), page, Constants.API_KEY, year)
            }
            val allMovies = response.body()?.Search ?: emptyList()
            Log.d("MoviePaging", "All Movies: $allMovies")
            LoadResult.Page(
                data = allMovies,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (allMovies.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}
