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
import com.example.movie_db.view.adapters.MoviesAdapter
import com.example.movie_db.R
import com.example.movie_db.model.data.movie.Movie
import androidx.lifecycle.Observer
import com.example.movie_db.utils.PaginationListener
import com.example.movie_db.model.database.MovieDao
import com.example.movie_db.model.database.MovieDatabase
import com.example.movie_db.model.network.Retrofit
import com.example.movie_db.model.repository.MovieRepositoryImpl
import com.example.movie_db.view_model.MoviesViewModel
import androidx.fragment.app.activityViewModels
import com.example.movie_db.model.repository.MovieRepository
import com.example.movie_db.view_model.SharedViewModel
import kotlinx.android.synthetic.main.main_layout.*

class FragmentOne : Fragment(), MoviesAdapter.RecyclerViewItemClick {

    private lateinit var adapter: MoviesAdapter
    private lateinit var recView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var toolbar: TextView
    private lateinit var moviesViewModel: MoviesViewModel
    private lateinit var layoutManager: GridLayoutManager

    private var currentPage = PaginationListener.PAGE_START
    private var isLastPage = false
    private var isLoading = false
    private var itemCount = 0
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sharedViewModel.savedMovies.observe(requireActivity(), Observer { item ->
            adapter.updateItem(item)
        })
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
        setViewModels()
        bindView(view)
        refresh()
        setAdapter()
        getMovies(currentPage)
    }

    override fun onResume() {
        super.onResume()
        swipeRefreshLayout.isRefreshing = true
        requireActivity().bottom_navigation.visibility = View.VISIBLE
        swipeRefreshLayout.isRefreshing = false
    }

    private fun bindView(view: View) {
        toolbar = view.findViewById(R.id.toolbar)
        toolbar.text = getString(R.string.movies)
        recView = view.findViewById(R.id.recycler_view)
        swipeRefreshLayout = view.findViewById(R.id.swipeInFragments)

        val movieDao: MovieDao = MovieDatabase.getDatabase(requireContext()).movieDao()
        val movieRepository = MovieRepositoryImpl(Retrofit, movieDao)
        moviesViewModel = MoviesViewModel(movieRepository)
    }

    private fun setViewModels() {
        val movieDao: MovieDao = MovieDatabase.getDatabase(requireContext()).movieDao()
        val movieRepository: MovieRepository = MovieRepositoryImpl(Retrofit, movieDao)
        moviesViewModel = MoviesViewModel(movieRepository)
    }

    private fun setAdapter() {
        layoutManager = GridLayoutManager(requireActivity(), 3)
        recView.layoutManager = layoutManager
        adapter = MoviesAdapter(this, requireActivity())
        recView.adapter = adapter

        recView.addOnScrollListener(object : PaginationListener(layoutManager) {
            override fun loadMoreItems() {
                isLoading = true
                currentPage++
                getMovies(currentPage)
            }

            override fun isLastPage(): Boolean = isLastPage

            override fun isLoading(): Boolean = isLoading
        })
    }

    private fun refresh() {
        recView.layoutManager = LinearLayoutManager(requireActivity())
        swipeRefreshLayout.setOnRefreshListener {
            adapter.clearAll()
            itemCount = 0
            currentPage= PaginationListener.PAGE_START
            isLastPage = false
            moviesViewModel.getMovies(currentPage)
        }
    }

    private fun getMovies(page: Int) {
        moviesViewModel.getMovies(page)
        moviesViewModel.liveData.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is MoviesViewModel.State.ShowLoading -> {
                    swipeRefreshLayout.isRefreshing = true
                }
                is MoviesViewModel.State.HideLoading -> {
                    swipeRefreshLayout.isRefreshing = false
                }
                is MoviesViewModel.State.Result -> {
                    adapter.removeFooterLoading()
                    adapter.addItems(result.list!!)
                    adapter.addFooterLoading()
                    isLoading = false
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

    override fun addToFavourites(position: Int, item: Movie) {
        moviesViewModel.addToFavourites(item)
        sharedViewModel.setMovie(item)
    }


//    private fun viewsOnInit() {
//        movies = ArrayList()
//        this.adapter = activity?.applicationContext?.let {
//            MoviesAdapter(
//                it
//            )
//        }!!
//        layoutManager = GridLayoutManager(activity, 4)
//        recView.layoutManager = layoutManager
//        recView.itemAnimator = DefaultItemAnimator()
//        recView.adapter = this.adapter
//
//        recView.addOnScrollListener(object : PaginationListener(layoutManager) {
//            override fun loadMoreItems() {
//                isLoading = true
//                currentPage++
//                moviesViewModel.getMovies(currentPage)
//            }
//
//            override fun isLastPage(): Boolean = isLastPage
//            override fun isLoading(): Boolean = isLoading
//        })

//        recView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)) {
//                    page++
//                    moviesViewModel.getMovies(page)
//                }
//            }
//        })
//        this.adapter.notifyDataSetChanged()
//    }

}
