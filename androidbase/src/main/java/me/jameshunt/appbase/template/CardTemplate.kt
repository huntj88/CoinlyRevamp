package me.jameshunt.appbase.template

import android.support.v7.widget.CardView
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import timber.log.Timber

class CardTemplate(view: View) : BaseTemplate<CardTemplateData>(view) {

    companion object {

        fun inflate(parent: ViewGroup): BaseTemplate<out BaseTemplateData> {

            val wrapper = FrameLayout(parent.context)

            //todo: px to dp
            wrapper.setPadding(40, 40, 40, 40)

            val cardView = CardView(parent.context)

            val textView = TextView(parent.context)
            cardView.addView(textView)

            wrapper.addView(cardView)

            return CardTemplate(wrapper)
        }
    }

    override fun observeData(observableData: Observable<CardTemplateData>) {
        observableData.subscribeBy(
                onNext = {
                    itemView as FrameLayout

                    val cardView = itemView.getChildAt(0) as CardView
                    val textView = cardView.getChildAt(0) as TextView
                    textView.text = it.string
                },
                onError = { Timber.e(it) },
                onComplete = { Timber.i("Card Observe complete") }
        )
    }
}

data class CardTemplateData(val string: String) : BaseTemplateData