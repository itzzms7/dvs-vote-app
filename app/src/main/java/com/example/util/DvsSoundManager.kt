package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.example.R

/**
 * Lightweight, zero-dependency sound manager for DVS subtle interaction chimes.
 * Uses USAGE_NOTIFICATION_EVENT to strictly respect device ringer/silent mode and system volume.
 */
class DvsSoundManager(context: Context) {

    private val audioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()

    private val soundPool = SoundPool.Builder()
        .setMaxStreams(2)
        .setAudioAttributes(audioAttributes)
        .build()

    private val walletChimeId: Int = soundPool.load(context.applicationContext, R.raw.chime_wallet_connected, 1)
    private val voteChimeId: Int = soundPool.load(context.applicationContext, R.raw.chime_vote_success, 1)

    /**
     * Soft confirmation chime played when wallet connection is successfully completed.
     */
    fun playWalletConnectedSound() {
        try {
            soundPool.play(walletChimeId, 0.75f, 0.75f, 1, 0, 1.0f)
        } catch (_: Exception) {
            // Non-blocking fallback
        }
    }

    /**
     * Distinct but restrained success chime played after the voting operation has actually succeeded.
     */
    fun playVoteSuccessSound() {
        try {
            soundPool.play(voteChimeId, 0.8f, 0.8f, 1, 0, 1.0f)
        } catch (_: Exception) {
            // Non-blocking fallback
        }
    }

    fun release() {
        soundPool.release()
    }
}
