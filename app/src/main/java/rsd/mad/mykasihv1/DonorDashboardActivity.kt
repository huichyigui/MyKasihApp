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
import rsd.mad.mykasihv1.databinding.ActivityDonorDashboardBinding

class DonorDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDonorDashboardBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDonorDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        navController = findNavController(R.id.nav_host_fragment_activity_donor_dashboard)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_donor_home, R.id.nav_donor_community, R.id.nav_donor_account
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.nav_donor_home -> {
                navController.navigate(R.id.nav_donor_home)
            }
            R.id.nav_donor_community -> {
                navController.navigate(R.id.nav_donor_community)
            }
            R.id.nav_donor_account -> {
                navController.navigate(R.id.nav_donor_account)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}

class ExitDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setMessage(getString(R.string.exit_message))
            .setPositiveButton(getString(R.string.exit)) { dialog, id ->
                requireActivity().finish() // Terminate the app
            }
            .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                // Do nothing
            }
        return builder.create()
    }
}
