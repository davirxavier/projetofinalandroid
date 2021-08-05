package davi.xavier.aep.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import davi.xavier.aep.AepApplication
import davi.xavier.aep.R
import davi.xavier.aep.data.UserViewModel
import davi.xavier.aep.data.entities.Sex
import davi.xavier.aep.databinding.FragmentSignupBinding
import kotlinx.coroutines.launch


class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignupBinding
    private lateinit var navController: NavController
    private var sending = false
    
    private val userViewModel: UserViewModel by activityViewModels {
        UserViewModel.AuthViewModelFactory(
            (activity?.application as AepApplication).userRepository
        ) 
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSignupBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(requireActivity(), R.id.home_nav_host)

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
        if (!sending) {
            sending = true
            val password = binding.senhaField.text.toString()
            if (password != binding.confirmarSenhaField.text.toString()) {
                Toast.makeText(requireContext(), R.string.passwords_not_equal, Toast.LENGTH_LONG).show()
                return
            }

            if (binding.alturaField.text.isBlank() || binding.pesoField.text.isBlank()) {
                Toast.makeText(requireContext(), R.string.empty_fields, Toast.LENGTH_LONG).show()
                return
            }

            val height = binding.alturaField.text.toString().toInt()
            val weight = binding.pesoField.text.toString().toDouble()

            lifecycleScope.launch {
                var toastMessageId: Int? = null

                try {
                    userViewModel.signUp(
                        email = binding.emailField.text.toString(),
                        password, height, weight,
                        sex = if (binding.sexoField.selectedItemPosition == 0) Sex.FEMALE else Sex.MALE
                    )

                    Toast.makeText(requireContext(), R.string.cadastro_text, Toast.LENGTH_SHORT).show()
                    navController.navigate(SignUpFragmentDirections.actionSignUpFragmentToLoginFragment())
                } catch (e: FirebaseAuthWeakPasswordException) {
                    toastMessageId = R.string.weak_pass
                } catch (e: FirebaseAuthInvalidCredentialsException) {
                    toastMessageId = R.string.invalid_email
                } catch (e: FirebaseAuthUserCollisionException) {
                    toastMessageId = R.string.user_exists
                } catch (e: Exception) {
                    Log.e("SignUpError", e.message!!)
                    toastMessageId = R.string.unknown_error
                }

                toastMessageId?.let { messageId ->
                    Toast.makeText(requireContext(), messageId, Toast.LENGTH_LONG).show()
                }
                sending = false
            }
        }
    }
}
