package com.example.movie_db.view.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movie_db.R
import com.example.movie_db.model.data.movie.Movie

class MovieListAdapter(
    private val itemClickListener: RecyclerViewItemClick? = null,
    val context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_LOADING = 0
    private val VIEW_TYPE_NORMAL = 1
    private var isLoaderVisible = false
    private var moviePosition = 1

    private var movies = listOf<Movie>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_NORMAL -> MoviesViewHolder(
                inflater.inflate(R.layout.movie_list_item, parent, false)
            )
            VIEW_TYPE_LOADING -> LoaderViewHolder(
                inflater.inflate(R.layout.progress_layout, parent, false)
            )
            else -> throw Throwable("Invalid View!")
        }
    }

    override fun getItemCount(): Int = movies.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MoviesViewHolder) {
            return holder.bind(movies[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        Log.d("my_debug", "movie list adapter getItemViewType:")

        return if (isLoaderVisible) {
            if(position == movies.size - 1) {
                Log.d("my_debug", "movie list adapter getItemViewType: VIEW_TYPE_LOADING")
                VIEW_TYPE_LOADING
            } else {
                Log.d("my_debug", "movie list adapter getItemViewType: VIEW_TYPE_NORMAL")
                VIEW_TYPE_NORMAL
            }
        } else {
            Log.d("my_debug", "movie list adapter getItemViewType: else VIEW_TYPE_NORMAL")
            VIEW_TYPE_NORMAL
        }
    }

    fun addFooterLoading() {
        isLoaderVisible = true
        (movies as? ArrayList<Movie>)?.add(Movie(id = -1))
        notifyItemInserted(movies.size.minus(1))
    }

    fun removeFooterLoading() {
        isLoaderVisible = false
        val position = movies.size.minus(1)
        if (movies.isNotEmpty()) {
            val item = getItem(position)
            if (item != null) {
                (movies as? ArrayList<Movie>)?.removeAt(position)
                notifyItemRemoved(position)
            }
        }

    }

    private fun getItem(position: Int): Movie? {
        return movies[position]
    }

    fun addItems(moviesList: List<Movie>) {
        if (movies.isEmpty()) movies = moviesList
        else {
            if (movies[movies.size - 1] != moviesList[moviesList.size - 1])
                (movies as? ArrayList<Movie>)?.addAll(moviesList)
        }
        notifyDataSetChanged()
    }

    fun updateItem(movie: Movie) {
        val foundMovie = movies.find { it.id == movie.id }
        foundMovie?.liked = movie.liked
        notifyDataSetChanged()
    }

    fun clearAll() {
        (movies as? ArrayList<Movie>)?.clear()
        moviePosition = 1
        notifyDataSetChanged()
    }

    inner class MoviesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(movie: Movie) {
            val mainPoster: ImageView = itemView.findViewById(R.id.mainPoster)
            val title: TextView = itemView.findViewById(R.id.title)
            val movieId: TextView = itemView.findViewById(R.id.movieId)
            val btnSave: ImageView = itemView.findViewById(R.id.iv_save)

            if (movie.position == 0) {
                movie.position = moviePosition
                moviePosition++
            }

            Glide.with(itemView.context)
                .load("https://image.tmdb.org/t/p/w342${movie.posterPath}")
                .into(mainPoster)

            movieId.text = movie.position.toString()
            title.text = movie.title

            if (movie.liked) {
                btnSave.setImageResource(R.drawable.ic_bookmark_clicked)
            } else {
                btnSave.setImageResource(R.drawable.ic_bookmark_not_clicked)
            }

            itemView.setOnClickListener {
                itemClickListener?.itemClick(adapterPosition, movie)
            }

            btnSave.setOnClickListener {
                itemClickListener?.addToFavourites(adapterPosition, movie)
                if (movie.liked) {
                    btnSave.setImageResource(R.drawable.ic_bookmark_clicked)
                } else {
                    btnSave.setImageResource(R.drawable.ic_bookmark_not_clicked)
                }
            }
        }
    }

    inner class LoaderViewHolder(view: View) : RecyclerView.ViewHolder(view)

    interface RecyclerViewItemClick {
        fun itemClick(position: Int, item: Movie)
        fun addToFavourites(position: Int, item: Movie)
    }
}