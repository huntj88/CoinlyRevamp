package me.jameshunt.appbase.template.card

import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import me.jameshunt.appbase.R
import me.jameshunt.base.TimeType


interface CardSectionData

data class CardHeaderData(val text: String) : CardSectionData

data class CardTextIconData(val text: String, val icon: Int, val action: () -> Unit) : CardSectionData

data class CardTimeSelectData(
        val selected: TimeType,
        val hour: () -> Unit,
        val day: () -> Unit,
        val week: () -> Unit,
        val month: () -> Unit,
        val year: () -> Unit
): CardSectionData

class CardSectionFactory {

    fun create(cardSectionData: CardSectionData, parentView: LinearLayout) {
        when (cardSectionData) {
            is CardHeaderData -> {
                val view = LayoutInflater.from(parentView.context).inflate(R.layout.card_header_title, parentView, false)
                view.findViewById<TextView>(R.id.cardHeaderTitle).text = cardSectionData.text
                parentView.addView(view)
            }
            is CardTextIconData -> {
                val view = LayoutInflater.from(parentView.context).inflate(R.layout.card_text_icon, parentView, false)
                view.findViewById<TextView>(R.id.cardText).text = cardSectionData.text
                view.findViewById<ImageView>(R.id.cardIcon).setImageDrawable(parentView.context.getDrawable(cardSectionData.icon))
                parentView.addView(view)
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

                parentView.addView(view)
            }
            else -> throw NotImplementedError()
        }
    }
}