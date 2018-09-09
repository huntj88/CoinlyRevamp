package me.jameshunt.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import me.jameshunt.appbase.BaseFragment
import javax.inject.Inject

class MoreMenuFragment: BaseFragment() {

    @Inject
    lateinit var visibilityManager: MoreFragmentVisibilityManager

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        parentFragment!!.moreComponent().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val layout = LinearLayout(context)

        val textView = TextView(context)
        textView.text = "more menu"
        layout.addView(textView)

        val button = Button(context)
        button.setOnClickListener {
            visibilityManager.showMenu()
        }
        layout.addView(button)

        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}