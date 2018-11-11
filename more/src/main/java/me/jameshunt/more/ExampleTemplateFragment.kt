package me.jameshunt.more

import io.reactivex.Observable
import me.jameshunt.appbase.template.*
import me.jameshunt.appbase.template.card.*
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
                        observable = Observable.just(HeaderTemplateData(
                                title = "wow",
                                value = "a value",
                                subValue = "a sub value",
                                subViews = listOf(
                                        HeaderTemplateDataItem(title = "wow0", value = "0000"),
                                        HeaderTemplateDataItem(title = "wow1", value = "0100"),
                                        HeaderTemplateDataItem(title = "wow2", value = "0020"),
                                        HeaderTemplateDataItem(title = "wow3", value = "0003"),
                                        HeaderTemplateDataItem(title = "wow3", value = "0003"),
                                        HeaderTemplateDataItem(title = "wow3", value = "0003"),
                                        HeaderTemplateDataItem(title = "wow3", value = "0003")
                                )
                        )),
                        templateType = TemplateFactory.HEADER
                ),
                TemplateObservableWrapper(
                        observable = Observable.just(CardTemplateData(sections = listOf(
                                CardHeaderData(text = L10n.additional_features),
                                CardDividerData(heightDp = 1, margin = 0),
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
                                CardDividerData(heightDp = 20, margin = 0),
                                CardTextIconData(text = L10n.give_feedback, icon = R.drawable.leak_canary_icon, action = {}),
                                CardTextIconData(text = L10n.write_a_review, icon = R.drawable.leak_canary_icon, action = {}),
                                CardTitleTwoValueData(title = "hello", value = "sup dog", subValue = "Homie G"),
                                CardTextIconData(text = L10n.report_a_bug, icon = R.drawable.leak_canary_icon, action = {}),
                                CardTextIconData(text = L10n.telegram, icon = R.drawable.leak_canary_icon, action = {}),
                                CardSlidingData(data = listOf(
                                        CardSlidingData.CardSlideItemData(title = "sup0", value = "dog4"),
                                        CardSlidingData.CardSlideItemData(title = "sup1", value = "dog3"),
                                        CardSlidingData.CardSlideItemData(title = "sup2", value = "dog2"),
                                        CardSlidingData.CardSlideItemData(title = "sup3", value = "dog1"),
                                        CardSlidingData.CardSlideItemData(title = "sup4", value = "cat"),
                                        CardSlidingData.CardSlideItemData(title = "sup5", value = "catdfgd"),
                                        CardSlidingData.CardSlideItemData(title = "sup6", value = "catfdgdfg")
                                )),
                                CardTextIconData(text = L10n.invite_a_friend, icon = R.drawable.leak_canary_icon, action = {})
                        ))),
                        templateType = TemplateFactory.CARD
                )
        ))
    }

}