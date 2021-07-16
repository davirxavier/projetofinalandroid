package davi.xavier.aep.esquecisenha

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import davi.xavier.aep.R
import davi.xavier.aep.cadastro.CadastroActivity
import davi.xavier.aep.databinding.ActivityEsqueciSenhaBinding
import davi.xavier.aep.login.LoginActivity

class EsqueciSenhaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEsqueciSenhaBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEsqueciSenhaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        binding.cadastroText.setOnClickListener { 
            onCadastro()
        }
        
        binding.entrarButton.setOnClickListener { 
            onContinuar()
        }
    }

    private fun onContinuar() {
        Toast.makeText(this, R.string.esqueci_text, Toast.LENGTH_SHORT).show()
        
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun onCadastro() {
        val intent = Intent(this, CadastroActivity::class.java)
        startActivity(intent)
    }
}
