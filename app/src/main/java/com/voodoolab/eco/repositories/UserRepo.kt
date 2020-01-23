package com.voodoolab.eco.repositories

import androidx.lifecycle.LiveData
import com.voodoolab.eco.models.ClearUserModel
import com.voodoolab.eco.models.Data
import com.voodoolab.eco.network.DataState
import com.voodoolab.eco.network.NetworkBoundResource
import com.voodoolab.eco.network.RetrofitBuilder
import com.voodoolab.eco.responses.LogoutResponse
import com.voodoolab.eco.responses.UpdateNameResponse
import com.voodoolab.eco.responses.UserInfoResponse
import com.voodoolab.eco.states.logout_state.LogoutViewState
import com.voodoolab.eco.states.user_state.UserViewState
import com.voodoolab.eco.utils.ApiSuccessResponse
import com.voodoolab.eco.utils.GenericApiResponse

object UserRepo {
    fun getUserInfo(token: String): LiveData<DataState<UserViewState>> {
        return object : NetworkBoundResource<UserInfoResponse, UserViewState>() {
            override fun handleApiSuccessResponse(response: ApiSuccessResponse<UserInfoResponse>) {
                result.value = DataState.data(
                    data =  UserViewState(
                        userResponse = convertDataFromRawDataToPresentData(response.body)
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<UserInfoResponse>> {
                return RetrofitBuilder.apiService.getUserInfo("Bearer $token", "application/json")
            }
        }.asLiveData()
    }

    fun updateUserName(token: String, name: String): LiveData<DataState<UserViewState>> {
        return object : NetworkBoundResource<UserInfoResponse, UserViewState>() {
            override fun handleApiSuccessResponse(response: ApiSuccessResponse<UserInfoResponse>) {
                result.value = DataState.data(
                    data = UserViewState(
                        userResponse = convertDataFromRawDataToPresentData(response.body)
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<UserInfoResponse>> {
                return RetrofitBuilder.apiService.setNewName("Bearer $token", name)
            }
        }.asLiveData()
    }

    fun logout(token: String): LiveData<DataState<LogoutViewState>> {
        return object : NetworkBoundResource<LogoutResponse, LogoutViewState>() {
            override fun handleApiSuccessResponse(response: ApiSuccessResponse<LogoutResponse>) {
                result.value = DataState.data(
                    data = LogoutViewState(
                        logoutResponse = response.body
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<LogoutResponse>> {
                return RetrofitBuilder.apiService.logout("Bearer $token")
            }
        }.asLiveData()
    }

    private fun convertDataFromRawDataToPresentData(userResponse: UserInfoResponse?): ClearUserModel? {
        userResponse?.let { rawResponse ->
            val moneyValues = ArrayList<Int>()
            val percentValues = ArrayList<Int>()
            rawResponse.month_cash_back?.forEach {
                it.value?.let { money ->
                    moneyValues.add(money.div(100))
                }

                it.percent?.let { percent ->
                    percentValues.add(percent)
                }
            }

            var currentProgress: Float? = null
            val rangeList: ArrayList<IntRange> = ArrayList()

            rawResponse.month_cash_back?.let { cashBacks ->
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

            rawResponse.data?.month_balance?.let { month_spend ->
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
                -1 -> currentProgress = 0f // мы не вышли даже на вервую секцию
                4 -> currentProgress = 100f // выше последней секции
                else -> {
                    val p = rawResponse.data?.month_balance?.div(100)
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

            val remainValue: Int? = if (currentSection == 4) {
                -1
            } else {
                moneyValues[currentSection + 1].minus(rawResponse.data?.month_balance?.div(100)!!)
            }

            val kop =
                if (rawResponse.data?.balance?.rem(100).toString().length == 2) rawResponse.data?.balance?.rem(
                    100
                ).toString() else "0${rawResponse.data?.balance?.rem(100)}"

            return ClearUserModel(
                rawResponse.data?.balance?.div(100),
                kop,
                rawResponse.data?.name,
                moneyValues,
                percentValues,
                currentSection,
                currentProgress,
                remainValue
            )
        }
        return null
    }
}