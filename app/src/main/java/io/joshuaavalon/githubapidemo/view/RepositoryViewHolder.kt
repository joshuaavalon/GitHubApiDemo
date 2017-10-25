package io.joshuaavalon.githubapidemo.view

import android.view.View
import android.widget.TextView
import io.joshuaavalon.githubapidemo.R
import io.joshuaavalon.githubapidemo.openUrl
import io.joshuaavalon.githubapidemo.service.model.Repository


class RepositoryViewHolder(itemView: View) : BindingViewHolder<Repository>(itemView) {
    private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
    private val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
    private val languageTextView: TextView = itemView.findViewById(R.id.languageTextView)
    private val starTextView: TextView = itemView.findViewById(R.id.starTextView)
    private val forkTextView: TextView = itemView.findViewById(R.id.forkTextView)

    override fun bind(model: Repository) {
        titleTextView.text = model.name
        descriptionTextView.text = model.description
        itemView.setOnClickListener { itemView.context.openUrl(model.htmlUrl) }
        val language = model.language
        // Resetting is needed because view is reused in RecyclerView.
        if (language != null) {
            languageTextView.text = language
            languageTextView.visibility = View.VISIBLE
        } else {
            languageTextView.text = null
            languageTextView.visibility = View.GONE
        }
        starTextView.text = model.stargazersCount.toString()
        forkTextView.text = model.forksCount.toString()
    }
}