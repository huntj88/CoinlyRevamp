package me.jameshunt.appbase.template.card

import android.support.v7.widget.CardView
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import me.jameshunt.appbase.SystemUtils
import me.jameshunt.appbase.template.BaseTemplate
import me.jameshunt.appbase.template.BaseTemplateData
import timber.log.Timber

class CardTemplate(view: View) : BaseTemplate<CardTemplateData>(view) {

    companion object {

        fun inflate(parent: ViewGroup): BaseTemplate<out BaseTemplateData> {
            val wrapper = FrameLayout(parent.context)
            wrapper.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

            val cardView = CardView(parent.context)

            val marginLayoutParams = ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            val margin = SystemUtils.dpToPx(24, parent.context)
            marginLayoutParams.setMargins(margin, margin / 3, margin, margin / 3)

            cardView.layoutParams = marginLayoutParams
            cardView.radius = SystemUtils.dpToPx(24f, parent.context)
            cardView.cardElevation = SystemUtils.dpToPx(4f, parent.context)


            val linearLayout = LinearLayout(parent.context)
            linearLayout.gravity = Gravity.CENTER
            linearLayout.orientation = LinearLayout.VERTICAL

            cardView.addView(linearLayout)
            wrapper.addView(cardView)

            return CardTemplate(wrapper)
        }
    }

    private val cardSectionFactory = CardSectionFactory()

    override fun observeData(observableData: Observable<CardTemplateData>) {
        observableData.subscribeBy(
                onNext = { templateData ->
                    val cardView = (itemView as FrameLayout).getChildAt(0) as CardView
                    val linearLayout = cardView.getChildAt(0) as LinearLayout
                    linearLayout.removeAllViews()

                    templateData.sections.forEach { sectionData ->
                        cardSectionFactory.create(sectionData, linearLayout)
                    }
                },
                onError = { Timber.e(it) },
                onComplete = { Timber.i("Card Observe complete") }
        )
    }
}

data class CardTemplateData(val sections: List<CardSectionData>) : BaseTemplateData