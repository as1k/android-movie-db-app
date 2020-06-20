package com.example.movie_db.view.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.movie_db.R
import com.example.movie_db.model.data.authentication.CurrentUser
import com.example.movie_db.view.activities.SignInActivity
import com.example.movie_db.view_model.ProfileViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : Fragment() {

    private val profileViewModel: ProfileViewModel by viewModel<ProfileViewModel>()
    private lateinit var progressBar: ProgressBar
    private lateinit var username: TextView
    private lateinit var name: TextView
    private lateinit var btnLogout: Button
    private lateinit var profilePhoto: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.profile_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews(view)
        setData()
        getUser(view)
    }

    private fun bindViews(view: View) {
        username = view.findViewById(R.id.profile_username)
        name = view.findViewById(R.id.profile_name)
        profilePhoto = view.findViewById(R.id.profilePhoto)
        progressBar = view.findViewById(R.id.progressBarProfile)
        btnLogout = view.findViewById(R.id.btnLogout)
        btnLogout.setOnClickListener {
            profileViewModel.logout(view)
        }
    }

    private fun setData() {
        username.text = CurrentUser.user!!.username
        name.text = CurrentUser.user!!.userId.toString()
        Glide.with(requireActivity())
            .load("https://secure.gravatar.com/avatar/${CurrentUser.user!!.avatar.gravatar.hash}")
            .into(profilePhoto)
    }

    private fun getUser(view: View) {
        profileViewModel.liveData.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is ProfileViewModel.State.ShowLoading -> {
                    progressBar.visibility = View.VISIBLE
                }
                is ProfileViewModel.State.HideLoading -> {
                    progressBar.visibility = View.GONE
                }
                is ProfileViewModel.State.Result -> {
                    if (result.isSuccess) {
                        val savedUser: SharedPreferences = view.context.getSharedPreferences(
                            "current_user",
                            Context.MODE_PRIVATE
                        )
                        savedUser.edit().remove("current_user").apply()
                        val intent = Intent(view.context, SignInActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                }
            }
        })
    }
}
