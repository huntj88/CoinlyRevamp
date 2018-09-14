package me.jameshunt.appbase.template

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import me.jameshunt.appbase.R
import timber.log.Timber

class HeaderTemplate(view: View) : BaseTemplate<HeaderTemplateData>(view) {

    companion object {

        fun inflate(parent: ViewGroup): BaseTemplate<out BaseTemplateData> {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.template_header, parent, false)
            return HeaderTemplate(view)
        }
    }

    override fun observeData(observableData: Observable<HeaderTemplateData>) {
        disposable = observableData.subscribeBy(
                onNext = { bindData(it) },
                onError = { Timber.e(it) },
                onComplete = { Timber.i("single value view holder completed") })
    }

    private fun bindData(data: HeaderTemplateData) {
        itemView.findViewById<TextView>(R.id.titleText).text = data.title
        itemView.findViewById<TextView>(R.id.valueText).text = data.value
        itemView.findViewById<TextView>(R.id.subValueText).text = data.subValue

        itemView.setBackgroundColor(itemView.context.getColor(R.color.colorPrimary))


        val linearLayout = itemView.findViewById<LinearLayout>(R.id.linearLayout)
        linearLayout.removeAllViews()

        data.subViews.forEach {

            val subView = LayoutInflater.from(itemView.context).inflate(R.layout.template_header_item, linearLayout, false)
            subView.findViewById<TextView>(R.id.titleText).text = it.title
            subView.findViewById<TextView>(R.id.valueText).text = it.value

            linearLayout.addView(subView)
        }
    }

}

data class HeaderTemplateData(
        val title: String,
        val value: String,
        val subValue: String,
        val subViews: List<HeaderTemplateDataItem>
): BaseTemplateData

data class HeaderTemplateDataItem(
        val title: String,
        val value: String
)