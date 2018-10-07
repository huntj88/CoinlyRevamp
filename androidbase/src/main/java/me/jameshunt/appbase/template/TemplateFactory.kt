package me.jameshunt.appbase.template

import android.view.ViewGroup
import me.jameshunt.appbase.template.card.CardTemplate

class TemplateFactory {

    companion object {
        const val CARD = 0
        const val HEADER = 1
        const val TOOLBAR = 2
    }

    fun createTemplate(parent: ViewGroup, templateType: Int): BaseTemplate<out BaseTemplateData> {
        return when(templateType) {
            CARD -> CardTemplate.inflate(parent)
            HEADER -> HeaderTemplate.inflate(parent)
            TOOLBAR -> ToolbarTemplate.inflate(parent)
            else -> throw IllegalArgumentException("type not registered in TemplateFactory: $templateType")
        }
    }
}