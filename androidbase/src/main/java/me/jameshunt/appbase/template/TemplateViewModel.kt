package me.jameshunt.appbase.template

import io.reactivex.Observable

interface TemplateViewModel {
    fun cleanUp()
    fun getAdapterData(): Observable<List<TemplateObservableWrapper>>
}