package me.jameshunt.coinly

import android.support.v4.app.FragmentManager
import me.jameshunt.appbase.BaseFragment
import me.jameshunt.appbase.FragmentID
import me.jameshunt.appbase.VisibilityManager

class MainVisibilityManager(private val fragmentManager: FragmentManager) : VisibilityManager {

    //todo consolidate all the visibility manager logic
    private val currentPage: MainFragmentID
        get() {
            val visibleFragmentIDs = fragmentManager
                    .fragments
                    .asSequence()
                    .filter { it.isVisible || !it.isHidden }
                    .map { fragment ->
                        fragment.tag ?: run {
                            when(fragment) {
                                is SplashFragment -> MainFragmentID.SPLASH.name
                                is PagerFragment -> MainFragmentID.PAGER.name
                                else -> throw NotImplementedError()
                            }
                        }
                    }
                    .map { MainFragmentID.valueOf(it) }
                    .toList()

            if (visibleFragmentIDs.size > 1) throw IllegalStateException("multiple visible fragments")

            return visibleFragmentIDs.firstOrNull() ?: MainFragmentID.NONE
        }

    fun showCurrent() {
        when (currentPage) {
            MainFragmentID.NONE, MainFragmentID.SPLASH -> showSplash()
            MainFragmentID.PAGER -> showPager()
        }
    }

    fun showSplash() {
        when (currentPage) {
            MainFragmentID.NONE -> showFragmentRemoveOld(MainFragmentID.SPLASH, currentPage, fragmentManager)
            MainFragmentID.SPLASH -> { /*don't do anything, already on page*/
            }
            MainFragmentID.PAGER -> throw IllegalStateException("should not go from pager to splash")
        }
    }

    fun showPager() {
        when (currentPage) {
            MainFragmentID.SPLASH -> showFragmentRemoveOld(MainFragmentID.PAGER, currentPage, fragmentManager)
            MainFragmentID.PAGER -> { /*don't do anything, already on page*/
            }
            else -> throw IllegalStateException("invalid navigation")
        }
    }

    fun onBackPressed(): Boolean {
        val shouldClose = when (currentPage) {
            MainFragmentID.SPLASH, MainFragmentID.NONE -> true
            MainFragmentID.PAGER -> {
                val fragment = fragmentManager.findFragmentByTag(MainFragmentID.PAGER.name) as PagerFragment
                fragment.onBackPressed()
            }
        }

        return shouldClose
    }
}

enum class MainFragmentID : FragmentID {
    NONE {
        override fun newInstance(): BaseFragment = throw IllegalStateException("cant instantiate none fragment")
    },
    SPLASH {
        override fun newInstance(): BaseFragment = SplashFragment()
    },
    PAGER {
        override fun newInstance(): BaseFragment = PagerFragment()
    }
}