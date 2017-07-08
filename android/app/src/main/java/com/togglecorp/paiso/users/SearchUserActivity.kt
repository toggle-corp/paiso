package com.togglecorp.paiso.users

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.togglecorp.paiso.R
import com.togglecorp.paiso.api.promise
import com.togglecorp.paiso.auth.Auth
import kotlinx.android.synthetic.main.activity_search_user.*


val SEARCH_USER = 0xbd

class SearchUserActivity : AppCompatActivity() {

    private var userListAdapter: UserListAdapter? = null
    private val userList = mutableListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_user)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Search user"

        userListAdapter = UserListAdapter(this, userList, {
            select(it)
        })
        userListView.layoutManager = LinearLayoutManager(this)
        userListView.adapter = userListAdapter

        searchMessage.visibility = View.VISIBLE
        userListView.visibility = View.GONE

        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_SEARCH) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            search(query)
        }
    }

    private fun select(user: User) {
        if (user.remoteId == null) {
            return
        }

        val data = Intent()
        data.putExtra("userRemoteId", user.remoteId!!)
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    private fun search(query: String) {
        if (TextUtils.isEmpty(query.trim())) {
            userList.clear()
            userListAdapter?.notifyDataSetChanged()
            return
        }

        searchMessage.visibility = View.GONE
        userListView.visibility = View.VISIBLE

        UserApi.search(Auth.getHeader(this), query).promise()
                .then {
                    userList.clear()
                    it?.body()?.filter { it.username != Auth.getUsername(this) }
                            ?.forEach { userList.add(it) }

                    runOnUiThread {
                        userListAdapter?.notifyDataSetChanged()
                    }
                }
                .catch {
                    it?.printStackTrace()
                }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_user, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.menu_search).actionView as SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.setIconifiedByDefault(false)
        return true
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

