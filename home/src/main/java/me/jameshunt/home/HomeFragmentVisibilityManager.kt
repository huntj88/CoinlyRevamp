package me.jameshunt.home

import android.support.v4.app.FragmentManager
import me.jameshunt.appbase.BaseFragment
import me.jameshunt.appbase.FragmentID
import me.jameshunt.appbase.VisibilityManager
import javax.inject.Inject

@HomeScope
class HomeFragmentVisibilityManager @Inject constructor(private val fragmentManager: FragmentManager): VisibilityManager {

    private val currentPage: HomeFragmentID
    get() {
        val visibleFragmentIDs = fragmentManager.fragments.filter { it.isVisible }.map { HomeFragmentID.valueOf(it.tag!!) }
        if(visibleFragmentIDs.size > 1) throw IllegalStateException("multiple visible fragments")

        return visibleFragmentIDs.firstOrNull()?: HomeFragmentID.NONE
    }

    fun showCurrentPage() {
        when(currentPage) {
            HomeFragmentID.NONE, HomeFragmentID.SUMMARY -> showSummary()
            HomeFragmentID.PORTFOLIO -> showPortfolio()
        }
    }

    fun showSummary() {
        when(currentPage) {
            HomeFragmentID.NONE -> showFragmentRemoveOld(HomeFragmentID.SUMMARY, currentPage, fragmentManager)
            HomeFragmentID.SUMMARY -> { /*don't do anything, already on page*/ }
            HomeFragmentID.PORTFOLIO -> showFragmentRemoveOld(HomeFragmentID.SUMMARY, currentPage, fragmentManager)
        }
    }

    fun showPortfolio() {
        when (currentPage) {
            HomeFragmentID.SUMMARY -> hideOldFragmentShowNewInstance(currentPage, HomeFragmentID.PORTFOLIO, fragmentManager)
            HomeFragmentID.PORTFOLIO -> { /*don't do anything, already on page*/ }
            else -> throw IllegalStateException("invalid navigation")
        }
    }
}

enum class HomeFragmentID : FragmentID {
    NONE {
        override fun newInstance(): BaseFragment = throw IllegalStateException("cant instantiate none fragment")
    },
    SUMMARY {
        override fun newInstance(): BaseFragment = SummaryFragment()
    },
    PORTFOLIO {
        override fun newInstance(): BaseFragment = PortfolioFragment()
    };
}