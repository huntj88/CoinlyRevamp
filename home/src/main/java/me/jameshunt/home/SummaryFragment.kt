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
        private val currencyAmountUseCase: CurrencyAmountUseCase,
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
                        HeaderTemplateDataItem(title = L10n.net_profit, value = "0020"),
                        HeaderTemplateDataItem(title = L10n.sold, value = "0003")
                )
        ))
    }

    private fun getTargetCurrencyCard(target: CurrencyType): Observable<CardTemplateData> {

        val currencyAmountObservable = currencyAmountUseCase.getCurrencyAmount(currencyType = target)
        val priceObservable = exchangeRateUseCase.getCurrentExchangeRate(selectedCurrencyUseCase.selectedBase, target)

        val valueObservable = valueUseCase.getValue(target)

        val paidObservable = paidUseCase.getPaidForCurrentlyHeld(target)
        val gainObservable = gainUseCase.getUnrealizedGain(target)
        val realizedGainObservable = gainUseCase.getRealizedGain(target)

        return Observables.combineLatest(
                priceObservable,
                currencyAmountObservable,
                valueObservable,
                paidObservable,
                gainObservable,
                realizedGainObservable
        ) { price, currencyAmount, value, paid, gain, realizedGain ->
            currencyCardUI(
                    target = target,
                    price = price.mapSuccess { it.toString() }.output(),
                    currencyAmount = currencyAmount.mapSuccess { it.toString() }.output(),
                    value = value.mapSuccess { it.toString() }.output(),
                    paid = paid.mapSuccess { it.toString() }.output(),
                    gain = gain.mapSuccess { it.toString() }.output(),
                    realizedGain = realizedGain.mapSuccess { it.toString() }.output()
            )
        }
    }

    private fun currencyCardUI(
            target: CurrencyType,
            price: String,
            currencyAmount: String,
            value: String,
            paid: String,
            gain: String,
            realizedGain: String
    ): CardTemplateData {
        return CardTemplateData(sections = listOf(
                CardHeaderActionData(text = target.fullName, actionText = "view more", action = {
                    selectedCurrencyUseCase.setSelectedTarget(target)
                    visibilityManager.showPortfolio()
                }),
                CardDividerData(height = 1, margin = 0),
                CardTitleTwoValueData(
                        title = L10n.gain_on_currency(target.fullName),
                        value = "$2,512.42",
                        subValue = currencyAmount
                        //subValue = "+121.21%"
                ),
                CardSlidingData(data = listOf(
                        CardSlidingData.CardSlideItemData(title = L10n.price, value = price),
                        CardSlidingData.CardSlideItemData(title = L10n.balance, value = value),
                        CardSlidingData.CardSlideItemData(title = L10n.paid, value = paid),
                        CardSlidingData.CardSlideItemData(title = L10n.net_profit, value = gain),
                        CardSlidingData.CardSlideItemData(title = "realized gain", value = realizedGain),
                        CardSlidingData.CardSlideItemData(title = L10n.sold, value = "0003")
                )),
                CardDividerData(height = 20, margin = 0, color = R.color.colorAccent)
        ))
    }

    override fun cleanUp() {

    }
}
