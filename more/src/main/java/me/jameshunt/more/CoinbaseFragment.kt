package me.jameshunt.more

import io.reactivex.Observable
import me.jameshunt.appbase.UrlLauncher
import me.jameshunt.appbase.template.TemplateFactory
import me.jameshunt.appbase.template.TemplateFragment
import me.jameshunt.appbase.template.TemplateObservableWrapper
import me.jameshunt.appbase.template.TemplateViewModel
import me.jameshunt.appbase.template.card.CardDividerData
import me.jameshunt.appbase.template.card.CardHeaderData
import me.jameshunt.appbase.template.card.CardTemplateData
import me.jameshunt.appbase.template.card.CardTextIconData
import me.jameshunt.coinbase.Integration
import javax.inject.Inject

class CoinbaseFragment: TemplateFragment<CoinbaseViewModel>() {
    override fun inject() {
        parentFragment!!.moreComponent().inject(this)
    }
}

class CoinbaseViewModel @Inject constructor(private val urlLauncher: UrlLauncher) : TemplateViewModel {
    override fun getAdapterData(): Observable<List<TemplateObservableWrapper>> {
        return Observable.just(listOf(
                TemplateObservableWrapper(
                        observable = Observable.just(CardTemplateData(sections = listOf(
                                CardHeaderData(text = "Coinbase"),
                                CardDividerData(height = 1, margin = 0),
                                CardTextIconData(text = "do stuff", icon = R.drawable.leak_canary_icon, action = {
                                    urlLauncher.launchUrl(Integration().getAuthUrl())
                                })
                        ))),
                        templateType = TemplateFactory.CARD
                )
        ))
    }

    override fun cleanUp() {

    }
}
