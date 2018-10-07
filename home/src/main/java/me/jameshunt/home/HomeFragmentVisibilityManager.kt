package me.jameshunt.home

import android.support.v4.app.FragmentManager
import dagger.Binds
import dagger.Module
import me.jameshunt.appbase.BaseFragment
import me.jameshunt.appbase.FragmentID
import me.jameshunt.appbase.VisibilityManager
import javax.inject.Inject

interface HomeFragmentVisibilityManager: VisibilityManager {
    fun showCurrent()
    fun showSummary()
    fun showPortfolio()
    fun onBackPressed(): Boolean
}

@HomeScope
class HomeFragmentVisibilityManagerImpl @Inject constructor(private val fragmentManager: FragmentManager): HomeFragmentVisibilityManager {

    //todo consolidate all the visibility manager logic
    private val currentPage: HomeFragmentID
    get() {
        val visibleFragmentIDs = fragmentManager
                .fragments
                .asSequence()
                .filter { it.isVisible || !it.isHidden }
                .map { fragment ->
                    fragment.tag ?: run {
                        when(fragment) {
                            is SummaryFragment -> HomeFragmentID.SUMMARY.name
                            is PortfolioFragment -> HomeFragmentID.PORTFOLIO.name
                            else -> throw NotImplementedError()
                        }
                    }
                }
                .map { HomeFragmentID.valueOf(it) }
                .toList()

        if(visibleFragmentIDs.size > 1) throw IllegalStateException("multiple visible fragments")

        return visibleFragmentIDs.firstOrNull()?: HomeFragmentID.NONE
    }

    override fun showCurrent() {
        when(currentPage) {
            HomeFragmentID.NONE, HomeFragmentID.SUMMARY -> showSummary()
            HomeFragmentID.PORTFOLIO -> showPortfolio()
        }
    }

    override fun showSummary() {
        when(currentPage) {
            HomeFragmentID.NONE -> showFragmentRemoveOld(HomeFragmentID.SUMMARY, currentPage, fragmentManager)
            HomeFragmentID.SUMMARY -> { /*don't do anything, already on page*/ }
            HomeFragmentID.PORTFOLIO -> showFragmentHideFragment(HomeFragmentID.SUMMARY, currentPage, fragmentManager)
        }
    }

    override fun showPortfolio() {
        when (currentPage) {
            HomeFragmentID.SUMMARY -> showFragmentHideFragment(HomeFragmentID.PORTFOLIO, currentPage, fragmentManager)
            HomeFragmentID.PORTFOLIO -> { /*don't do anything, already on page*/ }
            else -> throw IllegalStateException("invalid navigation")
        }
    }

    override fun onBackPressed(): Boolean {
        val shouldClose = when (currentPage) {
            HomeFragmentID.SUMMARY, HomeFragmentID.NONE -> true
            HomeFragmentID.PORTFOLIO -> {
                showSummary()
                false
            }
        }
        return shouldClose
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

@Module
abstract class HomeFragmentVisibilityModule {

    @Binds
    abstract fun getVisibilityManager(homeFragmentVisibilityManagerImpl: HomeFragmentVisibilityManagerImpl): HomeFragmentVisibilityManager
}