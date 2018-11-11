package me.jameshunt.appbase.template

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_template.*
import me.jameshunt.appbase.BaseFragment
import me.jameshunt.appbase.R
import timber.log.Timber
import javax.inject.Inject

abstract class TemplateFragment<ViewModel : TemplateViewModel> : BaseFragment() {

    @Inject
    lateinit var viewModel: ViewModel

    private var templateFactory: TemplateFactory = TemplateFactory()

    private var adapter: TemplateAdapter? = null

    private var disposable: Disposable? = null

    open val layoutId = R.layout.fragment_template

    abstract fun inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutId, container, false)
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        inject()

        templateRecyclerView.apply {
            this.layoutManager = LinearLayoutManager(this.context)
            this.layoutAnimation = AnimationUtils.loadLayoutAnimation(this.context, R.anim.layout_animation_fall_down)
        }

        this.disposable = this.viewModel.getAdapterData().subscribeBy(
                onNext = {
                    this.adapter?.cleanUp()
                    this.adapter = TemplateAdapter(it, this.templateFactory)
                    templateRecyclerView.adapter = this.adapter
                    templateRecyclerView.scheduleLayoutAnimation()
                },
                onError = { Timber.e(it) },
                onComplete = { Timber.i("template fragment adapter observable completed") }
        )
    }

    override fun cleanUp() {
        viewModel.cleanUp()
        disposable?.dispose()
        adapter?.cleanUp()
    }
}