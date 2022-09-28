package com.example.digitalassistantapp.models

data class DailyTasksViewModel (
    val taskNum: String,
    val taskDesc: String,
    val taskConfirm: Boolean,
    val taskQuestionnaire: Boolean)