package me.jameshunt.more

import io.reactivex.Observable
import me.jameshunt.appbase.IntegrationDeepLink
import me.jameshunt.appbase.IntegrationDeepLinkHandler
import me.jameshunt.appbase.UrlLauncher
import me.jameshunt.appbase.template.TemplateFactory
import me.jameshunt.appbase.template.TemplateFragment
import me.jameshunt.appbase.template.TemplateObservableWrapper
import me.jameshunt.appbase.template.TemplateViewModel
import me.jameshunt.appbase.template.card.CardDividerData
import me.jameshunt.appbase.template.card.CardHeaderData
import me.jameshunt.appbase.template.card.CardTemplateData
import me.jameshunt.appbase.template.card.CardTextIconData
import me.jameshunt.base.IntegrationStatus
import me.jameshunt.business.IntegrationUseCase
import me.jameshunt.coinbase.CoinbaseIntegration
import javax.inject.Inject

class IntegrationsFragment: TemplateFragment<IntegrationsViewModel>() {
    override fun inject() {
        parentFragment!!.moreComponent().inject(this)
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkForDeepLinkData()
    }
}

class IntegrationsViewModel @Inject constructor(
        private val moreFragmentVisibilityManager: MoreFragmentVisibilityManager,
        private val deepLinkHandler: IntegrationDeepLinkHandler,
        private val urlLauncher: UrlLauncher,
        private val integrationUseCase: IntegrationUseCase
) : TemplateViewModel {
    override fun getAdapterData(): Observable<List<TemplateObservableWrapper>> {
        return Observable.just(listOf(
                TemplateObservableWrapper(
                        observable = Observable.just(CardTemplateData(sections = listOf(
                                CardHeaderData(text = L10n.integrations),
                                CardDividerData(heightDp = 1, margin = 0),
                                CardTextIconData(text = "Coinbase", icon = R.drawable.leak_canary_icon, action = {
                                    when(integrationUseCase.coinbaseIntegrationStatus) {
                                        IntegrationStatus.NotIntegrated -> urlLauncher.launchUrl(CoinbaseIntegration.getAuthUrl())
                                        IntegrationStatus.Integrated -> moreFragmentVisibilityManager.showCoinbase()
                                    }

                                })
                        ))),
                        templateType = TemplateFactory.CARD
                )
        ))
    }

    fun checkForDeepLinkData() {
        deepLinkHandler.deepLink?.let {
            when(it) {
                is IntegrationDeepLink.Coinbase -> moreFragmentVisibilityManager.showCoinbase()
            }
        }
    }

    override fun cleanUp() {

    }
}
