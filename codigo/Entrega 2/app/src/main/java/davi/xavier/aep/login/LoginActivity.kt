package davi.xavier.aep.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import davi.xavier.aep.cadastro.CadastroActivity
import davi.xavier.aep.databinding.ActivityLoginBinding
import davi.xavier.aep.esquecisenha.EsqueciSenhaActivity
import davi.xavier.aep.home.HomeActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
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
        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
    
    private fun onCadastro() {
        val intent = Intent(this, CadastroActivity::class.java)
        startActivity(intent)
    }

    private fun onEsqueciSenha() {
        val intent = Intent(this, EsqueciSenhaActivity::class.java)
        startActivity(intent)
    }
}
