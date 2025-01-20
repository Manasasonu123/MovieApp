package com.example.movieapp.ui.details

import com.example.movieapp.data.moviedetails.MovieDetails
import com.example.movieapp.remote.MovieInterface
import com.example.movieapp.utils.Constants
import com.example.movieapp.utils.Result
import com.example.movieapp.utils.Status
import java.lang.Exception
import javax.inject.Inject

class MovieDetailsRepo @Inject constructor( private val movieInterface: MovieInterface) {

    suspend fun getMovieDetails(imdbId: String): Result<MovieDetails> {


        return try {

            val response = movieInterface.getMovieDetails(imdbId, Constants.API_KEY)
            if (response.isSuccessful) {

                Result(Status.SUCCESS, response.body(), null)

            } else {
                Result(Status.ERROR, null, null)
            }


        } catch (e: Exception) {
            Result(Status.ERROR, null, null)
        }


    }

}
