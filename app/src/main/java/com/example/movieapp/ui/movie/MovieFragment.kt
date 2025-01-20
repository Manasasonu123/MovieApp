//package com.example.movieapp.ui.movie
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.AdapterView
//import androidx.appcompat.widget.SearchView
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.viewModels
//import androidx.navigation.fragment.findNavController
//import androidx.recyclerview.widget.GridLayoutManager
//import com.example.movieapp.MovieViewModel
//import com.example.movieapp.databinding.FragmentMovieBinding
//import com.example.movieapp.ui.movie.MoviePagingAdapter
//import dagger.hilt.android.AndroidEntryPoint
//
//
//@AndroidEntryPoint
//class MovieFragment : Fragment() {
//    lateinit var binding: FragmentMovieBinding
//
//    val viewModel: MovieViewModel by viewModels()
//
//
//    val movieAdapter = MoviePagingAdapter()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//
//        binding = FragmentMovieBinding.inflate(inflater, container, false)
//        return binding.root
//
//
//    }
//
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        setRecyclerView()
//
////        // Handle spinner selection
////        binding.yearSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
////            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
////                val selectedYear = parent?.getItemAtPosition(position).toString()
////                viewModel.setQuery(selectedYear) // Pass the selected year to the ViewModel
////            }
////
////            override fun onNothingSelected(parent: AdapterView<*>?) {
////                // No-op
////            }
////        }
//
//        binding.movieSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                query?.let {
//                    viewModel.setQuery(it)
//                }
//                return true
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                return false
//            }
//        })
//
//        movieAdapter.onMovieClick {
//            val action=MovieFragmentDirections.actionMovieFragmentToDetailsFragment(it)
//            findNavController().navigate(action)
//        }
//
//        viewModel.list.observe(viewLifecycleOwner) {
//            movieAdapter.submitData(lifecycle, it)
//        }
//
//
//    }
//
//    private fun setRecyclerView() {
//        binding.movieRecycler.apply {
//            adapter = movieAdapter
//            layoutManager = GridLayoutManager(requireContext(), 2)
//        }
//    }
//
//
//}
package com.example.movieapp.ui.movie

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.movieapp.MovieViewModel
import com.example.movieapp.databinding.FragmentMovieBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MovieFragment : Fragment() {
    lateinit var binding: FragmentMovieBinding

    val viewModel: MovieViewModel by viewModels()

    val movieAdapter = MoviePagingAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMovieBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecyclerView()

        // Handle spinner selection for year filter
        binding.yearSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedYear = parent?.getItemAtPosition(position).toString()
                when (selectedYear) {
                    "Not Applicable" -> {
                        // Clear movies and show blank page
//                        viewModel.clearMovies()
                        viewModel.setYearFilter("")
                    }
                    "Select Year" -> {
                        // No-op, wait for a valid selection
                    }
                    else -> {
                        // Clear search query and set year filter
                        binding.movieSearch.setQuery("", false) // Clear the search bar
                        viewModel.setQuery(selectedYear)       // Fetch movies for the selected year
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No-op
            }
        }



        // Handle search query
        binding.movieSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    binding.yearSpinner.setSelection(0) // Reset spinner to default
                    viewModel.setQuery(it)             // Use search query as input
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        // Set up movie click listener for navigation
        movieAdapter.onMovieClick {
            val action = MovieFragmentDirections.actionMovieFragmentToDetailsFragment(it)
            findNavController().navigate(action)
        }

        // Observe the list of movies from ViewModel
        viewModel.list.observe(viewLifecycleOwner) { pagingData ->
            pagingData?.let {
                movieAdapter.submitData(lifecycle, it)
            } ?: run {
                Log.e("MovieFragment", "PagingData is null")
            }
        }
    }

    private fun setRecyclerView() {
        binding.movieRecycler.apply {
            adapter = movieAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }
}
