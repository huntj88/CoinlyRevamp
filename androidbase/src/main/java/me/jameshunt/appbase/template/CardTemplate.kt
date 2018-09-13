package me.jameshunt.appbase.template

import android.support.v7.widget.CardView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import me.jameshunt.appbase.R
import me.jameshunt.appbase.SystemUtils
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


interface CardSectionData

data class CardHeaderData(val text: String) : CardSectionData

data class CardTextIcon(val text: String, val icon: Int, val action: () -> Unit): CardSectionData

class CardSectionFactory {

    fun create(cardSectionData: CardSectionData, parentView: LinearLayout) {
        when (cardSectionData) {
            is CardHeaderData -> {
                val view = LayoutInflater.from(parentView.context).inflate(R.layout.card_header_title, parentView, false)
                view.findViewById<TextView>(R.id.cardHeaderTitle).text = cardSectionData.text
                parentView.addView(view)
            }
            is CardTextIcon -> {
                val view = LayoutInflater.from(parentView.context).inflate(R.layout.card_text_icon, parentView, false)
                view.findViewById<TextView>(R.id.cardText).text = cardSectionData.text
                view.findViewById<ImageView>(R.id.cardIcon).setImageDrawable(parentView.context.getDrawable(cardSectionData.icon))
                parentView.addView(view)
            }
            else -> throw NotImplementedError()
        }
    }
}