package me.jameshunt.appbase

import android.support.v4.app.FragmentManager

interface VisibilityManager {

    fun removeOldFragmentReplaceWith(fragmentID: FragmentID, fragmentManager: FragmentManager) {
        val fragment: BaseFragment? = fragmentManager.findFragmentByTag(fragmentID.name) as BaseFragment?

        if (fragment == null) {
            val ft = fragmentManager.beginTransaction()
            ft.replace(R.id.fragmentFrameLayout, fragmentID.newInstance(), fragmentID.name)
            ft.commit()
        }
    }

    fun hideOldFragmentShowNewInstance(oldFragmentID: FragmentID, fragmentID: FragmentID, fragmentManager: FragmentManager) {
        val oldFragment: BaseFragment? = fragmentManager.findFragmentByTag(oldFragmentID.name) as BaseFragment?

        val ft = fragmentManager.beginTransaction()

        oldFragment?.let { ft.hide(it) }
        ft.add(R.id.fragmentFrameLayout, fragmentID.newInstance(), fragmentID.name)

        ft.commit()
    }

    fun showFragmentRemoveOld(fragmentID: FragmentID, oldFragmentID: FragmentID, fragmentManager: FragmentManager) {
        val fragment: BaseFragment? = fragmentManager.findFragmentByTag(fragmentID.name) as BaseFragment?

        val ft = fragmentManager.beginTransaction()

        when (fragment != null) {
            true -> ft.show(fragment)
            false -> ft.add(R.id.fragmentFrameLayout, fragmentID.newInstance(), fragmentID.name)
        }

        val oldFragment: BaseFragment? = fragmentManager.findFragmentByTag(oldFragmentID.name) as BaseFragment?
        oldFragment?.let { ft.remove(oldFragment) }

        ft.commit()
    }
}

interface FragmentID {
    fun newInstance(): BaseFragment
    val name: String
}