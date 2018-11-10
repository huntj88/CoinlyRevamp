package me.jameshunt.currencyselect

import android.support.v4.app.FragmentManager
import javax.inject.Inject

interface CurrencySelectDialogManager {
    fun showDialog()
}

class CurrencySelectDialogManagerImpl @Inject constructor(private val fragmentManager: FragmentManager) : CurrencySelectDialogManager {
    override fun showDialog() {
        CurrencySelectDialogFragment().show(fragmentManager, CurrencySelectDialogFragment::class.java.simpleName)
    }
}