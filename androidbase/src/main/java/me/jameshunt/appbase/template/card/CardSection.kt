package me.jameshunt.appbase.template.card

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import me.jameshunt.appbase.R
import me.jameshunt.appbase.SystemUtils
import me.jameshunt.base.TimeType


interface CardSectionData

data class CardHeaderData(val text: String) : CardSectionData

data class CardHeaderActionData(val text: String, val actionText: String, val action: () -> Unit): CardSectionData

data class CardDividerData(val height: Int, val margin: Int, val color: Int? = null) : CardSectionData

data class CardTextIconData(val text: String, val icon: Int, val action: () -> Unit) : CardSectionData

data class CardTitleTwoValueData(val title: String, val value: String, val subValue: String) : CardSectionData

data class CardTimeSelectData(
        val selected: TimeType,
        val hour: () -> Unit,
        val day: () -> Unit,
        val week: () -> Unit,
        val month: () -> Unit,
        val year: () -> Unit
) : CardSectionData

data class CardSlidingData(val data: List<CardSlideItemData>) : CardSectionData {
    data class CardSlideItemData(val title: String, val value: String)
}

class CardSectionFactory {

    fun create(cardSectionData: CardSectionData, parentView: LinearLayout) {
        val view: View = when (cardSectionData) {
            is CardHeaderData -> {
                val view = LayoutInflater.from(parentView.context).inflate(R.layout.card_header_title, parentView, false)
                view.findViewById<TextView>(R.id.cardHeaderTitle).text = cardSectionData.text
                view
            }
            is CardHeaderActionData -> {
                val view = LayoutInflater.from(parentView.context).inflate(R.layout.card_header_action, parentView, false)
                view.findViewById<TextView>(R.id.cardHeaderTitle).text = cardSectionData.text
                val actionTextView = view.findViewById<TextView>(R.id.actionText)
                actionTextView.text = cardSectionData.actionText
                actionTextView.setOnClickListener { cardSectionData.action() }
                view
            }
            is CardDividerData -> {
                val view = View(parentView.context)
                val color = cardSectionData.color?: R.color.divider
                view.setBackgroundColor(parentView.context.getColor(color))

                val height = SystemUtils.dpToPx(cardSectionData.height, parentView.context)
                val margin = SystemUtils.dpToPx(cardSectionData.margin, parentView.context)

                val layoutParams = ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height)
                layoutParams.setMargins(margin, 0, margin, 0)
                view.layoutParams = layoutParams
                view
            }
            is CardTextIconData -> {
                val view = LayoutInflater.from(parentView.context).inflate(R.layout.card_text_icon, parentView, false)
                view.findViewById<TextView>(R.id.cardText).text = cardSectionData.text
                view.findViewById<ImageView>(R.id.cardIcon).setImageDrawable(parentView.context.getDrawable(cardSectionData.icon))
                view.setOnClickListener { cardSectionData.action() }
                view
            }
            is CardTitleTwoValueData -> {
                val view = LayoutInflater.from(parentView.context).inflate(R.layout.card_title_two_value, parentView, false)
                view.findViewById<TextView>(R.id.titleText).text = cardSectionData.title
                view.findViewById<TextView>(R.id.valueText).text = cardSectionData.value
                view.findViewById<TextView>(R.id.subValueText).text = cardSectionData.subValue
                view
            }
            is CardTimeSelectData -> {
                val view = LayoutInflater.from(parentView.context).inflate(R.layout.card_time_select, parentView, false)

                val hourButton = view.findViewById<TextView>(R.id.hourTextView)
                val dayButton = view.findViewById<TextView>(R.id.dayTextView)
                val weekButton = view.findViewById<TextView>(R.id.weekTextView)
                val monthButton = view.findViewById<TextView>(R.id.monthTextView)
                val yearButton = view.findViewById<TextView>(R.id.yearTextView)

                hourButton.setOnClickListener {
                    cardSectionData.hour()
                }

                dayButton.setOnClickListener {
                    cardSectionData.day()
                }

                weekButton.setOnClickListener {
                    cardSectionData.week()
                }

                monthButton.setOnClickListener {
                    cardSectionData.month()
                }

                yearButton.setOnClickListener {
                    cardSectionData.year()
                }

                view
            }
            is CardSlidingData -> {
                val view = LayoutInflater.from(parentView.context).inflate(R.layout.card_sliding, parentView, false)
                val subLinearLayout = view.findViewById<LinearLayout>(R.id.subLinearLayout)

                cardSectionData.data.forEach {
                    val subView = LayoutInflater.from(parentView.context).inflate(R.layout.card_sliding_item, parentView, false)
                    subView.findViewById<TextView>(R.id.titleText).text = it.title
                    subView.findViewById<TextView>(R.id.valueText).text = it.value

                    subLinearLayout.addView(subView)
                }

                view
            }
            else -> throw NotImplementedError()
        }

        parentView.addView(view)
    }
}