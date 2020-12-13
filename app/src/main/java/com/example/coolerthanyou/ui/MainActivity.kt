package com.example.coolerthanyou.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.coolerthanyou.BaseActivity
import com.example.coolerthanyou.BaseApplication
import com.example.coolerthanyou.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar

/**
 * Main Activity for all core functions.
 * Each "function" should reside as a fragment within this activity, swapping the [Fragment] and [ViewModel] as necessary.
 */
class MainActivity : BaseActivity() {

    private val viewModel: MainViewModel by viewModels { viewModelFactory }
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        // Inject dependencies before init
        (applicationContext as BaseApplication).appComponent.mainComponent().create().inject(this)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val boxValue : TextView = findViewById(R.id.quick_access_drawer_box_value)

        val boxValueList = arrayOf(getText(R.string.quick_access_drawer_default_box_value), getText(R.string.quick_access_drawer_secondary_box_value),getText(R.string.quick_access_drawer_tertiary_box_value))
        val boxSelectionTool = BoxSelector(boxValueList, viewModel)

        val quickAccessDrawer: View = findViewById(R.id.quick_access_drawer)
        quickAccessDrawer.setOnClickListener{ _ ->
            val boxSelectionDialog : AlertDialog.Builder = boxSelectionTool.getAlertDialog(this, boxValue)
            boxSelectionDialog.show()
        }

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.drawer_home,
                R.id.drawer_data,
                R.id.drawer_about,
                R.id.drawer_control
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
