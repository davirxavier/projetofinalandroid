package davi.xavier.aep.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import davi.xavier.aep.R
import davi.xavier.aep.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
        navController.navigate(LoginFragmentDirections.actionLoginFragmentToHomeActivity())
    }

    private fun onCadastro() {
        navController.navigate(LoginFragmentDirections.actionLoginFragmentToSignUpFragment())
    }

    private fun onEsqueciSenha() {
        navController.navigate(LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment())
    }
}
