package io.joshuaavalon.githubapidemo.view

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View


abstract class BindingViewHolder<in T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(model: T)

    protected val context: Context
        get() = itemView.context
}