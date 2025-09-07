package uz.m1nex.testfirebaseauth.screen

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.m1nex.testfirebaseauth.AppRepository
import uz.m1nex.testfirebaseauth.R
import uz.m1nex.testfirebaseauth.databinding.ScreenAuthBinding

class AuthScreen : Fragment(R.layout.screen_auth) {
    private val binding by viewBinding(ScreenAuthBinding::bind)
    override fun onStart() {
        super.onStart()
        if (AppRepository.isLogined) {
            findNavController().navigate(R.id.action_authScreen_to_profileScreen)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Login with Email & Password
        binding.btnLoginEmail.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            AppRepository.loginWithEmailAndPassword(
                email,
                password,
                onSuccess = {
                    findNavController().navigate(R.id.action_authScreen_to_profileScreen)
                    toast("Logged in with Email")
                },
                onFailure = {
                    toast("Error: $it")
                }
            )
        }

        // Login with Google
        binding.btnLoginGoogle.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                AppRepository.loginWithGoogle(
                    requireContext(),
                    onSuccess = {
                        findNavController().navigate(R.id.action_authScreen_to_profileScreen)
                        toast("Logged in with Google")
                    },
                    onFailure = {
                        toast("Error: $it")
                    }
                )
            }
        }

        // Login with GitHub
        binding.btnLoginGithub.setOnClickListener {
            AppRepository.loginWithGithub(
                requireActivity(),
                onSuccess = {
                    findNavController().navigate(R.id.action_authScreen_to_profileScreen)
                    toast("Logged in with GitHub")
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
