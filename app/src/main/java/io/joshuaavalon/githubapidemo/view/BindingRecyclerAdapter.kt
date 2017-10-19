package io.joshuaavalon.githubapidemo.view

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


class BindingRecyclerAdapter<T> private constructor(models: List<T>,
                                                    private val viewHolderFactory: (ViewGroup, Int) -> BindingViewHolder<T>,
                                                    private val viewTypeFactory: (List<T>, Int) -> Int,
                                                    private val modelsUpdater: (RecyclerView.Adapter<BindingViewHolder<T>>, MutableList<T>, List<T>) -> Unit)
    : RecyclerView.Adapter<BindingViewHolder<T>>() {

    class Builder<T> {
        private var viewHolderFactory: ((ViewGroup, Int) -> BindingViewHolder<T>)? = null
        private var viewTypeFactory: (List<T>, Int) -> Int = { _, _ -> 0 }
        private var models: List<T>? = null
        private var modelsUpdater: (RecyclerView.Adapter<BindingViewHolder<T>>, MutableList<T>, List<T>) -> Unit =
                { adapter, oldModels, netModels ->
                    oldModels.clear()
                    oldModels.addAll(netModels)
                    adapter.notifyDataSetChanged()
                }

        @JvmName("viewHolderGroupFactory")
        fun viewHolderFactory(factory: (ViewGroup, Int) -> BindingViewHolder<T>): Builder<T> {
            viewHolderFactory = factory
            return this
        }

        fun viewHolderFactory(layoutIdFactory: (Int) -> Int,
                              viewFactory: (View, Int) -> BindingViewHolder<T>): Builder<T> {
            return viewHolderFactory { viewGroup, type ->
                val layoutId = layoutIdFactory.invoke(type)
                val view = LayoutInflater.from(viewGroup.context)
                        .inflate(layoutId, viewGroup, false)
                viewFactory.invoke(view, type)
            }
        }

        fun viewHolderFactory(layoutId: Int, factory: (View) -> BindingViewHolder<T>): Builder<T> {
            return viewHolderFactory { viewGroup, _ ->
                val view = LayoutInflater.from(viewGroup.context)
                        .inflate(layoutId, viewGroup, false)
                factory.invoke(view)
            }
        }

        fun viewTypeFactory(factory: (List<T>, Int) -> Int): Builder<T> {
            viewTypeFactory = factory
            return this
        }

        fun modelsUpdater(updater: (RecyclerView.Adapter<BindingViewHolder<T>>, MutableList<T>, List<T>) -> Unit): Builder<T> {
            modelsUpdater = updater
            return this
        }

        fun build(): BindingRecyclerAdapter<T> {
            val factory = viewHolderFactory ?:
                    throw NullPointerException("You need to call viewHolderFactory()")
            val models = this.models?.toList() ?: listOf()
            return BindingRecyclerAdapter(models, factory, viewTypeFactory, modelsUpdater)
        }
    }

    private var _models = models.toMutableList()

    var models: List<T>
        get() = _models
        set(value) {
            modelsUpdater(this, _models, value)
        }

    override fun onBindViewHolder(holder: BindingViewHolder<T>, position: Int) {
        holder.bind(models[position])
    }

    override fun getItemCount(): Int = models.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<T> =
            viewHolderFactory.invoke(parent, viewType)

    override fun getItemViewType(position: Int): Int = viewTypeFactory.invoke(models, position)
}
