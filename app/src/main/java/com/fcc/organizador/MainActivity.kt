package com.fcc.organizador

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.fcc.organizador.databinding.ActivityMainBinding
import com.fcc.organizador.schedule.ScheduleFragment
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val fragmentList = arrayListOf(TeachersFragment(), ScheduleFragment())

        binding.apply {
            viewPager.adapter = ViewPagerAdapter(fragmentList, this@MainActivity.supportFragmentManager, lifecycle)
            viewPager.isUserInputEnabled = false// Do not permit to navigate viewPager with the swipe action, this is because
            //the TeachersFragment and ScheduleFragment have components with swipe interactions
            TabLayoutMediator(tabView, viewPager){ tab, position ->
                when(position){
                    0 -> {
                        tab.text = "Teachers"
                        tab.icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_person)
                    }
                    1 -> {
                        tab.text = "Schedule"
                        tab.icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_time)
                    }
                }
            }.attach()
        }
    }
}