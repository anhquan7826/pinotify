package com.anhquan.pinotify.model

data class PlayIntegrityVerdict(
    val deviceIntegrity: DeviceIntegrity,
)

data class DeviceIntegrity(
    val deviceRecognitionVerdict: List<String>
)
