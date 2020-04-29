package com.example.movie_db.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.movie_db.R
import com.example.movie_db.model.data.authentication.User

class FragmentProfile : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: ViewGroup = inflater
            .inflate(
                R.layout.profile_fragment,
                container, false
            ) as ViewGroup
        val username: TextView = rootView.findViewById(R.id.profile_username)
        username.text = User.user!!.userName

        return rootView
    }
}