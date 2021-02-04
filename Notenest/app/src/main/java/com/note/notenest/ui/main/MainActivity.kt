package com.note.notenest.ui.main


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.note.notenest.R
import com.note.notenest.databinding.ActivityMainBinding
import com.note.notenest.utils.rate
import com.note.notenest.utils.share
import com.note.notenest.viewModels.NoteViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    val noteViewModel: NoteViewModel by lazy {
        ViewModelProvider(this)[NoteViewModel::class.java]
    }

    val navController: NavController by lazy {
        findNavController(R.id.fragment)
    }

    lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)

        // setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setSupportActionBar(binding.toolbar)

        setupDrawerLayout()

    }

    private fun setupDrawerLayout() {
        binding.navView.setupWithNavController(navController)
        NavigationUI.setupActionBarWithNavController(this, navController, binding.drawerLayout)

        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.share -> {
                    share()
                    true
                }
                R.id.rate -> {
rate()
                    true
                }

                else -> {
                    false
                }
            }

            binding.drawerLayout.closeDrawer(GravityCompat.START);
            NavigationUI.onNavDestinationSelected(it, navController);


        }


    }


    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, binding.drawerLayout)
    }


    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


}