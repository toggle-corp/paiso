package com.togglecorp.paiso.settings

import android.app.Activity
import android.os.Bundle
import android.view.MenuItem
import com.togglecorp.paiso.R
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setActionBar(toolbar)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Settings"

        fragmentManager.beginTransaction()
                .replace(R.id.content, SettingsFragment())
                .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}