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
import com.example.movie_db.model.data.authentication.User
import com.example.movie_db.model.data.authentication.UserResponse
import com.example.movie_db.view.activities.SignInActivity
import com.example.movie_db.view_model.ProfileViewModel
import kotlinx.android.synthetic.main.profile_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentProfile : Fragment() {

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
        val rootView: ViewGroup = inflater
            .inflate(
                R.layout.profile_fragment,
                container, false
            ) as ViewGroup
        username = rootView.findViewById(R.id.profile_username)
        name = rootView.findViewById(R.id.profile_name)
        profilePhoto = rootView.findViewById(R.id.profilePhoto)
        btnLogout = rootView.findViewById(R.id.btnLogout)
        progressBar = rootView.findViewById(R.id.progressBarProfile)

        username.text = User.user!!.userName
        name.text = User.user!!.userId.toString()

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
                        val savedUser: SharedPreferences = rootView.context.getSharedPreferences(
                            "current_user",
                            Context.MODE_PRIVATE
                        )
                        savedUser.edit().remove("current_user").apply()
                        val intent = Intent(rootView.context, SignInActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                }
            }
        })

        btnLogout.setOnClickListener {
            profileViewModel.logout(rootView)
        }

        setData(User.user!!)

        return rootView
    }

    private fun setData(user: UserResponse) {
        Glide.with(requireActivity())
            .load("https://secure.gravatar.com/avatar/${user.avatar.gravatar.hash}")
            .into(profilePhoto)
    }
}
