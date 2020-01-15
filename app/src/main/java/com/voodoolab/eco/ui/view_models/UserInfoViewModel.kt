package com.voodoolab.eco.ui.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.voodoolab.eco.models.ClearUserModel
import com.voodoolab.eco.network.DataState
import com.voodoolab.eco.repositories.UserRepo
import com.voodoolab.eco.responses.UpdateNameResponse
import com.voodoolab.eco.responses.UserInfoResponse
import com.voodoolab.eco.states.user_state.UserStateEvent
import com.voodoolab.eco.states.user_state.UserViewState
import com.voodoolab.eco.utils.AbsentLiveData
import java.lang.ArithmeticException

class UserInfoViewModel : ViewModel() {

    private val _stateEvent: MutableLiveData<UserStateEvent> = MutableLiveData()
    private val _viewState: MutableLiveData<UserViewState> = MutableLiveData()

    val viewState: LiveData<UserViewState>
        get() = _viewState

    val dataState: LiveData<DataState<UserViewState>> = Transformations
        .switchMap(_stateEvent) {stateEvent ->
            stateEvent?.let {
                handleStateEvent(it)
            }
        }

    fun handleStateEvent(stateEvent: UserStateEvent): LiveData<DataState<UserViewState>> {
        return when (stateEvent) {
            is UserStateEvent.RequestUserInfo -> {
                UserRepo.getUserInfo(stateEvent.token)
            }
            is UserStateEvent.SetNewNameEvent -> {
                UserRepo.updateUserName(stateEvent.token, stateEvent.name)
            }
            is UserStateEvent.None -> {
                AbsentLiveData.create()
            }
        }
    }

    fun updateUserResponse(userResponse: UserInfoResponse?, updateNameResponse: UpdateNameResponse?) {
        val update = getCurentViewStateOrNew()
        update.clearResponse = convertDataFromRawDataToPresentData(userResponse)
        update.userResponse = userResponse
        update.updateNameResponse = updateNameResponse
        _viewState.value = update
    }

    fun getCurentViewStateOrNew(): UserViewState {
        val value = viewState.value?.let {
            it
        }?: UserViewState()
        return value
    }

    private fun convertDataFromRawDataToPresentData(userResponse: UserInfoResponse?): ClearUserModel {
        val moneyValues = ArrayList<Int>()
        val percentValues = ArrayList<Int>()
        userResponse?.month_cash_back?.forEach {
            it.value?.let {money ->
                moneyValues.add(money.div(100))
            }

            it.percent?.let {percent ->
                percentValues.add(percent)
            }
        }

        var currentProgress: Float? = null
        val rangeList: ArrayList<IntRange> = ArrayList()

        userResponse?.month_cash_back?.let { cashBacks ->
            if (cashBacks.size == 5) {
                cashBacks.withIndex().forEach { wrappedItem ->
                    if (wrappedItem.index == 0) {
                        wrappedItem.value.value?.let { endOfRange ->
                            rangeList.add(
                                IntRange(0, endOfRange.div(100) - 1)
                            )
                        }
                    } else if (wrappedItem.index == 4) {
                        wrappedItem.value.value?.let { endOfRange ->
                            cashBacks[wrappedItem.index - 1].value?.let { startRangeOf ->
                                rangeList.add(
                                    IntRange(startRangeOf.div(100), endOfRange.div(100))
                                )
                            }
                        }
                    } else {
                        wrappedItem.value.value?.let { endOfRange ->
                            cashBacks[wrappedItem.index - 1].value?.let { startRangeOf ->
                                rangeList.add(
                                    IntRange(startRangeOf.div(100), endOfRange.div(100) - 1)
                                )
                            }
                        }
                    }
                }
            }
        }

        var currentSection = -1

        userResponse?.data?.month_balance?.let { month_spend ->
            currentSection = when (month_spend.div(100)) {
                in rangeList[0] -> {
                    -1
                }
                in rangeList[1] -> {
                    0
                }
                in rangeList[2] -> {
                    1
                }
                in rangeList[3] -> {
                    2
                }
                in rangeList[4] -> {
                    3
                }
                else -> {
                    4
                }
            }
        }

        when (currentSection) {
            -1 ->  currentProgress = 0f // мы не вышли даже на вервую секцию
            4 ->  currentProgress = 100f // выше последней секции
            else -> {
                val p = userResponse?.data?.month_balance?.div(100)
                val firstRange = rangeList[currentSection + 1].first
                val endRange = rangeList[currentSection + 1].last

                //текущее положение в рэндж листе
                val currentPositionRangeList = currentSection + 1

                // текущий рэндж, до которого мы дошли
                val range = endRange - firstRange

                var sum = 0
                for (index in 0 until currentPositionRangeList) {
                    sum += rangeList[index].last - rangeList[index].first + 1
                }

                val c = p?.minus(sum)
                val percentInCurrentRange =
                    c?.toFloat()
                        ?.div(range)?.times(25)
                        ?.plus(
                            currentPositionRangeList.minus(1)
                                .times(25)
                        )
                percentInCurrentRange?.let {
                    currentProgress = it
                }
            }
        }

        var remainValue: Int?
        if (currentSection == 4) {
            remainValue = -1
        } else {
            remainValue = moneyValues[currentSection + 1].minus(userResponse?.data?.month_balance?.div(100)!!)
        }

        return ClearUserModel(
            userResponse?.data?.balance?.div(100.0),
            userResponse?.data?.name,
            moneyValues,
            percentValues,
            currentSection,
            currentProgress,
            remainValue
        )
    }

    fun setStateEvent(event: UserStateEvent) {
        _stateEvent.value = event
    }

}