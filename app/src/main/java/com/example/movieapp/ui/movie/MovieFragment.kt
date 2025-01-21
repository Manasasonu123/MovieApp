package com.example.movieapp

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
import com.example.movieapp.ui.movie.MoviePagingAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieFragment : Fragment() {

    private lateinit var binding: FragmentMovieBinding
    private val viewModel: MovieViewModel by viewModels()
    private val movieAdapter = MoviePagingAdapter()
    private var query: String? = null

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

        binding.movieSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(input: String?): Boolean {
                input?.let {
                    binding.yearSpinner.setSelection(0)
                    viewModel.setQuery(it)
                    query=it
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        binding.yearSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedYear = parent?.getItemAtPosition(position).toString()
                if (selectedYear == "Not Applicable") {
                    viewModel.setMovieYearFilter(query ?: "", "")
                } else {
                    viewModel.setMovieYearFilter(query ?: "", selectedYear)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No-op
            }
        }

        movieAdapter.onMovieClick {
            val action = MovieFragmentDirections.actionMovieFragmentToDetailsFragment(it)
            findNavController().navigate(action)
        }

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
