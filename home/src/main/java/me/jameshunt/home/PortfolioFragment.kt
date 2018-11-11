package me.jameshunt.home

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import me.jameshunt.appbase.template.*
import me.jameshunt.appbase.template.card.*
import me.jameshunt.base.*
import me.jameshunt.business.TimeTypePricesUseCase
import me.jameshunt.currencyselect.CurrencySelectDialogManager
import timber.log.Timber
import javax.inject.Inject

class PortfolioFragment : TemplateFragment<PortfolioViewModel>() {

    private var toolbarDisposable: Disposable? = null

    override val layoutId: Int = R.layout.fragment_toolbar_template

    override fun inject() {
        parentFragment!!.homeComponent().inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.toolbarDisposable = this.viewModel.getToolbarData().subscribeBy(
                onNext = { toolbarData ->
                    view.findViewById<ImageView>(R.id.backButton).setOnClickListener { toolbarData.back() }
                    view.findViewById<TextView>(R.id.dropDownTitle).apply {
                        text = toolbarData.title
                        setOnClickListener { viewModel.showCurrencySelectDialog() }
                    }
                },
                onError = { Timber.e(it) },
                onComplete = { Timber.i("toolbar data complete") }
        )
    }

    override fun cleanUp() {
        this.toolbarDisposable?.dispose()
        this.viewModel.cleanUp()
    }
}

class PortfolioViewModel @Inject constructor(
        private val visibilityManager: HomeFragmentVisibilityManager,
        private val selectedCurrencyUseCase: SelectedCurrencyUseCase,
        private val selectedTimeTypeUseCase: SelectedTimeTypeUseCase,
        private val timeTypePricesUseCase: TimeTypePricesUseCase,
        private val currencySelect: CurrencySelectDialogManager
) : TemplateViewModel {

    override fun getAdapterData(): Observable<List<TemplateObservableWrapper>> {
        return selectedCurrencyUseCase.getSelectedTarget().map { getAdapterObservables(it) }
    }

    private fun getAdapterObservables(target: CurrencyType): List<TemplateObservableWrapper> {
        return listOf(
                TemplateObservableWrapper(getHeader(target), TemplateFactory.HEADER),
                TemplateObservableWrapper(getLineChart(target), TemplateFactory.CARD)
        )
    }

    fun getToolbarData(): Observable<ToolbarTemplateData> {
        return selectedCurrencyUseCase.getSelectedTarget().map {
            ToolbarTemplateData(
                    back = { visibilityManager.onBackPressed() },
                    title = it.fullName,
                    dropDownAction = { Timber.i("drop down clicked") },
                    addTransaction = { Timber.i("add transaction clicked") }
            )
        }
    }

    private fun getHeader(currencyType: CurrencyType): Observable<HeaderTemplateData> {

        //todo: updates to data

        return Observable.just(
                HeaderTemplateData(
                        title = L10n.gain_on_currency(currencyType.fullName),
                        value = "$12,000",
                        subViews = listOf(
                                HeaderTemplateDataItem(title = L10n.value, value = "$12,213.98"),
                                HeaderTemplateDataItem(title = L10n.paid, value = "$9,214.74")
                        ),
                        subValue = "+24.12%"
                )
        )
    }

    private fun getLineChart(currencyType: CurrencyType): Observable<CardTemplateData> {
        return selectedTimeTypeUseCase.getSelectedTimeType().flatMap { timeType ->

            timeTypePricesUseCase.getTimeTypePrices(currencyType, timeType).map { timePrices ->

                val points = timePrices
                        .map {
                            CardLineChartData.Point(
                                    x = it.time / 1000000.0f,
                                    y = 1 / it.price.toFloat()
                            )
                        }

                CardTemplateData(listOf(
                        CardTitleTwoValueData(title = L10n.currency_price(currencyType.fullName), value = "blah", subValue = "23%"),
                        CardDividerData(heightDp = 1, margin = 24),
                        CardLineChartData(
                                points = points,
                                timeType = timeType
                        ),
                        CardTimeSelectData(
                                selected = timeType,
                                hour = { selectedTimeTypeUseCase.setSelectedTimeType(TimeType.HOUR) },
                                day = { selectedTimeTypeUseCase.setSelectedTimeType(TimeType.DAY) },
                                week = { selectedTimeTypeUseCase.setSelectedTimeType(TimeType.WEEK) },
                                month = { selectedTimeTypeUseCase.setSelectedTimeType(TimeType.MONTH) },
                                year = { selectedTimeTypeUseCase.setSelectedTimeType(TimeType.YEAR) }
                        ),
                        CardDividerData(heightDp = 15, margin = 0, color = R.color.colorAccent),
                        CardDividerData(heightDp = 1, margin = 24),
                        CardDividerData(heightDp = 10, margin = 0, color = R.color.colorAccent),
                        CardSlidingData(listOf(
                                CardSlidingData.CardSlideItemData("blah", "290"),
                                CardSlidingData.CardSlideItemData("wow", "4302"),
                                CardSlidingData.CardSlideItemData("sup", "12")
                        )),
                        CardDividerData(heightDp = 20, margin = 0, color = R.color.colorAccent)
                ))
            }
        }
    }

    fun showCurrencySelectDialog() {
        currencySelect.showDialog()
    }

    override fun cleanUp() {

    }
}