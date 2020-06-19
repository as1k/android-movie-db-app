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
import com.example.movie_db.view_model.MoviesViewModel
import androidx.fragment.app.activityViewModels
import com.example.movie_db.view.adapters.FavouritesAdapter
import com.example.movie_db.view_model.SharedViewModel
import kotlinx.android.synthetic.main.main_layout.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class FragmentSaved : Fragment(), FavouritesAdapter.RecyclerViewItemClick {

    private lateinit var recViewFav: RecyclerView
    private lateinit var swipeRefreshLayoutFav: SwipeRefreshLayout
    private val moviesViewModel by viewModel<MoviesViewModel>()
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
        return inflater.inflate(R.layout.fragments_activity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViews(view)
        swipeRefresh()
        setAdapter()
        getFavMovieCoroutine()
    }

    private fun bindViews(view: View) {
        swipeRefreshLayoutFav = view.findViewById(R.id.swipeInFragments)
        recViewFav = view.findViewById(R.id.recycler_view)
        toolbar = view.findViewById(R.id.toolbar)
        toolbar.text = getString(R.string.favorites)
    }

    private fun swipeRefresh() {
        recViewFav.layoutManager = LinearLayoutManager(requireActivity())
        swipeRefreshLayoutFav.setOnRefreshListener {
            favAdapter.clearAll()
            moviesViewModel.getSavedMovies()
        }
    }

    private fun setAdapter() {
        layoutManager = GridLayoutManager(requireActivity(), 3)
        recViewFav.layoutManager = layoutManager
        favAdapter = FavouritesAdapter(this, requireActivity())
        recViewFav.adapter = favAdapter
    }

    private fun getFavMovieCoroutine() {
        moviesViewModel.getSavedMovies()
        moviesViewModel.liveData.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is MoviesViewModel.State.ShowLoading -> {
                    swipeRefreshLayoutFav.isRefreshing = true
                }
                is MoviesViewModel.State.HideLoading -> {
                    swipeRefreshLayoutFav.isRefreshing = false
                }
                is MoviesViewModel.State.Result -> {
                    favAdapter.addItems(result.list!!)
                }
            }
        })
    }

    override fun itemClick(position: Int, item: Movie) {
//        val intent = Intent(context, MovieInfoActivity::class.java).also {
//            it.putExtra("id", item.id)
//            it.putExtra("pos", position)
//        }
//        context?.startActivity(intent)

        val bundle = Bundle()
        bundle.putInt("id", item.id)
        val movieInfoFragment =
            MovieInfoFragment()
        movieInfoFragment.arguments = bundle
        parentFragmentManager.beginTransaction().add(R.id.frameLayout, movieInfoFragment).addToBackStack(null).commit()
        requireActivity().bottom_navigation.visibility = View.GONE
    }

    override fun removeFromFavourite(position: Int, item: Movie) {
        moviesViewModel.addToFavourites(item)
        sharedViewModel.setMovie(item)
    }

//    private fun viewsOnInit() {
//        movies = ArrayList()
//        adapter = activity?.applicationContext?.let {
//            MoviesAdapter(
//                it
//            )
//        }!!
//        recView.layoutManager = GridLayoutManager(activity, 4)
//        recView.itemAnimator = DefaultItemAnimator()
//        recView.adapter = adapter
//        adapter.notifyDataSetChanged()
//
//        moviesViewModel.getSavedMovies()
//        moviesViewModel.liveData.observe(viewLifecycleOwner, Observer { result ->
//            when (result) {
//                is MoviesViewModel.State.ShowLoading -> {
//                    swipeRefreshLayout.isRefreshing = true
//                }
//                is MoviesViewModel.State.HideLoading -> {
//                    swipeRefreshLayout.isRefreshing = false
//                }
//                is MoviesViewModel.State.Result -> {
//                    adapter.movies = result.list as MutableList<Movie>
//                    adapter.notifyDataSetChanged()
//                }
//            }
//        })
//    }

}
