package me.jameshunt.home

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import me.jameshunt.appbase.template.*
import me.jameshunt.base.CurrencyType
import me.jameshunt.base.SelectedCurrencyUseCase
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
        private val currencySelect: CurrencySelectDialogManager
) : TemplateViewModel {

    override fun getAdapterData(): Observable<List<TemplateObservableWrapper>> {
        return selectedCurrencyUseCase.getSelectedTarget().map { getAdapterObservables(it) }
    }

    private fun getAdapterObservables(target: CurrencyType): List<TemplateObservableWrapper> {
        return listOf(
                TemplateObservableWrapper(Observable.just(
                        HeaderTemplateData(
                                title = L10n.gain_on_currency(target.fullName),
                                value = "$12,000",
                                subViews = listOf(
                                        HeaderTemplateDataItem(title = L10n.value, value = "$12,213.98"),
                                        HeaderTemplateDataItem(title = L10n.paid, value = "$9,214.74")
                                ),
                                subValue = "+24.12%"
                        )
                ), TemplateFactory.HEADER)
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

    fun showCurrencySelectDialog() {
        currencySelect.showDialog()
    }

    override fun cleanUp() {

    }
}