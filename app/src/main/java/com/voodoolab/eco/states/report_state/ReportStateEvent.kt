package com.voodoolab.eco.states.report_state

sealed class ReportStateEvent {
    class SentReportEvent(
        val tokenApp: String,
        val text: String?,
        val operationId: Int?,
        val rating: Double?
        ) : ReportStateEvent()
}