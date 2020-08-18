package edu.rosehulman.samuelma.letsgetknotty

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import edu.rosehulman.samuelma.letsgetknotty.projectlist.ProjectListFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),
    SplashFragment.OnLoginButtonPressedListener {

    val auth = FirebaseAuth.getInstance()
    lateinit var authListener: FirebaseAuth.AuthStateListener
    private val RC_SIGN_IN = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
  //      setSupportActionBar(toolbar)
        initializeListeners()
    }

    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(authListener)
    }

    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(authListener)
    }

    private fun initializeListeners() {
        // See https://firebase.google.com/docs/auth/users#the_user_lifecycle
        authListener = FirebaseAuth.AuthStateListener { auth ->
            val user = auth.currentUser
            Log.d(Constants.TAG, "In auth listener, User: $user")
            if (user != null) {
                Log.d(Constants.TAG, "UID: ${user.uid}")
                Log.d(Constants.TAG, "Name: ${user.displayName}")
                Log.d(Constants.TAG, "Email: ${user.email}")
                Log.d(Constants.TAG, "Photo: ${user.photoUrl}")
                Log.d(Constants.TAG, "Phone: ${user.phoneNumber}")
                switchToProjectListFragment(user.uid)
            } else {
                switchToSplashFragment()
            }
        }
    }

    private fun switchToSplashFragment() {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, SplashFragment())
        ft.commit()
    }

    private fun switchToProjectListFragment(uid: String) {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, ProjectListFragment.newInstance(uid))
        ft.commit()
    }

    override fun onLoginButtonPressed() {
        launchLoginUI()
    }

    private fun launchLoginUI() {
        // For details, see https://firebase.google.com/docs/auth/android/firebaseui#sign_in
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        val loginIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setLogo(R.drawable.ic_launcher_custom)
            .build()

        startActivityForResult(loginIntent, RC_SIGN_IN)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_logout -> {
                // TODO: Sign out.
                auth.signOut()
                true
            }
            R.id.action_settings -> {
                getWhichSettings()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getWhichSettings() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.dialog_which_settings_title))
        // For others, see https://developer.android.com/reference/android/provider/Settings
        builder.setItems(R.array.settings_types) { _, index ->
            var actionConstant = when (index) {
                0 -> Settings.ACTION_SOUND_SETTINGS
                1 -> Settings.ACTION_SEARCH_SETTINGS
                else -> Settings.ACTION_SETTINGS
            }
            startActivity(Intent(actionConstant))
        }
        builder.create().show()
    }


}
