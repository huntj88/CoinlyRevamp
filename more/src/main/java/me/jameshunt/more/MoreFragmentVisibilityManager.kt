package me.jameshunt.more

import android.support.v4.app.FragmentManager
import dagger.Binds
import dagger.Module
import me.jameshunt.appbase.BaseFragment
import me.jameshunt.appbase.FragmentID
import me.jameshunt.appbase.VisibilityManager
import javax.inject.Inject

interface MoreFragmentVisibilityManager: VisibilityManager {
    fun showCurrent()
    fun showMenu()
    fun showExampleTemplate()
    fun onBackPressed(): Boolean
}

@MoreScope
class MoreFragmentVisibilityManagerImpl @Inject constructor(private val fragmentManager: FragmentManager) : MoreFragmentVisibilityManager {

    //todo consolidate all the visibility manager logic
    private val currentPage: MoreFragmentID
        get() {
            val visibleFragmentIDs = fragmentManager
                    .fragments
                    .asSequence()
                    .filter { it.isVisible || !it.isHidden }
                    .map { fragment ->
                        fragment.tag ?: run {
                            when(fragment) {
                                is MoreMenuFragment -> MoreFragmentID.MENU.name
                                is ExampleTemplateFragment -> MoreFragmentID.EXAMPLE.name
                                else -> throw NotImplementedError()
                            }
                        }
                    }
                    .map { MoreFragmentID.valueOf(it) }
                    .toList()

            if(visibleFragmentIDs.size > 1) throw IllegalStateException("multiple visible fragments")

            return visibleFragmentIDs.firstOrNull()?: MoreFragmentID.NONE
        }

    override fun showCurrent() {
        when(currentPage) {
            MoreFragmentID.NONE, MoreFragmentID.MENU -> showMenu()
            MoreFragmentID.EXAMPLE -> showExampleTemplate()
        }
    }

    override fun showMenu() {

        when(currentPage) {
            MoreFragmentID.NONE -> showFragmentRemoveOld(MoreFragmentID.MENU, currentPage, fragmentManager)
            MoreFragmentID.MENU -> { /*don't do anything, already on page*/ }
            MoreFragmentID.EXAMPLE -> showFragmentRemoveOld(MoreFragmentID.MENU, currentPage, fragmentManager)
        }
    }

    override fun showExampleTemplate() {
        when (currentPage) {
            MoreFragmentID.MENU -> hideOldFragmentShowNewInstance(currentPage, MoreFragmentID.EXAMPLE, fragmentManager)
            MoreFragmentID.EXAMPLE -> { /*don't do anything, already on page*/ }
            else -> throw IllegalStateException("invalid navigation")
        }
    }

    override fun onBackPressed(): Boolean {
        val shouldClose = when (currentPage) {
            MoreFragmentID.MENU, MoreFragmentID.NONE -> true
            MoreFragmentID.EXAMPLE -> {
                showMenu()
                false
            }
        }
        return shouldClose
    }
}

enum class MoreFragmentID : FragmentID {
    NONE {
        override fun newInstance(): BaseFragment = throw IllegalStateException("cant instantiate none fragment")
    },
    MENU {
        override fun newInstance(): BaseFragment = MoreMenuFragment()
    },
    EXAMPLE {
        override fun newInstance(): BaseFragment = ExampleTemplateFragment()
    }
}

@Module
abstract class MoreFragmentVisibilityModule {

    @Binds
    abstract fun getVisibilityManager(moreFragmentVisibilityManagerImpl: MoreFragmentVisibilityManagerImpl): MoreFragmentVisibilityManager
}