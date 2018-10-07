package me.jameshunt.home

import io.reactivex.Observable
import me.jameshunt.appbase.template.*
import me.jameshunt.base.CurrencyType
import me.jameshunt.business.SelectedCurrencyUseCase
import timber.log.Timber
import javax.inject.Inject

class PortfolioFragment : TemplateFragment<PortfolioViewModel>() {
    override fun inject() {
        parentFragment!!.homeComponent().inject(this)
    }
}

class PortfolioViewModel @Inject constructor(
        private val visibilityManager: HomeFragmentVisibilityManager,
        private val selectedCurrencyUseCase: SelectedCurrencyUseCase
) : TemplateViewModel {

    override fun getAdapterData(): Observable<List<TemplateObservableWrapper>> {
        return selectedCurrencyUseCase.getSelectedTarget().map { getAdapterObservables(it) }
    }

    private fun getAdapterObservables(target: CurrencyType): List<TemplateObservableWrapper> {
        return mutableListOf(
                TemplateObservableWrapper(getToolbar(target), TemplateFactory.TOOLBAR)
//                TemplateObservableWrapper(getHeader(), TemplateFactory.HEADER)
        )
    }

    private fun getToolbar(target: CurrencyType): Observable<ToolbarTemplateData> {
        return Observable.just(ToolbarTemplateData(
                back = { visibilityManager.onBackPressed() },
                title = target.fullName,
                dropDownAction = { Timber.i("drop down clicked") },
                addTransaction = { Timber.i("add transaction clicked") }
        ))
    }

    override fun cleanUp() {

    }
}