package davi.xavier.aep.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import davi.xavier.aep.AepApplication
import davi.xavier.aep.R
import davi.xavier.aep.data.StatsViewModel
import davi.xavier.aep.data.UserViewModel
import davi.xavier.aep.databinding.ActivityHomeBinding
import davi.xavier.aep.home.fragments.home.LocationUpdateService
import davi.xavier.aep.home.fragments.stats.StatInfoFragment
import davi.xavier.aep.login.LoginHomeActivity
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    
    private val userViewModel: UserViewModel by viewModels { 
        UserViewModel.AuthViewModelFactory((application as AepApplication).userRepository)
    }

    private val statViewModel: StatsViewModel by viewModels {
        StatsViewModel.StatsViewModelFactory((application as AepApplication).statRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        
        val drawerLayout = binding.drawerLayout
        val toggle = ActionBarDrawerToggle(this, drawerLayout, binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        
        val navView: NavigationView = binding.navView
        val navFrag = supportFragmentManager.findFragmentById(R.id.home_nav_host) as NavHostFragment
        val navController = navFrag.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_stats
            ), drawerLayout
        )
        
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        
        val item = navView.menu.findItem(R.id.nav_logout)
        item.setOnMenuItemClickListener{
            onLogout()
            return@setOnMenuItemClickListener true
        }
        
        val headerView = navView.getHeaderView(0)
        
        val userEmailText = headerView.findViewById<TextView>(R.id.userEmailDrawerText)
        val userNameText = headerView.findViewById<TextView>(R.id.usernameDrawerText)
        userViewModel.getUserInfo().observe(this, {
            it?.let {
                userNameText.text = it.email
                userEmailText.text = it.info.userUid
            }
        })

        binding.toolbar.setNavigationOnClickListener {
            val frag = navFrag.childFragmentManager.fragments[0]
            if (frag is StatInfoFragment) {
                navController.navigateUp()
            } else {
                if (drawerLayout.isOpen) drawerLayout.close() else drawerLayout.open()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.home_nav_host)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
    
    private fun onLogout() {
        lifecycleScope.launch {
            stopService(Intent(this@HomeActivity, LocationUpdateService::class.java))
            statViewModel.finishStats()
            userViewModel.logoff()

            val intent = Intent(this@HomeActivity, LoginHomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

}
