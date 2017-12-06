package net.ericsson.emovs.cast;

import net.ericsson.emovs.cast.interfaces.IEmpCastListener;
import net.ericsson.emovs.cast.models.MediaTrack;

import java.util.List;

/**
 * Created by Joao Coelho on 2017-12-06.
 */

public class EmptyEmpCastListener implements IEmpCastListener {
    @Override
    public void onTracksUpdated(List<MediaTrack> audioTracks, List<MediaTrack> subtitleTracks) {

    }

    @Override
    public void onVolumeChanged(int volume, boolean muted) {

    }

    @Override
    public void onTimeshiftEnabled(boolean timeshiftEnabled) {

    }

    @Override
    public void onDurationChange(int duration) {

    }

    @Override
    public void onLive(boolean isLive) {

    }

    @Override
    public void onError(String errorCode, String message) {

    }
}
