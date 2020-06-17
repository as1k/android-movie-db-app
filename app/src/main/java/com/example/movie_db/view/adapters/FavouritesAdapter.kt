package com.example.movie_db.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movie_db.R
import com.example.movie_db.model.data.movie.Movie

class FavouritesAdapter(
    private val itemClickListner: RecyclerViewItemClick? = null,
    val context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var movies = listOf<Movie>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouritesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.skeleton, parent, false)
        return FavouritesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return movies.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is FavouritesViewHolder) {
            return holder.bind(movies[position])
        }
    }

    fun addItems(moviesList: List<Movie>) {
        movies = moviesList
        notifyDataSetChanged()
    }

    fun addItem(movie: Movie) {
        if (!movies.contains(movie)) {
            (movies as? ArrayList<Movie>)?.add(movie)
            notifyItemInserted(movies.size - 1)
        }
    }

    fun removeItem(movie: Movie) {
        val id = movie.id
        val foundMovie = movies.find { it.id == id }
        if (foundMovie != null) {
            (movies as? ArrayList<Movie>)?.remove(foundMovie)
        }
        notifyDataSetChanged()
    }

    fun clearAll() {
        (movies as? ArrayList<Movie>)?.clear()
        notifyDataSetChanged()
    }

    inner class FavouritesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mainPoster: ImageView = itemView.findViewById(R.id.mainPoster)
        private val title: TextView = itemView.findViewById(R.id.title)
        private val movieId: TextView = itemView.findViewById(R.id.movieId)
        private val btnSave: ImageView = itemView.findViewById(R.id.ivSave)
        private var id: Int = 0

        fun bind(movie: Movie) {
            Glide.with(itemView.context)
                .load("https://image.tmdb.org/t/p/w342${movie.pathToPoster}")
                .into(mainPoster)

            id = movie.id
            movieId.text = (adapterPosition + 1).toString()
            title.text = movie.title

            if (movie.liked) {
                btnSave.setImageResource(R.drawable.ic_bookmark)
            } else {
                btnSave.setImageResource(R.drawable.ic_bookmark_filled)
            }

            itemView.setOnClickListener {
                itemClickListner?.itemClick(adapterPosition, movie)
            }

            btnSave.setOnClickListener {
                itemClickListner?.removeFromFavourite(adapterPosition, movie)
                btnSave.setImageResource(R.drawable.ic_bookmark_filled)
            }
        }
    }

    interface RecyclerViewItemClick {
        fun itemClick(position: Int, item: Movie)
        fun removeFromFavourite(position: Int, item: Movie)
    }
}
