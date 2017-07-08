package com.togglecorp.paiso

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.app.AppCompatActivity
import com.togglecorp.paiso.auth.Auth
import com.togglecorp.paiso.auth.LoginActivity
import com.togglecorp.paiso.contacts.ContactListFragment
import com.togglecorp.paiso.dashboard.DashboardFragment
import com.togglecorp.paiso.database.SyncManager
import com.togglecorp.paiso.expenses.ExpenseListFragment
import com.togglecorp.paiso.fcm.sendRegistrationToServer
import com.togglecorp.paiso.notifications.NotificationListFragment
import com.togglecorp.paiso.settings.SettingsFragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setActionBar(toolbar)

        checkAuthenticated()

        val dashboardFragment = DashboardFragment()
        val contactListFragment = ContactListFragment()
        val notificationListFragment = NotificationListFragment()
        val expenseListFragment = ExpenseListFragment()

        bottomTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        switchTo(dashboardFragment)
                    }
                    1 -> {
                        switchTo(expenseListFragment)
                    }
                    2 -> {
                        switchTo(contactListFragment)
                    }
                    3 -> {
                        switchTo(notificationListFragment)
                    }
                }
            }
        })

        switchTo(dashboardFragment)
        setupTabLayout()
    }

    override fun onResume() {
        super.onResume()
        checkAuthenticated()

        SyncManager.fetch(this).then { sendRegistrationToServer(this) }
    }

    fun checkAuthenticated() {
        if (Auth.getToken(this) == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
    }

    override fun onPause() {
        super.onPause()
    }

    private fun switchTo(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
    }


    private fun setupTabLayout() {
        val colors: ColorStateList
        if (Build.VERSION.SDK_INT >= 23) {
            colors = resources.getColorStateList(R.color.tab_icon, theme)
        } else {
            colors = resources.getColorStateList(R.color.tab_icon)
        }

        for (i in 0..bottomTabLayout.getTabCount() - 1) {
            val tab = bottomTabLayout.getTabAt(i)
            var icon = tab!!.getIcon()

            if (icon != null) {
                icon = DrawableCompat.wrap(icon)
                DrawableCompat.setTintList(icon, colors)
            }
        }
    }
}
