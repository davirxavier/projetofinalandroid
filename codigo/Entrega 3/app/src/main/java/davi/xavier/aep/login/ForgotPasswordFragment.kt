package davi.xavier.aep.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import davi.xavier.aep.R
import davi.xavier.aep.databinding.FragmentForgotPasswordBinding

class ForgotPasswordFragment : Fragment() {
    private lateinit var binding: FragmentForgotPasswordBinding
    private lateinit var navController: NavController

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
        Toast.makeText(requireContext(), R.string.esqueci_text, Toast.LENGTH_SHORT).show()
        navController.navigate(ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToLoginFragment())
    }

    private fun onCadastro() {
        navController.navigate(ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToSignUpFragment2())
    }
}
