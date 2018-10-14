package me.jameshunt.business


import io.reactivex.Observable
import me.jameshunt.base.*
import javax.inject.Inject

class SortTransactionUseCase @Inject constructor(private val repository: Repository) {

    fun getSortedTransactions(currencyType: CurrencyType): Observable<DataSource<SortedTransactions>> {
        return repository.readTransactions(currencyType = currencyType).map {
            it.mapSuccess { transactions -> transactions.splitBySoldOrPurchased(currencyType = currencyType) }
        }
    }

    private fun List<Transaction>.splitBySoldOrPurchased(currencyType: CurrencyType): SortedTransactions {
        val purchased = mutableListOf<Transaction>()
        val sold = mutableListOf<Transaction>()

        this.forEach {
            if (it.fromCurrencyType == currencyType && it.toCurrencyType != currencyType) {
                sold.add(it)
            } else if (it.fromCurrencyType != currencyType && it.toCurrencyType == currencyType) {
                purchased.add(it)
            }
        }

        return SortedTransactions(
                purchased = purchased,
                sold = sold
        )
    }
}

data class SortedTransactions(
        val purchased: List<Transaction>,
        val sold: List<Transaction>
)