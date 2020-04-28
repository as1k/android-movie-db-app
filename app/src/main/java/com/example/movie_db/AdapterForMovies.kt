package com.example.movie_db

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

class AdapterForMovies(var context: Context, var movies: List<Movie>) :
    RecyclerView.Adapter<AdapterForMovies.MyViewHolder>() {

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

    override fun getItemCount(): Int = movies.size

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val view: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.skeleton, viewGroup, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder, i: Int) {
        viewHolder.bind(movies[i])
    }
}
