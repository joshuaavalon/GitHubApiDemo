package io.joshuaavalon.githubapidemo.view

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Used by [BindingRecyclerAdapter].
 */
abstract class BindingViewHolder<in T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    /**
     * Bind the model data to the view when the view is shown on screen.
     *
     * @param model Data model.
     */
    abstract fun bind(model: T)

    protected val context: Context
        get() = itemView.context
}