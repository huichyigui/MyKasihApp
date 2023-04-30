package rsd.mad.mykasihv1

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import rsd.mad.mykasihv1.databinding.ActivityDoneeDashboardBinding

class DoneeDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDoneeDashboardBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDoneeDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_donee_dashboard)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_donee_home, R.id.nav_donee_community, R.id.nav_donee_account
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val backPressCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val exitDialogFragment = ExitDialogFragment()
                exitDialogFragment.show(supportFragmentManager, "ExitDialog")
            }
        }
        onBackPressedDispatcher.addCallback(backPressCallback)
    }
    override fun onSupportNavigateUp(): Boolean {
        navController = findNavController(R.id.nav_host_fragment_activity_donee_dashboard)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.nav_donee_home -> {
                navController.navigate(R.id.nav_donee_home)
                true
            }
            R.id.nav_donee_community -> {
                navController.navigate(R.id.nav_donee_community)
                true
            }
            R.id.nav_donee_account -> {
                navController.navigate(R.id.nav_donee_account)
                true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}