package davi.xavier.aep.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import davi.xavier.aep.AepApplication
import davi.xavier.aep.R
import davi.xavier.aep.data.UserViewModel
import davi.xavier.aep.databinding.FragmentForgotPasswordBinding
import kotlinx.coroutines.launch

class ForgotPasswordFragment : Fragment() {
    private lateinit var binding: FragmentForgotPasswordBinding
    private lateinit var navController: NavController
    private var sending = false

    private val userViewModel: UserViewModel by activityViewModels {
        UserViewModel.AuthViewModelFactory(
            (activity?.application as AepApplication).userRepository
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentForgotPasswordBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(requireActivity(), R.id.home_nav_host)

        binding.cadastroText.setOnClickListener { 
            onCadastro()
        }

        binding.entrarButton.setOnClickListener { 
            onContinuar()
        }
    }
    
    private fun onContinuar() {
        if (!sending) {
            sending = true
            lifecycleScope.launch {
                try {
                    userViewModel.forgotPassword(binding.emailField.text.toString())
                    Toast.makeText(requireContext(), R.string.esqueci_text, Toast.LENGTH_LONG).show()
                    navController.navigate(ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToLoginFragment())
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), R.string.unknown_error, Toast.LENGTH_LONG).show()
                }
                sending = false
            }
        }
    }

    private fun onCadastro() {
        navController.navigate(ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToSignUpFragment2())
    }
}
