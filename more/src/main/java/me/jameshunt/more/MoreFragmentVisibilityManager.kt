package me.jameshunt.more

import android.support.v4.app.FragmentManager
import me.jameshunt.appbase.BaseFragment
import me.jameshunt.appbase.FragmentID
import javax.inject.Inject

@MoreScope
class MoreFragmentVisibilityManager @Inject constructor(private val fragmentManager: FragmentManager) {

    private var currentPage: MoreFragmentID = MoreFragmentID.MENU

    fun showMenu() {
        val menuFragment: MoreMenuFragment? = fragmentManager.findFragmentByTag(MoreFragmentID.MENU.name) as MoreMenuFragment?
        val ft = fragmentManager.beginTransaction()

        fragmentManager
                .fragments
                .filter { menuFragment != it }
                .forEach { ft.remove(it) }

        menuFragment
                ?.let { if (!it.isVisible) ft.show(it) }
                ?: ft.add(R.id.fragmentFrameLayout, MoreFragmentID.MENU.newInstance(), MoreFragmentID.MENU.name)

        ft.commit()

        currentPage = MoreFragmentID.MENU
    }

    fun showExampleTemplate() {
        val exampleFragment: ExampleTemplateFragment? = fragmentManager.findFragmentByTag(MoreFragmentID.EXAMPLE.name) as ExampleTemplateFragment?
        val ft = fragmentManager.beginTransaction()

        fragmentManager
                .fragments
                .filter { exampleFragment != it }
                .forEach { ft.hide(it) }

        exampleFragment
                ?.let { if (!it.isVisible) ft.show(it) }
                ?: ft.add(R.id.fragmentFrameLayout, MoreFragmentID.EXAMPLE.newInstance(), MoreFragmentID.EXAMPLE.name)

        ft.commit()

        currentPage = MoreFragmentID.EXAMPLE
    }

}

enum class MoreFragmentID : FragmentID {
    MENU {
        override fun newInstance(): BaseFragment = MoreMenuFragment()
    },
    EXAMPLE {
        override fun newInstance(): BaseFragment = ExampleTemplateFragment()
    }
}