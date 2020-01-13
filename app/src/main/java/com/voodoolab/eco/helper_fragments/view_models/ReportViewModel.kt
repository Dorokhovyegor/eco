package com.voodoolab.eco.helper_fragments.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.voodoolab.eco.network.DataState
import com.voodoolab.eco.repositories.ReportRepo
import com.voodoolab.eco.responses.ReportResponse
import com.voodoolab.eco.states.report_state.ReportStateEvent
import com.voodoolab.eco.states.report_state.ReportViewState

class ReportViewModel : ViewModel() {
    private val _stateEventReport: MutableLiveData<ReportStateEvent> = MutableLiveData()
    private val _viewStateReport: MutableLiveData<ReportViewState> = MutableLiveData()

    val viewState: LiveData<ReportViewState>
        get() = _viewStateReport

    val dataStateReport: LiveData<DataState<ReportViewState>> = Transformations
        .switchMap(_stateEventReport) { stateEvent ->
            stateEvent?.let {
                handleStateEvent(it)
            }
        }

    private fun handleStateEvent(stateEvent: ReportStateEvent): LiveData<DataState<ReportViewState>> {
        return when (stateEvent) {
            is ReportStateEvent.SentReportEvent -> {
                println("DEBUG token ${stateEvent.tokenApp}")
                println("DEBUG token ${stateEvent.operationId}")
                println("DEBUG token ${stateEvent.text}")
                println("DEBUG token ${stateEvent.rating}")
                return ReportRepo.sendReport(stateEvent.tokenApp, stateEvent.operationId, stateEvent.text, stateEvent.rating)
            }
        }
    }

    fun setReportResponse(reportResponse: ReportResponse) {
        val update = getCurrentStateOrNew()
        update.reportResponse = reportResponse
        _viewStateReport.value = update
    }

    fun getCurrentStateOrNew(): ReportViewState {
        val value = viewState.value?.let {
            it
        } ?: ReportViewState()
        return value
    }

    fun setStateEvent(event: ReportStateEvent) {
        _stateEventReport.value = event
    }
}