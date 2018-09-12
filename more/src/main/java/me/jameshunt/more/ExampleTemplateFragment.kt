package me.jameshunt.more

import io.reactivex.Observable
import me.jameshunt.appbase.template.*
import javax.inject.Inject

class ExampleTemplateFragment: TemplateFragment<ExampleViewModel>() {

    override fun inject() {
        parentFragment!!.moreComponent().inject(this)
    }
}

class ExampleViewModel @Inject constructor(): TemplateViewModel {
    override fun cleanUp() {

    }

    override fun getAdapterData(): Observable<List<TemplateObservableWrapper>> {
        return Observable.just(listOf(
                TemplateObservableWrapper(
                        observable = Observable.just(CardTemplateData(string = "wow a string")),
                        templateType = TemplateFactory.CARD
                )
        ))
    }

}