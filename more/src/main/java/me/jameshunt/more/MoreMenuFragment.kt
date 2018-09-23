package me.jameshunt.more

import io.reactivex.Observable
import me.jameshunt.appbase.template.*
import me.jameshunt.appbase.template.card.*
import javax.inject.Inject

class MoreMenuFragment : TemplateFragment<MoreMenuViewModel>() {

    override fun inject() {
        parentFragment!!.moreComponent().inject(this)
    }
}

class MoreMenuViewModel @Inject constructor(private val moreFragmentVisibilityManager: MoreFragmentVisibilityManager) : TemplateViewModel {
    override fun getAdapterData(): Observable<List<TemplateObservableWrapper>> {
        return Observable.just(listOf(
                TemplateObservableWrapper(
                        observable = Observable.just(CardTemplateData(sections = listOf(
                                CardHeaderData(text = L10n.additional_features),
                                CardDividerData(height = 1, margin = 0),
                                CardTextIconData(text = L10n.integrations, icon = R.drawable.leak_canary_icon, action = {
                                    moreFragmentVisibilityManager.showIntegrations()
                                }),
                                CardTextIconData(text = L10n.news, icon = R.drawable.leak_canary_icon, action = {}),
                                CardTextIconData(text = L10n.security, icon = R.drawable.leak_canary_icon, action = {}),
                                CardTextIconData(text = L10n.notifications, icon = R.drawable.leak_canary_icon, action = {
                                    moreFragmentVisibilityManager.showExampleTemplate()
                                })
                        ))),
                        templateType = TemplateFactory.CARD
                ),
                TemplateObservableWrapper(
                        observable = Observable.just(CardTemplateData(sections = listOf(
                                CardHeaderData(text = L10n.get_in_touch),
                                CardDividerData(height = 1, margin = 0),
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

    override fun cleanUp() {

    }
}
