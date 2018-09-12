package me.jameshunt.appbase.template

import android.support.v7.widget.RecyclerView
import android.view.View
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

abstract class BaseTemplate<TemplateData: BaseTemplateData>(view: View) : RecyclerView.ViewHolder(view) {

    var disposable: Disposable? = null

    abstract fun observeData(observableData: Observable<TemplateData>)
}

interface BaseTemplateData
