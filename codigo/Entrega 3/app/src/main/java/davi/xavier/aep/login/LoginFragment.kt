package davi.xavier.aep.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import davi.xavier.aep.R
import davi.xavier.aep.data.AuthViewModel
import davi.xavier.aep.data.entities.Sex
import davi.xavier.aep.databinding.FragmentLoginBinding
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var navController: NavController
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel by viewModels<AuthViewModel>()
        authViewModel = viewModel

        navController = Navigation.findNavController(requireActivity(), R.id.login_nav_host)

        binding.entrarButton.setOnClickListener {
            onLogin()
        }

        binding.cadastroText.setOnClickListener { 
            onCadastro()
        }

        binding.esqueciText.setOnClickListener { 
            onEsqueciSenha()
        }
    }
    
    private fun onLogin() {
            lifecycleScope.launch {
                try {
                    authViewModel.login(binding.emailField.text.toString(), binding.senhaField.text.toString())
                    navController.navigate(LoginFragmentDirections.actionLoginFragmentToHomeActivity())
                    requireActivity().finish()
                } catch (e: Exception) {
                    showInvalidCredentialsToast()
                }
            }
    }
    
    private fun showInvalidCredentialsToast() {
        Toast.makeText(requireContext(), R.string.invalid_credentials, Toast.LENGTH_SHORT).show()
    }

    private fun onCadastro() {
        navController.navigate(LoginFragmentDirections.actionLoginFragmentToSignUpFragment())
    }

    private fun onEsqueciSenha() {
        navController.navigate(LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment())
    }
}
