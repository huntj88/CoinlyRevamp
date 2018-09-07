package me.jameshunt.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import me.jameshunt.appbase.BaseFragment
import javax.inject.Inject

class SummaryFragment : BaseFragment() {

    @Inject
    lateinit var visibilityManager: HomeFragmentVisibilityManager

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        parentFragment!!.homeComponent().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val button = Button(context)
        button.setOnClickListener {
            visibilityManager.showPortfolio()
        }
        return button
    }
}