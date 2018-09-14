package me.jameshunt.more

import io.reactivex.Observable
import me.jameshunt.appbase.template.*
import me.jameshunt.appbase.template.card.CardHeaderData
import me.jameshunt.appbase.template.card.CardTemplateData
import me.jameshunt.appbase.template.card.CardTextIconData
import me.jameshunt.appbase.template.card.CardTimeSelectData
import me.jameshunt.base.TimeType
import timber.log.Timber
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
                                CardHeaderData(text = L10n.additional_features),
                                CardTextIconData(text = L10n.integrations, icon = R.drawable.leak_canary_icon, action = {}),
                                CardTextIconData(text = L10n.news, icon = R.drawable.leak_canary_icon, action = {}),
                                CardTextIconData(text = L10n.security, icon = R.drawable.leak_canary_icon, action = {}),
                                CardTimeSelectData(
                                        selected = TimeType.HOUR,
                                        hour = { Timber.i("hour clicked") },
                                        day = { Timber.i("day clicked") },
                                        week = { Timber.i("week clicked") },
                                        month = { Timber.i("month clicked") },
                                        year = { Timber.i("year clicked") }
                                ),
                                CardTextIconData(text = L10n.notifications, icon = R.drawable.leak_canary_icon, action = {})
                        ))),
                        templateType = TemplateFactory.CARD
                ),
                TemplateObservableWrapper(
                        observable = Observable.just(CardTemplateData(sections = listOf(
                                CardHeaderData(text = L10n.get_in_touch),
                                CardTextIconData(text = L10n.give_feedback, icon = R.drawable.leak_canary_icon, action = {}),
                                CardTextIconData(text = L10n.write_a_review, icon = R.drawable.leak_canary_icon, action = {}),
                                CardTextIconData(text = L10n.report_a_bug, icon = R.drawable.leak_canary_icon, action = {}),
                                CardTextIconData(text = L10n.telegram, icon = R.drawable.leak_canary_icon, action = {}),
                                CardTextIconData(text = L10n.invite_a_friend, icon = R.drawable.leak_canary_icon, action = {})
                        ))),
                        templateType = TemplateFactory.CARD
                )
        ))
    }

}