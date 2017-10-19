package io.joshuaavalon.githubapidemo

import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import com.afollestad.materialdialogs.MaterialDialog
import com.squareup.picasso.Picasso
import io.joshuaavalon.githubapidemo.service.GitHubService
import io.joshuaavalon.githubapidemo.service.model.Repository
import io.joshuaavalon.githubapidemo.view.BindingRecyclerAdapter
import io.joshuaavalon.githubapidemo.view.RepositoryViewHolder
import io.joshuaavalon.githubapidemo.view.animateModelsUpdater
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    companion object {
        val defaultUser = "google"
        private val userKey = "io.joshuaavalon.githubapidemo.userKey"
        private val sortFieldKey = "io.joshuaavalon.githubapidemo.sortFieldKey"
        private val sortOrderKey = "io.joshuaavalon.githubapidemo.sortOrderKey"
    }

    private lateinit var user: String
    private lateinit var adapter: BindingRecyclerAdapter<Repository>
    private lateinit var sortField: Repository.Sort
    private lateinit var sortOrder: SortOrder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        user = savedInstanceState?.getString(userKey) ?: defaultUser
        val sortFieldOrdinal = savedInstanceState?.getInt(sortFieldKey) ?: 0
        val sortOrderOrdinal = savedInstanceState?.getInt(sortOrderKey) ?: 0

        sortField = Repository.Sort::class.java.enumConstants[sortFieldOrdinal]
        sortOrder = SortOrder::class.java.enumConstants[sortOrderOrdinal]

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        collapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT)
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = BindingRecyclerAdapter.Builder<Repository>()
                .viewHolderFactory(R.layout.item_repo, ::RepositoryViewHolder)
                .modelsUpdater(::animateModelsUpdater)
                .build()
        recyclerView.adapter = adapter
        floatingActionButton.setOnClickListener { openUrl("https://github.com/$user") }
        loadUser()
    }

    private fun loadUser() {
        collapsingToolbarLayout.title = user
        val githubService = GitHubService()
        githubService.getUser(user, {
            Picasso.with(this).load(it.avatarUrl).into(toolbarImageView)
        }, {
            Snackbar.make(coordinatorLayout, it.toString(), Snackbar.LENGTH_LONG).show()
        })
        githubService.listUserRepositories(user, {
            setSortRepository(it)
        }, {
            Snackbar.make(coordinatorLayout, it.toString(), Snackbar.LENGTH_LONG).show()
        })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
            when (item.itemId) {
                R.id.menu_edit -> {
                    showInputDialog()
                    true
                }
                R.id.menu_sort -> {
                    showSortFieldDialog()
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(userKey, user)
        outState.putInt(sortFieldKey, sortField.ordinal)
        outState.putInt(sortOrderKey, sortOrder.ordinal)
    }

    private fun showSortFieldDialog() {
        val fields = Repository.Sort::class.java.enumConstants
        val items = fields.map { getString(it.resId) }
        MaterialDialog.Builder(this)
                .title(R.string.sort_by)
                .items(items)
                .itemsCallbackSingleChoice(sortField.ordinal) { _, _, which, _ ->
                    sortField = fields[which]
                    showSortOrderDialog()
                    true
                }
                .show()
    }

    private fun showSortOrderDialog() {
        val fields = SortOrder::class.java.enumConstants
        val items = fields.map { getString(it.resId) }
        MaterialDialog.Builder(this)
                .title(R.string.sort_order)
                .items(items)
                .itemsCallbackSingleChoice(sortOrder.ordinal) { _, _, which, _ ->
                    sortOrder = fields[which]
                    setSortRepository()
                    true
                }
                .cancelListener {
                    setSortRepository()
                }
                .show()
    }

    private fun setSortRepository(models: List<Repository> = adapter.models) {
        val comparator = when (sortField) {
            Repository.Sort.Name -> compareBy<Repository> { it.name }
            Repository.Sort.Star -> compareBy { it.stargazersCount }
            Repository.Sort.LastPush -> compareBy { it.pushedAt }
            Repository.Sort.Fork -> compareBy { it.forksCount }
        }
        adapter.models = if (sortOrder == SortOrder.Ascending)
            models.sortedWith(comparator)
        else
            models.sortedWith(comparator).reversed()
        recyclerView.layoutManager.scrollToPosition(0)
        appBarLayout.setExpanded(true)
    }


    private fun showInputDialog() {
        MaterialDialog.Builder(this)
                .title("Enter User")
                .inputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS)
                .input(null, null) { _, input ->
                    if (input.isBlank())
                        return@input
                    user = input.toString()
                    loadUser()
                }.show()
    }
}