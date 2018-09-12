package me.jameshunt.appbase.template

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

class TemplateAdapter(
        private val dataObservables: List<TemplateObservableWrapper>,
        private val templateFactory: TemplateFactory
) : RecyclerView.Adapter<BaseTemplate<BaseTemplateData>>() {

    private val compositeDisposable = CompositeDisposable()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseTemplate<BaseTemplateData> {
        @Suppress("UNCHECKED_CAST")
        return templateFactory.createTemplate(parent, viewType) as BaseTemplate<BaseTemplateData>
    }

    override fun onBindViewHolder(holder: BaseTemplate<BaseTemplateData>, position: Int) {
        holder.disposable?.let {
            if (!it.isDisposed) {
                compositeDisposable.remove(it)
                // this will dispose the disposable as well
            }
        }

        bind(holder, position)
    }

    private fun bind(holder: BaseTemplate<BaseTemplateData>, position: Int) {
        @Suppress("UNCHECKED_CAST")
        holder.observeData(dataObservables[position].observable as Observable<BaseTemplateData>)

        holder.disposable?.let {
            compositeDisposable.add(it)
        }
    }

    override fun getItemCount(): Int {
        return dataObservables.size
    }

    override fun getItemViewType(position: Int): Int {
        return dataObservables[position].templateType
    }

    fun cleanUp() {
        compositeDisposable.dispose()
    }
}

data class TemplateObservableWrapper(
        val observable: Observable<out BaseTemplateData>,
        val templateType: Int)