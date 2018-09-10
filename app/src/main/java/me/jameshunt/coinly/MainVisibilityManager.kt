package me.jameshunt.coinly

import android.support.v4.app.FragmentManager
import me.jameshunt.appbase.BaseFragment
import me.jameshunt.appbase.FragmentID

class MainVisibilityManager(private val fragmentManager: FragmentManager) {

//    fun showCurrent() {
//
//        val visibleFragmentIDs = fragmentManager.fragments.filter { it.isVisible }.map { MainFragmentID.valueOf(it.tag!!) }
//        if(visibleFragmentIDs.size > 1) throw IllegalStateException("multiple visible fragments")
//
//        visibleFragmentIDs.firstOrNull()?.let {
//            when(it) {
//                MainFragmentID.SPLASH -> showSplash()
//                MainFragmentID.PAGER -> showCurrent()
//            }
//        }
//    }

    fun showSplash() {
        val fragment = fragmentManager.findFragmentByTag(MainFragmentID.SPLASH.name) as BaseFragment?
        showFragment(fragment, MainFragmentID.SPLASH)
    }

    fun showPager() {
        val fragment = fragmentManager.findFragmentByTag(MainFragmentID.PAGER.name) as BaseFragment?
        showFragment(fragment, MainFragmentID.PAGER)
    }

    private fun showFragment(fragment: BaseFragment?, fragmentID: MainFragmentID) {
        fragment?.let {
            val fragmentVisible = it.isVisible

            if (!fragmentVisible) {
                fragmentManager
                        .beginTransaction()
                        .show(fragment)
                        .commit()
            }
        } ?: let {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, fragmentID.newInstance(), fragmentID.name)
                    .commit()
        }
    }
}

enum class MainFragmentID : FragmentID {
    SPLASH {
        override fun newInstance(): BaseFragment = SplashFragment()
    },
    PAGER {
        override fun newInstance(): BaseFragment = PagerFragment()
    }
}