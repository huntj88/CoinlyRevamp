package me.jameshunt.business

import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import me.jameshunt.base.ActivityScope
import me.jameshunt.base.SelectedTimeTypeUseCase
import me.jameshunt.base.TimeType
import javax.inject.Inject

@ActivityScope
class SelectedTimeTypeUseCaseImpl @Inject constructor() : SelectedTimeTypeUseCase {

    private lateinit var emitter: ObservableEmitter<TimeType>
    private val observable = Observable
            .create<TimeType> {
                emitter = it
                emitter.onNext(TimeType.DAY)
            }
            .distinctUntilChanged()
            .replay(1)
            .autoConnect()

    override fun getSelectedTimeType(): Observable<TimeType> = observable

    override fun setSelectedTimeType(timeType: TimeType) {
        emitter.onNext(timeType)
    }

    init {
        println(getSelectedTimeType().blockingFirst())
    }
}