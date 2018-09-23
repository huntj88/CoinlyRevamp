package me.jameshunt.coinly

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_pager.*
import me.jameshunt.appbase.BaseFragment
import me.jameshunt.home.HomeFragment
import me.jameshunt.more.MoreFragment

class PagerFragment : BaseFragment() {

    private val pagerAdapter: PagerAdapter by lazy { PagerAdapter(childFragmentManager) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_pager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //subscribe to stuff here

        viewPager.adapter = pagerAdapter

        bottomNav.setOnNavigationItemSelectedListener { item ->

            var partOfPager = true

            when (item.itemId) {
                R.id.homeItem -> viewPager.currentItem = 0
                R.id.addTransactionItem -> {
                    //fragmentVisibilityManager.showAddTransactionFragment()
                    partOfPager = false
                }
                R.id.moreItem -> viewPager.currentItem = 1
            }

            partOfPager
        }

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                var realPosition = position

                if (position >= 1)
                    realPosition++

                bottomNav.menu.getItem(realPosition).isChecked = true
            }
        })
    }

    override fun cleanUp() {
        super.cleanUp()
        //unsubscribe here
    }

    fun onBackPressed(): Boolean {
        val currentFragment = (viewPager.adapter as PagerAdapter).getCurrentFragment(viewPager)

        val shouldClose: Boolean = when(currentFragment) {
            is HomeFragment -> currentFragment.onBackPressed()
            is MoreFragment -> currentFragment.onBackPressed()
            else -> throw NotImplementedError()
        }

        return shouldClose
    }

}

class PagerAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> MoreFragment()
            else -> throw NotImplementedError("wow, ryan wants me to add more screens to the view pager or something")
        }
    }

    fun getCurrentFragment(viewPager: ViewPager): Fragment? {
        return if (count == 0) null
        else instantiateItem(viewPager, viewPager.currentItem) as? BaseFragment
    }

    override fun getCount(): Int = 2

}