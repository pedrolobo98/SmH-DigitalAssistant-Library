package com.smh_digitalassistant_library.models

data class DailyTasksViewModel (
    val taskNum: String,
    val taskDesc: String,
    val taskConfirm: Boolean,
    val taskQuestionnaire: Boolean)