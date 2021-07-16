package davi.xavier.aep.cadastro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import davi.xavier.aep.R
import davi.xavier.aep.databinding.ActivityCadastroBinding
import davi.xavier.aep.home.HomeActivity
import davi.xavier.aep.login.LoginActivity

class CadastroActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCadastroBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        binding.sexoField.adapter = ArrayAdapter.createFromResource(
            this,
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
        Toast.makeText(this, R.string.cadastro_text, Toast.LENGTH_SHORT).show()
        
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}
