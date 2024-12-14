package com.anhquan.pinotify.model

data class PlayIntegrityStatus(
    val basic: Boolean = false,
    val device: Boolean = false,
    val strong: Boolean = false,
)