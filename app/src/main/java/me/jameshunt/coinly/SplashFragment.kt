package me.jameshunt.coinly

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.jameshunt.appbase.BaseFragment

class SplashFragment: BaseFragment() {

    // todo: load all of the data here, from cryptoCompare and integrations

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return layoutInflater.inflate(R.layout.fragment_splash, container,false)
    }
}