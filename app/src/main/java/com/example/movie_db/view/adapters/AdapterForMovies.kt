package com.example.movie_db.view.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movie_db.view.activities.MovieInfoActivity
import com.example.movie_db.model.data.movie.Movie
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.movie_db.R

class AdapterForMovies(
    var context: Context
//    var movies: List<Movie>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var movies = mutableListOf<Movie>()
    private val VIEW_TYPE_LOADING = 0
    private val VIEW_TYPE_NORMAL = 1

    private var isLoaderVisible = false

    override fun getItemCount(): Int = movies.size

    override fun getItemViewType(position: Int): Int {
        return if (isLoaderVisible) {
            if (position == movies.size - 1) {
                VIEW_TYPE_LOADING
            } else {
                VIEW_TYPE_NORMAL
            }
        } else {
            VIEW_TYPE_NORMAL
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        val view: View = LayoutInflater.from(viewGroup.context)
//            .inflate(R.layout.skeleton, viewGroup, false)
//        return MyViewHolder(view)

        val inflater = LayoutInflater.from(viewGroup.context)
        return when (viewType) {
            VIEW_TYPE_NORMAL -> MyViewHolder(
                inflater.inflate(R.layout.skeleton, viewGroup, false)
            )
            VIEW_TYPE_LOADING -> ProgressViewHolder(
                inflater.inflate(R.layout.progress_layout, viewGroup, false)
            )
            else -> throw Throwable("invalid view")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        if (holder is MyViewHolder) {
            holder.bind(movies[i])
        }
    }

    fun addLoading() {
        isLoaderVisible = true
        movies.add(Movie(id = -1))
        notifyItemInserted(movies.size - 1)
    }

    fun removeLoading() {
        isLoaderVisible = false
        val position = movies.size - 1
        if (movies.isNotEmpty()) {
            val item = getItem(position)
            if (item != null) {
                movies.removeAt(position)
                notifyItemRemoved(position)
            }
        }
    }

    fun clear() {
        (movies as? ArrayList<Movie>)?.clear()
        movies.clear()
        notifyDataSetChanged()
    }

    private fun getItem(position: Int): Movie? {
        return movies[position]
    }

    fun replaceItems(moviesList: List<Movie>) {
        if (movies.isNullOrEmpty()) movies = moviesList as MutableList<Movie>
        else {
            if (movies!![movies!!.size - 1] != moviesList[moviesList.size - 1])
                (movies as MutableList).addAll(moviesList)
        }
        notifyDataSetChanged()
    }

    inner class MyViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(post: Movie?) {
            val title = view.findViewById<TextView>(R.id.title)
            val mainPoster = view.findViewById<ImageView>(R.id.mainPoster)

            title.text = post?.title

            Glide.with(context)
                .load(post!!.getPathToPoster())
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(mainPoster)

            view.setOnClickListener {
                val intent= Intent(view.context, MovieInfoActivity::class.java)
                intent.putExtra("movie_id", post.id)
                view.context.startActivity(intent)
            }
        }
    }

    inner class ProgressViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    }
}
