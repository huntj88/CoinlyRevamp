package me.jameshunt.home

import android.support.v4.app.FragmentManager
import me.jameshunt.appbase.BaseFragment
import me.jameshunt.appbase.FragmentID
import me.jameshunt.appbase.VisibilityManager
import javax.inject.Inject

@HomeScope
class HomeFragmentVisibilityManager @Inject constructor(private val fragmentManager: FragmentManager): VisibilityManager {

    private var currentPage: HomeFragmentID = HomeFragmentID.SUMMARY

    fun showCurrentPage() {
        when(currentPage) {
            HomeFragmentID.SUMMARY -> showFragmentRemoveOld(HomeFragmentID.SUMMARY, HomeFragmentID.PORTFOLIO, fragmentManager)
            HomeFragmentID.PORTFOLIO -> hideOldFragmentShowNewInstance(HomeFragmentID.SUMMARY, HomeFragmentID.PORTFOLIO, fragmentManager)
        }
    }

    fun showSummary() {
        when(currentPage) {
            HomeFragmentID.SUMMARY -> { /*don't do anything, already on page*/ }
            HomeFragmentID.PORTFOLIO -> showFragmentRemoveOld(HomeFragmentID.SUMMARY, HomeFragmentID.PORTFOLIO, fragmentManager)
        }

        currentPage = HomeFragmentID.SUMMARY
    }

    fun showPortfolio() {
        when (currentPage) {
            HomeFragmentID.SUMMARY -> hideOldFragmentShowNewInstance(HomeFragmentID.SUMMARY, HomeFragmentID.PORTFOLIO, fragmentManager)
            HomeFragmentID.PORTFOLIO -> { /*don't do anything, already on page*/ }
            else -> throw IllegalStateException("invalid navigation")
        }

        currentPage = HomeFragmentID.PORTFOLIO
    }
}

enum class HomeFragmentID : FragmentID {
    SUMMARY {
        override fun newInstance(): BaseFragment = SummaryFragment()
    },
    PORTFOLIO {
        override fun newInstance(): BaseFragment = PortfolioFragment()
    };
}