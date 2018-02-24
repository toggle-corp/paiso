package com.togglecorp.paiso

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.togglecorp.paiso.auth.Auth
import com.togglecorp.paiso.auth.LoginActivity
import com.togglecorp.paiso.contacts.ContactListFragment
import com.togglecorp.paiso.dashboard.DashboardFragment
import com.togglecorp.paiso.database.SyncManager
import com.togglecorp.paiso.expenses.ExpenseListFragment
import com.togglecorp.paiso.fcm.sendRegistrationToServer
import com.togglecorp.paiso.notifications.NotificationListFragment
import com.togglecorp.paiso.settings.SettingsActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async


class MainActivity : AppCompatActivity() {
    private var mMenu = null as Menu?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

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
                        title = "Dashboard"
                        switchTo(dashboardFragment)
                    }
                    1 -> {
                        title = "Expenses"
                        switchTo(expenseListFragment)
                    }
                    2 -> {
                        title = "Contacts"
                        switchTo(contactListFragment)
                    }
                    3 -> {
                        title = "Notifications"
                        switchTo(notificationListFragment)
                    }
                }
            }
        })

        title = "Dashboard"
        switchTo(dashboardFragment)
        setupTabLayout()
    }

    override fun onResume() {
        super.onResume()
        checkAuthenticated()
    }

    private fun refresh() {
        var button = null as ImageView?
        if (mMenu != null) {
            button = mMenu!!.findItem(R.id.refresh).actionView as ImageView

            val animation = AnimationUtils.loadAnimation(this, R.anim.rotation)
            animation.repeatCount = Animation.INFINITE
            button.startAnimation(animation)
            button.setImageResource(R.drawable.ic_sync)
            button.isEnabled = false
        }
        SyncManager.sync(this).then {
            sendRegistrationToServer(this)
            async(UI) {
                button?.clearAnimation()
                button?.setImageResource(R.drawable.ic_refresh)
                button?.isEnabled = true
            }
        }
    }

    private fun checkAuthenticated() {
        if (Auth.getToken(this) == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
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

        for (i in 0..bottomTabLayout.tabCount - 1) {
            var icon = bottomTabLayout.getTabAt(i)!!.icon

            if (icon != null) {
                icon = DrawableCompat.wrap(icon)
                DrawableCompat.setTintList(icon, colors)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        val button = (getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
                .inflate(R.layout.layout_refresh_action, null) as ImageView
        menu.findItem(R.id.refresh)?.actionView = button
        button.setImageResource(R.drawable.ic_refresh)

        button.setOnClickListener {
            refresh()
        }

        mMenu = menu
        refresh()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
