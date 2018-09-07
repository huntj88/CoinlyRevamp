package me.jameshunt.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import me.jameshunt.appbase.BaseFragment
import javax.inject.Inject

class PortfolioFragment : BaseFragment() {

    @Inject
    lateinit var visibilityManager: HomeFragmentVisibilityManager

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        parentFragment!!.homeComponent().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val layout = LinearLayout(context)

        val textView = TextView(context)
        textView.text = "portfolio"
        layout.addView(textView)

        val button = Button(context)
        button.setOnClickListener {
            visibilityManager.showSummary()
        }
        layout.addView(button)

        return layout
    }
}