//package com.example.movieapp.ui.movie
//
//import android.util.Log
//import androidx.paging.PagingSource
//import androidx.paging.PagingState
//import com.example.movieapp.data.Movie
//import com.example.movieapp.remote.MovieInterface
//import com.example.movieapp.utils.Constants
//
//class MoviePaging(val s: String, val movieInterface: MovieInterface) : PagingSource<Int, Movie>() {
//
//    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
//        return state.anchorPosition?.let {
//            val anchorPage = state.closestPageToPosition(it)
//            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
//        }
//    }
//
//    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
//        val page = params.key ?: 1
//
//        return try {
//
//            val data = movieInterface.getAllMovies(s, page, Constants.API_KEY)
//            Log.d("TAG", "load: ${data.body()}")
//            LoadResult.Page(
//                data = data.body()?.Search!!,
//                prevKey = if (page == 1) null else page - 1,
//                nextKey = if (data.body()?.Search?.isEmpty()!!) null else page + 1
//            )
//
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//            LoadResult.Error(e)
//        }
//
//
//    }
//}
package com.example.movieapp.ui.movie

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.movieapp.data.Movie
import com.example.movieapp.remote.MovieInterface
import com.example.movieapp.utils.Constants

class MoviePaging(
    private val input: String?, // Input from either search or year filter
    private val movieInterface: MovieInterface,
    private val selectedYear:String?
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
            val response = movieInterface.getAllMovies(input.orEmpty(), page, Constants.API_KEY)
            val movies = response.body()?.Search ?: emptyList()

            Log.d("MoviePaging", "Loaded Movies: $movies")

            LoadResult.Page(
                data = movies,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (movies.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}
//override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
//    val page = params.key ?: 1
//
//    return try {
//        // Fetch movies from the API
//        val response = movieInterface.getAllMovies("", page, Constants.API_KEY)
//        val allMovies = response.body()?.Search ?: emptyList()
//
//        // Filter movies based on the selectedYear
//        val filteredMovies = if (!selectedYear.isNullOrEmpty()) {
//            allMovies.filter { movie ->
//                movie.Year == selectedYear // Only include movies that match the year
//            }
//        } else {
//            allMovies // No filter applied if selectedYear is null or empty
//        }
//
//        Log.d("MoviePaging", "Filtered Movies for Year $selectedYear: $filteredMovies")
//
//        LoadResult.Page(
//            data = filteredMovies,
//            prevKey = if (page == 1) null else page - 1,
//            nextKey = if (filteredMovies.isEmpty()) null else page + 1
//        )
//    } catch (e: Exception) {
//        e.printStackTrace()
//        LoadResult.Error(e)
//    }
//}




