package org.listenbrainz.android.model

data class MbidMapping(
    val artist_mbids: List<String>,
    val recording_mbid: String,
    val release_mbid: String? = null
)