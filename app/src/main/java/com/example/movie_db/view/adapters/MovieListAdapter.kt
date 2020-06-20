package com.example.movie_db.view.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movie_db.R
import com.example.movie_db.model.data.movie.Movie

class MovieListAdapter(
    private val itemClickListner: RecyclerViewItemClick? = null,
    val context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_LOADING = 0
    private val VIEW_TYPE_NORMAL = 1
    private var isLoaderVisible = false
    private var moviePosition = 1

    private var movieList = listOf<Movie>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_NORMAL -> MovieListViewHolder(
                inflater.inflate(R.layout.movie_list_item, parent, false)
            )
            VIEW_TYPE_LOADING -> LoaderViewHolder(
                inflater.inflate(R.layout.progress_layout, parent, false)
            )
            else -> throw Throwable("Invalid View!")
        }
    }

    override fun getItemCount(): Int = movieList.size ?: 0

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MovieListViewHolder) {
            return holder.bind(movieList[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoaderVisible) {
            if (position == movieList.size - 1) {
                VIEW_TYPE_LOADING
            } else {
                VIEW_TYPE_NORMAL
            }
        } else {
            VIEW_TYPE_NORMAL
        }
    }

    fun addLoadingProgressBar() {
        isLoaderVisible = true
        (movieList as? ArrayList<Movie>)?.add(Movie(id = -1))
        notifyItemInserted(movieList.size.minus(1))
    }

    fun removeLoadingProgressBar() {
        isLoaderVisible = false
        val position = movieList.size.minus(1)
        if (movieList.isNotEmpty()) {
            val item = getItem(position)
            if (item != null) {
                (movieList as? ArrayList<Movie>)?.removeAt(position)
                notifyItemRemoved(position)
            }
        }
    }

    private fun getItem(position: Int): Movie? {
        return movieList[position]
    }

    fun addItems(moviesList: List<Movie>) {
        if (movieList.isEmpty()) movieList = moviesList
        else {
            if (movieList[movieList.size - 1] != moviesList[moviesList.size - 1])
                (movieList as? ArrayList<Movie>)?.addAll(moviesList)
        }
        notifyDataSetChanged()
    }

    fun updateItem(movie: Movie) {
        val foundMovie = movieList.find { it.id == movie.id }
        foundMovie?.liked = movie.liked
        notifyDataSetChanged()
    }

    fun clearAll() {
        (movieList as? ArrayList<Movie>)?.clear()
        moviePosition = 1
        notifyDataSetChanged()
    }

    inner class MovieListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mainPoster: ImageView = itemView.findViewById(R.id.mainPoster)
        private val title: TextView = itemView.findViewById(R.id.title)
        private val movieId: TextView = itemView.findViewById(R.id.movieId)
        private val btnSave: ImageView = itemView.findViewById(R.id.iv_save)
        private var id: Int = 0

        fun bind(movie: Movie) {

            if (movie.position == 0) {
                movie.position = moviePosition
                moviePosition++
            }

            Glide.with(itemView.context)
                .load("https://image.tmdb.org/t/p/w342${movie.posterPath}")
                .into(mainPoster)

            id = movie.id
            movieId.text = movie.position.toString()
            title.text = movie.title

            if (movie.liked) {
                btnSave.setImageResource(R.drawable.ic_bookmark_clicked)
            } else {
                btnSave.setImageResource(R.drawable.ic_bookmark_not_clicked)
            }

            itemView.setOnClickListener {
                itemClickListner?.itemClick(adapterPosition, movie)
            }

            btnSave.setOnClickListener {
                itemClickListner?.like(adapterPosition, movie)
                val drawable: Drawable = btnSave.drawable
                if (drawable.constantState?.equals(
                        getDrawable(
                            itemView.context,
                            R.drawable.ic_bookmark_not_clicked
                        )?.constantState
                    ) == true
                ) {
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
        fun like(position: Int, item: Movie)
    }
}
