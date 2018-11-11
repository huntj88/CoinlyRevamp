package me.jameshunt.business

import io.reactivex.Observable
import me.jameshunt.base.*
import javax.inject.Inject

class TimeTypePricesUseCase @Inject constructor(
        private val repository: Repository,
        private val selectedCurrencyUseCase: SelectedCurrencyUseCase
) {

    fun getTimeTypePrices(target: CurrencyType, timeType: TimeType): Observable<List<TimePrice>> {
        return repository.readTimePricesForTimeType(selectedCurrencyUseCase.selectedBase, target, timeType)
                .map { timePrices -> timePrices.sortedBy { it.time } }
    }
}