package me.jameshunt.more

import io.reactivex.Observable
import me.jameshunt.appbase.template.*
import javax.inject.Inject

class ExampleTemplateFragment : TemplateFragment<ExampleViewModel>() {

    override fun inject() {
        parentFragment!!.moreComponent().inject(this)
    }
}

class ExampleViewModel @Inject constructor() : TemplateViewModel {
    override fun cleanUp() {

    }

    override fun getAdapterData(): Observable<List<TemplateObservableWrapper>> {
        return Observable.just(listOf(
                TemplateObservableWrapper(
                        observable = Observable.just(CardTemplateData(sections = listOf(
                                CardHeaderData(text = "woooow"),
                                CardTextIcon(text = "coinly is cool", icon = R.drawable.leak_canary_icon, action = {}),
                                CardTextIcon(text = "doggy", icon = R.drawable.leak_canary_icon, action = {}),
                                CardTextIcon(text = "sup", icon = R.drawable.leak_canary_icon, action = {})
                        ))),
                        templateType = TemplateFactory.CARD
                ),
                TemplateObservableWrapper(
                        observable = Observable.just(CardTemplateData(sections = listOf(
                                CardHeaderData(text = "woooow"),
                                CardHeaderData(text = "woooow"),
                                CardHeaderData(text = "woooow"),
                                CardHeaderData(text = "woooow")
                        ))),
                        templateType = TemplateFactory.CARD
                )
        ))
    }

}