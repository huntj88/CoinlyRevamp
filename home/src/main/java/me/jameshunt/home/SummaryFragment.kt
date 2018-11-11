package me.jameshunt.home

import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import me.jameshunt.appbase.template.*
import me.jameshunt.appbase.template.card.*
import me.jameshunt.base.*
import me.jameshunt.business.*
import me.jameshunt.business.gain.GainUseCase
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
        private val exchangeRateUseCase: ExchangeRateUseCase,
        private val valueUseCase: ValueUseCase,
        private val paidUseCase: PaidUseCase,
        private val gainUseCase: GainUseCase
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
                        HeaderTemplateDataItem(title = L10n.realized_gain, value = "0020"),
                        HeaderTemplateDataItem(title = L10n.sold, value = "0003")
                )
        ))
    }

    private fun getTargetCurrencyCard(target: CurrencyType): Observable<CardTemplateData> {

        val totalGainObservable = gainUseCase.getNetProfit(currencyType = target)
        val slidingDataObservable = slidingPartOfCard(target)

        return Observables.combineLatest(totalGainObservable, slidingDataObservable) { totalGain, slidingData ->
            currencyCardUI(
                    target = target,
                    totalGain = totalGain.outputDouble(),
                    cardSlidingData = slidingData
            )
        }
    }

    private fun currencyCardUI(target: CurrencyType, totalGain: String, cardSlidingData: CardSlidingData): CardTemplateData {
        return CardTemplateData(sections = listOf(
                CardHeaderActionData(text = target.fullName, actionText = L10n.view_more, action = {
                    selectedCurrencyUseCase.setSelectedTarget(target)
                    visibilityManager.showPortfolio()
                }),
                CardDividerData(heightDp = 1, margin = 0),
                CardTitleTwoValueData(
                        title = L10n.gain_on_currency(target.fullName),
                        value = totalGain,
                        subValue = "+121.21%"
                ),
                cardSlidingData,
                CardDividerData(heightDp = 20, margin = 0, color = R.color.colorAccent)
        ))
    }

    private fun slidingPartOfCard(target: CurrencyType): Observable<CardSlidingData> {
        val priceObservable = exchangeRateUseCase.getCurrentExchangeRate(selectedCurrencyUseCase.selectedBase, target)
        val valueObservable = valueUseCase.getValue(target)

        val paidObservable = paidUseCase.getPaidForCurrentlyHeld(target)
        val realizedGainObservable = gainUseCase.getRealizedGain(target)

        return Observables.combineLatest(
                priceObservable,
                valueObservable,
                paidObservable,
                realizedGainObservable
        ) { price, value, paid, realizedGain ->

            CardSlidingData(data = listOf(
                    CardSlidingData.CardSlideItemData(title = L10n.price, value = price.outputDouble()),
                    CardSlidingData.CardSlideItemData(title = L10n.balance, value = value.outputDouble()),
                    CardSlidingData.CardSlideItemData(title = L10n.paid, value = paid.outputDouble()),
                    CardSlidingData.CardSlideItemData(title = L10n.realized_gain, value = realizedGain.outputDouble())
            ))
        }
    }

    override fun cleanUp() {

    }
}
