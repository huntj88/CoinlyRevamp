package me.jameshunt.more

import io.reactivex.Observable
import me.jameshunt.appbase.template.TemplateFactory
import me.jameshunt.appbase.template.TemplateFragment
import me.jameshunt.appbase.template.TemplateObservableWrapper
import me.jameshunt.appbase.template.TemplateViewModel
import me.jameshunt.appbase.template.card.CardDividerData
import me.jameshunt.appbase.template.card.CardHeaderData
import me.jameshunt.appbase.template.card.CardTemplateData
import me.jameshunt.appbase.template.card.CardTextIconData
import javax.inject.Inject

class IntegrationsFragment: TemplateFragment<IntegrationsViewModel>() {
    override fun inject() {
        parentFragment!!.moreComponent().inject(this)
    }
}

class IntegrationsViewModel @Inject constructor() : TemplateViewModel {
    override fun getAdapterData(): Observable<List<TemplateObservableWrapper>> {
        return Observable.just(listOf(
                TemplateObservableWrapper(
                        observable = Observable.just(CardTemplateData(sections = listOf(
                                CardHeaderData(text = L10n.integrations),
                                CardDividerData(height = 1, margin = 0),
                                CardTextIconData(text = "Coinbase", icon = R.drawable.leak_canary_icon, action = {})
                        ))),
                        templateType = TemplateFactory.CARD
                )
        ))
    }

    override fun cleanUp() {

    }
}
