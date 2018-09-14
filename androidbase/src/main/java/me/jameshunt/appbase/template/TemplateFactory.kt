package me.jameshunt.appbase.template

import android.view.ViewGroup
import me.jameshunt.appbase.template.card.CardTemplate

class TemplateFactory {

    companion object {
        val CARD = 0
    }

    fun createTemplate(parent: ViewGroup, templateType: Int): BaseTemplate<out BaseTemplateData> {
        return when(templateType) {
            CARD -> CardTemplate.inflate(parent)
            else -> throw IllegalArgumentException("type not registered in TemplateFactory: $templateType")
        }
    }
}