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

    /*
     * The following variables use `lateinit` because Android does not initialize the activity on
     * constructor. Using `lateinit` allows you to defer initialization to `onCreate()`.
     */
    /**
     * GitHub User Name
     */
    private lateinit var user: String
    private lateinit var adapter: BindingRecyclerAdapter<Repository>
    /**
     * Field that use to sort the repositories.
     */
    private lateinit var sortField: Repository.Sort
    /**
     * Sort order: Ascending or Descending
     */
    private lateinit var sortOrder: SortOrder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the layout that your activity are going to inflate.
        setContentView(R.layout.activity_main)

        // Load the user from previous pause or load default user.
        // This should be save to preferences instead to persist setting after app closing
        // Same the the sort field and sort order.
        user = savedInstanceState?.getString(userKey) ?: defaultUser
        val sortFieldOrdinal = savedInstanceState?.getInt(sortFieldKey) ?: 0
        val sortOrderOrdinal = savedInstanceState?.getInt(sortOrderKey) ?: 0
        sortField = Repository.Sort::class.java.enumConstants[sortFieldOrdinal]
        sortOrder = SortOrder::class.java.enumConstants[sortOrderOrdinal]

        // Assign toolbar as action bar
        setSupportActionBar(toolbar)
        // Hide the default title on action bar
        supportActionBar?.setDisplayShowTitleEnabled(false)
        // Use title on `CollapsingToolbarLayout` instead.
        // Set the color to transparent when it is showing image.
        collapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT)
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE)

        // Initialize recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = BindingRecyclerAdapter.Builder<Repository>()
                .viewHolderFactory(R.layout.item_repo, ::RepositoryViewHolder)
                .modelsUpdater(::animateModelsUpdater)
                .build()
        recyclerView.adapter = adapter

        // When the `FloatingActionButton` is clicked, open user's GitHub page.
        floatingActionButton.setOnClickListener { openUrl("https://github.com/$user") }

        loadUser()
    }

    /**
     * This method load the data by user name and bind them to the views.
     */
    private fun loadUser() {
        // Set the title on top.
        collapsingToolbarLayout.title = user
        val gitHubService = GitHubService()
        gitHubService.getUser(user, {
            // When the user data is load, bind the user's profile image to ImageView on top.
            Picasso.with(this).load(it.avatarUrl).into(toolbarImageView)
        }, {
            // Show the error for network request that is not working.
            Snackbar.make(coordinatorLayout, it.toString(), Snackbar.LENGTH_LONG).show()
        })
        gitHubService.listUserRepositories(user, {
            setSortRepository(it)
        }, {
            // Show the error for network request that is not working.
            Snackbar.make(coordinatorLayout, it.toString(), Snackbar.LENGTH_LONG).show()
        })
    }

    /**
     * Inflate an options menu on action bar.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    /**
     * When the options menu has an item selected, open call the appropriate functions.
     * Item not found can be pass to parent instead.
     */
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

    /**
     * Preserve variables on pause.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(userKey, user)
        outState.putInt(sortFieldKey, sortField.ordinal)
        outState.putInt(sortOrderKey, sortOrder.ordinal)
    }

    /**
     * Show an dialog that allow user to change the sorting field.
     */
    private fun showSortFieldDialog() {
        val fields = Repository.Sort::class.java.enumConstants
        val items = fields.map { getString(it.resId) }
        MaterialDialog.Builder(this)
                .title(R.string.sort_by)
                .items(items)
                .itemsCallbackSingleChoice(sortField.ordinal) { _, _, which, _ ->
                    val fieldChanged = fields[which] == sortField
                    sortField = fields[which]
                    // Show the sort order dialog after user selected the field.
                    showSortOrderDialog(fieldChanged)
                    true
                }
                .show()
    }

    /**
     * Show an dialog that allow user to change the sorting order.
     */
    private fun showSortOrderDialog(fieldChanged: Boolean) {
        val fields = SortOrder::class.java.enumConstants
        val items = fields.map { getString(it.resId) }
        MaterialDialog.Builder(this)
                .title(R.string.sort_order)
                .items(items)
                .itemsCallbackSingleChoice(sortOrder.ordinal) { _, _, which, _ ->
                    sortOrder = fields[which]
                    // Update the RecyclerView with sorted repository
                    setSortRepository()
                    true
                }
                .cancelListener {
                    // This should be call when sort field is changed.
                    if (fieldChanged)
                    // Update the RecyclerView with sorted repository
                        setSortRepository()
                }
                .show()
    }

    /**
     * Update the RecyclerView with sorted repository
     */
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
        // Reset the position back to top.
        recyclerView.layoutManager.scrollToPosition(0)
        appBarLayout.setExpanded(true)
    }

    /**
     * Show an dialog that allow user to input GitHub user name.
     */
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