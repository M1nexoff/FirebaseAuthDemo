package uz.m1nex.testfirebaseauth.screen

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import uz.m1nex.testfirebaseauth.AppRepository
import uz.m1nex.testfirebaseauth.R
import uz.m1nex.testfirebaseauth.databinding.ScreenProfileBinding

class ProfileScreen : Fragment(R.layout.screen_profile) {
    private val binding by viewBinding(ScreenProfileBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = AppRepository.getCurrentUser()

        binding.tvName.text = user?.displayName ?: "No Name"
        binding.tvEmail.text = user?.email ?: "No Email"

        // Load avatar if exists
        if (user?.photoUrl != null) {
            Glide.with(this)
                .load(user.photoUrl)
                .into(binding.imgAvatar)
        }

        // Logout
        binding.btnLogout.setOnClickListener {
            AppRepository.logout {
                toast("Logged out")
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
        // Delete Account
        binding.btnDelete.setOnClickListener {
            AppRepository.deleteAccount(
                onSuccess = {
                    toast("Account deleted")
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                },
                onFailure = {
                    toast("Error: $it")
                }
            )
        }
    }

    private fun toast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        Log.d("TTT", "error $msg")
    }
}
