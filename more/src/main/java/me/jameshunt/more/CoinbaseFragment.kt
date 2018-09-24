package me.jameshunt.more

import io.reactivex.Observable
import me.jameshunt.appbase.IntegrationDeepLink
import me.jameshunt.appbase.IntegrationDeepLinkHandler
import me.jameshunt.appbase.template.TemplateFactory
import me.jameshunt.appbase.template.TemplateFragment
import me.jameshunt.appbase.template.TemplateObservableWrapper
import me.jameshunt.appbase.template.TemplateViewModel
import me.jameshunt.appbase.template.card.CardDividerData
import me.jameshunt.appbase.template.card.CardHeaderData
import me.jameshunt.appbase.template.card.CardTemplateData
import me.jameshunt.appbase.template.card.CardTextIconData
import timber.log.Timber
import javax.inject.Inject

class CoinbaseFragment: TemplateFragment<CoinbaseViewModel>() {
    override fun inject() {
        parentFragment!!.moreComponent().inject(this)
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkForDeepLinkData()
    }
}

class CoinbaseViewModel @Inject constructor(private val deepLinkHandler: IntegrationDeepLinkHandler) : TemplateViewModel {
    override fun getAdapterData(): Observable<List<TemplateObservableWrapper>> {
        return Observable.just(listOf(
                TemplateObservableWrapper(
                        observable = Observable.just(CardTemplateData(sections = listOf(
                                CardHeaderData(text = "Coinbase"),
                                CardDividerData(height = 1, margin = 0),
                                CardTextIconData(text = "do stuff", icon = R.drawable.leak_canary_icon, action = {

                                })
                        ))),
                        templateType = TemplateFactory.CARD
                )
        ))
    }

    fun checkForDeepLinkData() {
        deepLinkHandler.consumeDeepLinkData<IntegrationDeepLink.Coinbase>()?.let {
            Timber.i("do stuff with code: ${it.code}")
        }
    }

    override fun cleanUp() {

    }
}
