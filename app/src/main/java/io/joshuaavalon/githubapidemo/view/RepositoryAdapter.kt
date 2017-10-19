package io.joshuaavalon.githubapidemo.view

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import io.joshuaavalon.githubapidemo.R
import io.joshuaavalon.githubapidemo.service.model.Repository


class RepositoryAdapter(private val repositories: List<Repository>) :
        RecyclerView.Adapter<RepositoryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepositoryViewHolder =
            RepositoryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_repo, parent, false))

    override fun onBindViewHolder(holder: RepositoryViewHolder, position: Int) {
        holder.bind(repositories[position])
    }

    override fun getItemCount(): Int = repositories.size
}