package me.jameshunt.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.jameshunt.appbase.BaseFragment
import javax.inject.Inject

class HomeFragment : BaseFragment() {

    internal val homeComponent: HomeComponent by lazy {
        HomeComponent.create(activity!!.activityComponent(), childFragmentManager)
    }

    @Inject
    lateinit var visibilityManager: HomeFragmentVisibilityManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        homeComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_frame_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        visibilityManager.showCurrentPage()
    }
}