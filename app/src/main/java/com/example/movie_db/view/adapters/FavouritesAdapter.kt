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
    private val itemClickListener: RecyclerViewItemClick? = null,
    val context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var movieList = listOf<Movie>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouritesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.movie_list_item, parent, false)
        return FavouritesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return movieList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is FavouritesViewHolder) {
            return holder.bind(movieList[position])
        }
    }

    fun addItems(moviesList: List<Movie>) {
        movieList = moviesList
        notifyDataSetChanged()
    }

    fun addItem(movie: Movie) {
        if (!movieList.contains(movie)) {
            (movieList as? ArrayList<Movie>)?.add(movie)
            notifyItemInserted(movieList.size - 1)
        }
    }

    fun removeItem(movie: Movie) {
        val id = movie.id
        val foundMovie = movieList.find { it.id == id }
        if (foundMovie != null) {
            (movieList as? ArrayList<Movie>)?.remove(foundMovie)
        }
        notifyDataSetChanged()
    }

    fun clearAll() {
        (movieList as? ArrayList<Movie>)?.clear()
        notifyDataSetChanged()
    }

    inner class FavouritesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mainPoster: ImageView = itemView.findViewById(R.id.mainPoster)
        private val title: TextView = itemView.findViewById(R.id.title)
        private val movieId: TextView = itemView.findViewById(R.id.movieId)
        private val btnSave: ImageView = itemView.findViewById(R.id.iv_save)
        private var id: Int = 0

        fun bind(movie: Movie) {
            Glide.with(itemView.context)
                .load("https://image.tmdb.org/t/p/w342${movie.posterPath}")
                .into(mainPoster)

            id = movie.id
            movieId.text = (adapterPosition + 1).toString()
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
                itemClickListener?.removeFromLiked(adapterPosition, movie)
                btnSave.setImageResource(R.drawable.ic_bookmark_not_clicked)
            }
        }
    }

    interface RecyclerViewItemClick {
        fun itemClick(position: Int, item: Movie)
        fun removeFromLiked(position: Int, item: Movie)
    }
}
