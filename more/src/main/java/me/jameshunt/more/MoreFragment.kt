package me.jameshunt.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.jameshunt.appbase.BaseFragment
import me.jameshunt.appbase.activityComponent
import javax.inject.Inject

class MoreFragment : BaseFragment() {

    internal val moreComponent: MoreComponent by lazy {
        MoreComponent.create(activity!!.activityComponent(), childFragmentManager)
    }

    @Inject
    lateinit var moreFragmentVisibilityManager: MoreFragmentVisibilityManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_frame_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            moreComponent.inject(this)
            moreFragmentVisibilityManager.showMenu()
    }
}