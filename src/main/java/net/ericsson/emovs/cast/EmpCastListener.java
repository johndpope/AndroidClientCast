package net.ericsson.emovs.cast;
/*
 * Copyright (c) 2017 Ericsson. All Rights Reserved
 *
 * This SOURCE CODE FILE, which has been provided by Ericsson as part
 * of an Ericsson software product for use ONLY by licensed users of the
 * product, includes CONFIDENTIAL and PROPRIETARY information of Ericsson.
 *
 * USE OF THIS SOFTWARE IS GOVERNED BY THE TERMS AND CONDITIONS OF
 * THE LICENSE STATEMENT AND LIMITED WARRANTY FURNISHED WITH
 * THE PRODUCT.
 */

import java.util.List;

/**
 * Callbacks triggered by EMP Chromecast communication channel
 */
public interface EmpCastListener {

    /**
     * Triggered when the selected audio tracks change or when a sender app request it
     * @param audioTracks list of available audio tracks
     * @param subtitleTracks list of available subtitle tracks
     */
    void onTracksUpdated(List<MediaTrack> audioTracks, List<MediaTrack> subtitleTracks);

    /**
     * Triggered when the audio volume changes (eg. using the TV remote)
     * @param volume
     * @param muted
     */
    void onVolumeChanged(int volume, boolean muted);

    /**
     * Triggered when timeshift is enabled or disabled (when disabled the user can't pause the stream)
     * @param timeshiftEnabled
     */
    void onTimeshiftEnabled(boolean timeshiftEnabled);

    /**
     * Triggered when the asset duration changes
     * @param duration in milliseconds
     */
    void onDurationChange(int duration);

    /**
     * Triggered when the stream type changes (live or timeshift)
     * @param isLive
     */
    void onLive(boolean isLive);

    /**
     * Triggered when the receiver throws an error
     * @param errorCode
     * @param message
     */
    void onError(String errorCode, String message);
}
