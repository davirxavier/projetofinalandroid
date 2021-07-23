package davi.xavier.aep.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import davi.xavier.aep.R
import davi.xavier.aep.databinding.FragmentSignupBinding

class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignupBinding
    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSignupBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(requireActivity(), R.id.login_nav_host)

        binding.sexoField.adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.sexos,
            android.R.layout.simple_spinner_dropdown_item
        )

        binding.continuarButton.setOnClickListener {
            onContinuar()
        }

        binding.entrarText.setOnClickListener {
            onContinuar()
        }
    }
    
    private fun onContinuar() {
        Toast.makeText(requireContext(), R.string.cadastro_text, Toast.LENGTH_SHORT).show()
        navController.navigate(SignUpFragmentDirections.actionSignUpFragmentToLoginFragment())
    }
}
