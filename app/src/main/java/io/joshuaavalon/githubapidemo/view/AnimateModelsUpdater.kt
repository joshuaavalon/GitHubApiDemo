package io.joshuaavalon.githubapidemo.view

import android.support.v7.widget.RecyclerView


fun <T> animateModelsUpdater(adapter: RecyclerView.Adapter<BindingViewHolder<T>>,
                             oldModels: MutableList<T>,
                             newModels: List<T>) {
    applyAndAnimateRemovals(adapter, oldModels, newModels)
    applyAndAnimateAdditions(adapter, oldModels, newModels)
    applyAndAnimateMovedItems(adapter, oldModels, newModels)
}

private fun <T> removeItem(adapter: RecyclerView.Adapter<BindingViewHolder<T>>,
                           oldModels: MutableList<T>,
                           position: Int): T {
    val model = oldModels.removeAt(position)
    adapter.notifyItemRemoved(position)
    return model
}

private fun <T> addItem(adapter: RecyclerView.Adapter<BindingViewHolder<T>>,
                        oldModels: MutableList<T>,
                        position: Int,
                        model: T) {
    oldModels.add(position, model)
    adapter.notifyItemInserted(position)
}

private fun <T> moveItem(adapter: RecyclerView.Adapter<BindingViewHolder<T>>,
                         oldModels: MutableList<T>,
                         fromPosition: Int,
                         toPosition: Int) {
    val model = oldModels.removeAt(fromPosition)
    oldModels.add(toPosition, model)
    adapter.notifyItemMoved(fromPosition, toPosition)
}

private fun <T> applyAndAnimateRemovals(adapter: RecyclerView.Adapter<BindingViewHolder<T>>,
                                        oldModels: MutableList<T>,
                                        newModels: List<T>) {
    for (index in oldModels.size - 1 downTo 0) {
        val model = oldModels[index]
        if (!newModels.contains(model))
            removeItem(adapter, oldModels, index)

    }
}

private fun <T> applyAndAnimateAdditions(adapter: RecyclerView.Adapter<BindingViewHolder<T>>,
                                         oldModels: MutableList<T>,
                                         newModels: List<T>) {
    newModels.forEachIndexed { index, model ->
        if (!oldModels.contains(model))
            addItem(adapter, oldModels, index, model)
    }
}

private fun <T> applyAndAnimateMovedItems(adapter: RecyclerView.Adapter<BindingViewHolder<T>>,
                                          oldModels: MutableList<T>,
                                          newModels: List<T>) {
    for (toPosition in newModels.size - 1 downTo 0) {
        val model = newModels[toPosition]
        val fromPosition = oldModels.indexOf(model)
        if (fromPosition >= 0 && fromPosition != toPosition) {
            moveItem(adapter, oldModels, fromPosition, toPosition)
        }
    }
}