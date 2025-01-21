package com.example.movieapp

import android.util.Log
import androidx.lifecycle.*
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
import com.example.movieapp.utils.Result
import com.example.movieapp.utils.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(
    private val movieInterface: MovieInterface,
    private val repository: MovieDetailsRepo
) : ViewModel() {

    private val activeFilter = MutableLiveData<Pair<String?, String?>>()

    val list: LiveData<PagingData<Movie>> = activeFilter.switchMap { filter ->
        getPagedMovies(filter.first, filter.second)
    }

    private fun getPagedMovies(query: String?, yearFilter: String?): LiveData<PagingData<Movie>> {
        val input = buildInput(query, yearFilter)
        Log.d("MovieViewModel", "Getting paged movies with input: $input")

        return Pager(PagingConfig(pageSize = 10)) {
            MoviePaging(query, yearFilter, movieInterface)
        }.liveData.cachedIn(viewModelScope)
    }

    private fun buildInput(query: String?, yearFilter: String?): String {
        return when {
            !query.isNullOrEmpty() && !yearFilter.isNullOrEmpty() -> "$query|$yearFilter"
            !query.isNullOrEmpty() -> query
            !yearFilter.isNullOrEmpty() -> yearFilter
            else -> ""
        }
    }

    fun setQuery(query: String) {
        activeFilter.postValue(Pair(query, null))
    }

    fun setMovieYearFilter(query: String, year: String) {
        activeFilter.postValue(Pair(query, year))
    }

    fun clearMovies() {
        activeFilter.postValue(Pair(null, null))
    }

    private val _movieDetails = MutableLiveData<Events<Result<MovieDetails>>>()
    val movieDetails: LiveData<Events<Result<MovieDetails>>> = _movieDetails

    fun getMovieDetails(imdbId: String) = viewModelScope.launch {
        _movieDetails.postValue(Events(Result(Status.LOADING, null, null)))
        try {
            val result = repository.getMovieDetails(imdbId)
            _movieDetails.postValue(Events(result))
        } catch (e: Exception) {
            Log.e("MovieViewModel", "Error fetching movie details", e)
            _movieDetails.postValue(Events(Result(Status.ERROR, null, e.message)))
        }
    }
}
