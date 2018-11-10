package me.jameshunt.currencyselect

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import io.reactivex.Single
import io.reactivex.rxkotlin.Singles
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.dialog_fragment_coin_select.*
import me.jameshunt.appbase.BaseDialogFragment
import me.jameshunt.appbase.activityComponent
import me.jameshunt.base.CurrencyType
import me.jameshunt.base.SelectedCurrencyUseCase
import me.jameshunt.business.EnabledCurrencyUseCase
import timber.log.Timber
import javax.inject.Inject

class CurrencySelectDialogFragment : BaseDialogFragment() {

    @Inject
    lateinit var viewModel: CurrencySelectViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CurrencySelectComponent.create(activity!!.activityComponent()).inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return inflater.inflate(R.layout.dialog_fragment_coin_select, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        this.viewModel.getCurrencyToShow().subscribeBy(
                onSuccess = { createCoinViews(it) },
                onError = { Timber.e(it) }
        )
    }

    private fun createCoinViews(selectData: CurrencySelectData) {

        val viewFactory = CurrencySelectViewFactory(layoutInflater, enabledCoinsScrollView)

        selectData.enabled.map { currencyType ->
            viewFactory.inflate(currencyType = currencyType, isSelected = selectData.selected == currencyType, selectCoin = {
                viewModel.setSelectedCurrency(currencyType)
                dismiss()
            })
        }.forEach {
            enabledCoinsScrollView.addView(it)
        }
    }
}

class CurrencySelectViewModel @Inject constructor(
        private val selectedCurrencyUseCase: SelectedCurrencyUseCase,
        private val enabledCurrencyUseCase: EnabledCurrencyUseCase
) {

    fun getCurrencyToShow(): Single<CurrencySelectData> {

        val enabled = enabledCurrencyUseCase.getEnabledCurrencies().firstOrError()
        val selected = selectedCurrencyUseCase.getSelectedTarget().firstOrError()

        return Singles.zip(selected, enabled).map { (selected, enabled) ->
            CurrencySelectData(selected = selected, enabled = enabled)
        }
    }

    fun setSelectedCurrency(currencyType: CurrencyType) {
        this.selectedCurrencyUseCase.setSelectedTarget(currencyType)
    }
}

data class CurrencySelectData(val selected: CurrencyType, val enabled: Set<CurrencyType>)

private class CurrencySelectViewFactory(
        private val layoutInflater: LayoutInflater,
        private val parent: ViewGroup) {

    fun inflate(currencyType: CurrencyType, isSelected: Boolean, selectCoin: () -> Unit): View {
        val coinSelectView = layoutInflater.inflate(R.layout.dialog_fragment_coin_option, parent, false)
        coinSelectView?.findViewById<ImageView>(R.id.currencyIconImageView)?.setImageResource(this.getImageId(currencyType))
        coinSelectView?.findViewById<TextView>(R.id.currencyNameTextView)?.text = currencyType.fullName
        coinSelectView?.findViewById<RadioButton>(R.id.currencySelectRadioButton)?.isChecked = isSelected
        coinSelectView?.findViewById<RadioButton>(R.id.currencySelectRadioButton)?.setOnClickListener {
            selectCoin()
        }

        return coinSelectView
    }

    private fun getImageId(currencyType: CurrencyType): Int {
        return when (currencyType) {
            CurrencyType.BTC -> R.drawable.ic_bitcoin
            CurrencyType.ETH -> R.drawable.ic_ethereum
            CurrencyType.LTC -> R.drawable.ic_litecoin
            CurrencyType.BCH -> R.drawable.ic_bitcoin_cash
            else -> throw NotImplementedError()
        }
    }
}
