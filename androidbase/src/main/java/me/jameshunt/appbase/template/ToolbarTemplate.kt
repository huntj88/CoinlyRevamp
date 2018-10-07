package me.jameshunt.appbase.template

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import me.jameshunt.appbase.R
import timber.log.Timber

class ToolbarTemplate(view: View): BaseTemplate<ToolbarTemplateData>(view) {

    companion object {

        fun inflate(parent: ViewGroup): BaseTemplate<out BaseTemplateData> {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.template_toolbar, parent, false)
            return ToolbarTemplate(view)
        }
    }

    override fun observeData(observableData: Observable<ToolbarTemplateData>) {
        disposable = observableData.subscribeBy(
                onNext = { bindData(it) },
                onError = { Timber.e(it) },
                onComplete = { Timber.i("single value view holder completed") })
    }

    private fun bindData(data: ToolbarTemplateData) {

        itemView.findViewById<ImageView>(R.id.backButton).setOnClickListener { data.back() }

        val dropDownTitle = itemView.findViewById<TextView>(R.id.dropDownTitle)
        dropDownTitle.text = data.title
        dropDownTitle.setOnClickListener { data.dropDownAction() }

        itemView.findViewById<ImageView>(R.id.addTransaction).setOnClickListener { data.addTransaction() }

        itemView.setBackgroundColor(itemView.context.getColor(R.color.colorPrimary))
    }
}

data class ToolbarTemplateData(
        val back: () -> Unit,
        val title: String,
        val dropDownAction: () -> Unit,
        val addTransaction: () -> Unit
): BaseTemplateData