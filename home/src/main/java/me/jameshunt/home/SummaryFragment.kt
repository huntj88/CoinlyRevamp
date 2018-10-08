package me.jameshunt.home

import io.reactivex.Observable
import me.jameshunt.appbase.template.*
import me.jameshunt.appbase.template.card.*
import me.jameshunt.base.CurrencyType
import me.jameshunt.base.DataSource
import me.jameshunt.business.CurrencyTypeExchangeRateUseCase
import me.jameshunt.business.EnabledCurrencyUseCase
import me.jameshunt.base.SelectedCurrencyUseCase
import javax.inject.Inject

class SummaryFragment : TemplateFragment<SummaryViewModel>() {
    override fun inject() {
        parentFragment!!.homeComponent().inject(this)
    }
}

class SummaryViewModel @Inject constructor(
        private val visibilityManager: HomeFragmentVisibilityManager,
        private val enabledCurrencyUseCase: EnabledCurrencyUseCase,
        private val selectedCurrencyUseCase: SelectedCurrencyUseCase,
        private val exchangeRateUseCase: CurrencyTypeExchangeRateUseCase
) : TemplateViewModel {

    override fun getAdapterData(): Observable<List<TemplateObservableWrapper>> {
        return enabledCurrencyUseCase.getEnabledCurrencies().map { getAdapterObservables(it) }
    }

    private fun getAdapterObservables(enabled: Set<CurrencyType>): List<TemplateObservableWrapper> {
        return mutableListOf(
                TemplateObservableWrapper(getHeader(), TemplateFactory.HEADER)
        ).apply {
            this.addAll(enabled.map { TemplateObservableWrapper(getTargetCurrencyCard(it), TemplateFactory.CARD) })
        }
    }

    private fun getHeader(): Observable<HeaderTemplateData> {
        return Observable.just(HeaderTemplateData(
                title = L10n.gain_on_portfolio,
                value = "$12,021.12",
                subValue = "+24.12%",
                subViews = listOf(
                        HeaderTemplateDataItem(title = L10n.balance, value = "0000"),
                        HeaderTemplateDataItem(title = L10n.paid, value = "0100"),
                        HeaderTemplateDataItem(title = L10n.net_profit, value = "0020"),
                        HeaderTemplateDataItem(title = L10n.sold, value = "0003")
                )
        ))
    }

    private fun getTargetCurrencyCard(target: CurrencyType): Observable<CardTemplateData> {
        return selectedCurrencyUseCase
                .getSelectedBase()
                .flatMap { exchangeRateUseCase.getCurrentExchangeRate(it, target) }
                .map {
                    when (it) {
                        is DataSource.Success -> currencyCardUI(target = target, price = (1.0 / it.data).toString())
                        is DataSource.Error -> currencyCardUI(target = target, price = "price not available")
                    }
                }
    }

    private fun currencyCardUI(target: CurrencyType, price: String): CardTemplateData {
        return CardTemplateData(sections = listOf(
                CardHeaderActionData(text = target.fullName, actionText = "view more", action = {
                    selectedCurrencyUseCase.setSelectedTarget(target)
                    visibilityManager.showPortfolio()
                }),
                CardDividerData(height = 1, margin = 0),
                CardTitleTwoValueData(
                        title = L10n.gain_on_currency(target.fullName),
                        value = "$2,512.42",
                        subValue = "+121.21%"
                ),
                CardSlidingData(data = listOf(
                        CardSlidingData.CardSlideItemData(title = L10n.price, value = price),
                        CardSlidingData.CardSlideItemData(title = L10n.balance, value = "0000"),
                        CardSlidingData.CardSlideItemData(title = L10n.paid, value = "0100"),
                        CardSlidingData.CardSlideItemData(title = L10n.net_profit, value = "0020"),
                        CardSlidingData.CardSlideItemData(title = L10n.sold, value = "0003")
                )),
                CardDividerData(height = 20, margin = 0, color = R.color.colorAccent)
        ))
    }

    override fun cleanUp() {

    }
}
