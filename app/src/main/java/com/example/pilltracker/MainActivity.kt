package com.example.pilltracker
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity(), LoginFragment.LoginSuccessListener {

    private var loggedInUsername: String? = null
    private var loggedInPassword: String? = null
    private var isOptionsMenuEnabled = false // Add this flag

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //PillTrackerAPI().run()

        //val logInPage = LoginFragment.newInstance(this)
        //replaceFragment(logInPage)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        val pharmacyPage: Fragment = PharmacyFragment()

        bottomNavigationView.setOnItemSelectedListener { item ->
            // Check if Options Menu is enabled
            if (!isOptionsMenuEnabled) return@setOnItemSelectedListener false

            val fragment: Fragment
            when (item.itemId) {
                R.id.nav_myPills -> {
                    if (loggedInUsername != null) {
                        fragment = MyPillsFragment.newInstance(loggedInUsername!!)
                    } else {
                        return@setOnItemSelectedListener false
                    }
                }
                R.id.nav_log -> {
                    if (loggedInUsername != null) {
                        fragment = LogFragment.newInstance(loggedInUsername!!)
                    } else {
                        return@setOnItemSelectedListener false
                    }
                }
                R.id.nav_pharmacy -> {
                    fragment = PharmacyFragment()
                }
                else -> {
                    return@setOnItemSelectedListener false
                }
            }
            replaceFragment(fragment)
            true
        }


        disableNavigationButtons()
        disableOptionsMenu() // Add this line
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (isOptionsMenuEnabled) { // Check if Options Menu is enabled
            menuInflater.inflate(R.menu.options_menu, menu)
            return true
        }
        return false // Return false if Options Menu is disabled
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_settings -> {
                val profileFragment = ProfileFragment.newInstance(loggedInUsername ?: "defaultUsername")
                replaceFragment(profileFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onLoginSuccess(username: String, password: String) {
        loggedInUsername = username
        loggedInPassword = password

        enableNavigationButtons()
        enableOptionsMenu() // Add this line

        val logPage= LogFragment.newInstance(loggedInUsername!!)
        val myPillsPage = MyPillsFragment.newInstance(loggedInUsername!!)
        replaceFragment(myPillsPage)
    }

    private fun replaceFragment(pillTrackerFragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.pill_tracker_frame_layout, pillTrackerFragment)
        fragmentTransaction.commit()
    }

    private fun disableNavigationButtons() {
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.menu.findItem(R.id.nav_myPills).isEnabled = false
        bottomNavigationView.menu.findItem(R.id.nav_log).isEnabled = false
        bottomNavigationView.menu.findItem(R.id.nav_pharmacy).isEnabled = false
    }

    private fun enableNavigationButtons() {
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.menu.findItem(R.id.nav_myPills).isEnabled = true
        bottomNavigationView.menu.findItem(R.id.nav_log).isEnabled = true
        bottomNavigationView.menu.findItem(R.id.nav_pharmacy).isEnabled = true
        bottomNavigationView.selectedItemId = R.id.nav_myPills
    }

    private fun disableOptionsMenu() {
        isOptionsMenuEnabled = false
        invalidateOptionsMenu()
    }

    private fun enableOptionsMenu() {
        isOptionsMenuEnabled = true
        invalidateOptionsMenu()
    }
}
