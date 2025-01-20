//import dagger.hilt.android.lifecycle.HiltViewModel

////package com.example.movieapp
////
////import androidx.lifecycle.LiveData
////import androidx.lifecycle.MutableLiveData
////import androidx.lifecycle.ViewModel
////import androidx.lifecycle.switchMap
////import androidx.lifecycle.viewModelScope
////import androidx.paging.Pager
////import androidx.paging.PagingConfig
////import androidx.paging.cachedIn
////import androidx.paging.liveData
////import com.example.movieapp.data.moviedetails.MovieDetails
////import com.example.movieapp.remote.MovieInterface
////import com.example.movieapp.ui.details.MovieDetailsRepo
////import com.example.movieapp.ui.movie.MoviePaging
////import com.example.movieapp.utils.Events
////import com.example.movieapp.utils.Status
////import com.example.movieapp.utils.Result
////import dagger.hilt.android.lifecycle.HiltViewModel
////import kotlinx.coroutines.launch
////import javax.inject.Inject
////
////@HiltViewModel
////class MovieViewModel @Inject constructor(
////    private val movieInterface: MovieInterface,
////    private val repository: MovieDetailsRepo
////) : ViewModel() {
////
////
////    private val query = MutableLiveData<String>()
////
////
////    val list = query.switchMap { query ->
////        Pager(PagingConfig(pageSize = 10)) {
////            MoviePaging(query, movieInterface)
////        }.liveData.cachedIn(viewModelScope)
////    }
////
////    fun setQuery(s: String) {
////        query.postValue(s)
////    }
////
////    private val _movieDetails = MutableLiveData<Events<Result<MovieDetails>>>()
////    val movieDetails: LiveData<Events<Result<MovieDetails>>> = _movieDetails
////
////
////    fun getMovieDetails(imdbId: String) = viewModelScope.launch {
////        _movieDetails.postValue(Events(Result(Status.LOADING, null, null)))
////        _movieDetails.postValue(Events(repository.getMovieDetails(imdbId)))
////
////    }
////
////
////}
package com.example.movieapp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.liveData
import com.example.movieapp.data.Movie
import com.example.movieapp.data.moviedetails.MovieDetails
import com.example.movieapp.remote.MovieInterface
import com.example.movieapp.ui.details.MovieDetailsRepo
import com.example.movieapp.ui.movie.MoviePaging
import com.example.movieapp.utils.Events
import com.example.movieapp.utils.Status
import com.example.movieapp.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

////@HiltViewModel
////class MovieViewModel @Inject constructor(
////    private val movieInterface: MovieInterface,
////    private val repository: MovieDetailsRepo
////) : ViewModel() {
////
////    // Single active filter
////    private val activeFilter = MutableLiveData<Pair<String, String?>>()
////
////    // LiveData for paged movie list
////    val list: LiveData<PagingData<Movie>> = activeFilter.switchMap { filter ->
////        getPagedMovies(filter.first, filter.second)
////    }
////
////    private fun getPagedMovies(query: String?, yearFilter: String?): LiveData<PagingData<Movie>> {
////        val input = buildInput(query, yearFilter)
////        Log.d("MovieViewModel", "Getting paged movies with input: $input")
////        return Pager(PagingConfig(pageSize = 10)) {
////            MoviePaging(input, movieInterface) // Match the constructor
////        }.liveData.cachedIn(viewModelScope)
////    }
////
////    // Utility function to build the input string
////    private fun buildInput(query: String?, yearFilter: String?): String {
////        return when {
////            !query.isNullOrEmpty() && !yearFilter.isNullOrEmpty() -> "$query|$yearFilter"
////            !query.isNullOrEmpty() -> query
////            !yearFilter.isNullOrEmpty() -> yearFilter
////            else -> "" // Default if both are null/empty
////        }
////    }
////
////    // Set search query and clear year filter
////    fun setQuery(query: String) {
////        activeFilter.postValue(Pair(query, null)) // Set query as active filter
////    }
////
////    // Set year filter and clear search query
////    fun setYearFilter(year: String) {
////        activeFilter.postValue(Pair("", year)) // Set year filter as active filter
////    }
////
////    // Movie details LiveData
////    private val _movieDetails = MutableLiveData<Events<Result<MovieDetails>>>()
////    val movieDetails: LiveData<Events<Result<MovieDetails>>> = _movieDetails
////
////    // Fetch movie details
////    fun getMovieDetails(imdbId: String) = viewModelScope.launch {
////        _movieDetails.postValue(Events(Result(Status.LOADING, null, null)))
////        _movieDetails.postValue(Events(repository.getMovieDetails(imdbId)))
////    }
////}
//
@HiltViewModel
class MovieViewModel @Inject constructor(
    private val movieInterface: MovieInterface,
    private val repository: MovieDetailsRepo
) : ViewModel() {

    // LiveData for active filter
    private val activeFilter = MutableLiveData<Pair<String?, String?>>()

    // LiveData for paged movie list
    private val _list = activeFilter.switchMap { filter ->
        getPagedMovies(filter.first, filter.second)
    }
    val list: LiveData<PagingData<Movie>> = _list

    // Fetch paged movies based on query or year filter
    private fun getPagedMovies(query: String?, yearFilter: String?): LiveData<PagingData<Movie>> {
        val input = buildInput(query, yearFilter)
        Log.d("MovieViewModel", "Getting paged movies with input: $input")

        // Handle empty input case
        if (input.isEmpty()) {
            return MutableLiveData(PagingData.empty()) // Return empty PagingData
        }

        return Pager(PagingConfig(pageSize = 10)) {
            MoviePaging(input, movieInterface,yearFilter) // Data source for paging
        }.liveData.cachedIn(viewModelScope)
    }

    // Utility function to build the input string
    private fun buildInput(query: String?, yearFilter: String?): String {
        return when {
            !query.isNullOrEmpty() && !yearFilter.isNullOrEmpty() -> "$query|$yearFilter"
            !query.isNullOrEmpty() -> query
            !yearFilter.isNullOrEmpty() -> yearFilter
            else -> "" // Default if both are null/empty
        }
    }

    // Clear the movie list (used for "Not Applicable" case)
    fun clearMovies() {
        activeFilter.postValue(Pair(null, null)) // Clear all filters
    }

    // Set search query and clear year filter
    fun setQuery(query: String) {
        activeFilter.postValue(Pair(query, null)) // Only set query
    }

    // Set year filter and clear search query
    fun setYearFilter(year: String) {
        activeFilter.postValue(Pair("", year)) // Only set year filter
    }

    // LiveData for movie details
    private val _movieDetails = MutableLiveData<Events<Result<MovieDetails>>>()
    val movieDetails: LiveData<Events<Result<MovieDetails>>> = _movieDetails

    // Fetch movie details by IMDB ID
    fun getMovieDetails(imdbId: String) = viewModelScope.launch {
        _movieDetails.postValue(Events(Result(Status.LOADING, null, null))) // Show loading state
        try {
            val result = repository.getMovieDetails(imdbId)
            _movieDetails.postValue(Events(result))
        } catch (e: Exception) {
            Log.e("MovieViewModel", "Error fetching movie details", e)
            _movieDetails.postValue(Events(Result(Status.ERROR, null, e.message)))
        }
    }
}

