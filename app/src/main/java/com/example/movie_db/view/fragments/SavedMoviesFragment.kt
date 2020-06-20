package com.example.movie_db.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.movie_db.R
import com.example.movie_db.model.data.movie.Movie
import androidx.lifecycle.Observer
import com.example.movie_db.view_model.MovieListViewModel
import androidx.fragment.app.activityViewModels
import com.example.movie_db.view.adapters.FavouritesAdapter
import com.example.movie_db.view_model.SharedViewModel
import kotlinx.android.synthetic.main.main_menu_layout.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class SavedMoviesFragment : Fragment(), FavouritesAdapter.RecyclerViewItemClick {

    private lateinit var recyclerViewFav: RecyclerView
    private lateinit var swipeRefreshLayoutFav: SwipeRefreshLayout
    private val movieListViewModel by viewModel<MovieListViewModel>()
    private lateinit var layoutManager: GridLayoutManager
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var toolbar: TextView
    private lateinit var favAdapter: FavouritesAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sharedViewModel.savedMovies.observe(viewLifecycleOwner, Observer { item ->
            if (item.liked) favAdapter.addItem(item)
            else favAdapter.removeItem(item)
        })
    }

    override fun onResume() {
        super.onResume()
        swipeRefreshLayoutFav.isRefreshing = true
        requireActivity().bottom_navigation.visibility = View.VISIBLE
        swipeRefreshLayoutFav.isRefreshing = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.main_menu_decor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViews(view)
        refresh()
        setAdapter()
        getFavMoviesCoroutine()
    }

    private fun bindViews(view: View) {
        swipeRefreshLayoutFav = view.findViewById(R.id.swipeInFragments)
        recyclerViewFav = view.findViewById(R.id.recycler_view)
        toolbar = view.findViewById(R.id.toolbar)
        toolbar.text = getString(R.string.favorites)
    }

    private fun refresh() {
        recyclerViewFav.layoutManager = LinearLayoutManager(requireActivity())
        swipeRefreshLayoutFav.setOnRefreshListener {
            favAdapter.clearAll()
            movieListViewModel.getLikedMovieList()
        }
    }

    private fun setAdapter() {
        layoutManager = GridLayoutManager(requireActivity(), 3)
        recyclerViewFav.layoutManager = layoutManager
        favAdapter = FavouritesAdapter(this, requireActivity())
        recyclerViewFav.adapter = favAdapter
    }

    private fun getFavMoviesCoroutine() {
        movieListViewModel.getLikedMovieList()
        movieListViewModel.liveData.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is MovieListViewModel.State.ShowLoading -> {
                    swipeRefreshLayoutFav.isRefreshing = true
                }
                is MovieListViewModel.State.HideLoading -> {
                    swipeRefreshLayoutFav.isRefreshing = false
                }
                is MovieListViewModel.State.Result -> {
                    favAdapter.addItems(result.list!!)
                }
            }
        })
    }

    override fun itemClick(position: Int, item: Movie) {
        val bundle = Bundle()
        bundle.putInt("id", item.id)
        val movieInfoFragment =
            MovieInfoFragment()
        movieInfoFragment.arguments = bundle
        parentFragmentManager.beginTransaction().add(R.id.frameLayout, movieInfoFragment).addToBackStack(null).commit()
        requireActivity().bottom_navigation.visibility = View.GONE
    }

    override fun removeFromLiked(position: Int, item: Movie) {
        movieListViewModel.addToFavourites(item)
        sharedViewModel.setMovie(item)
    }
}
