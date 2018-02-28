package net.ericsson.emovs.cast;

import net.ericsson.emovs.cast.interfaces.IEmpCastListener;
import net.ericsson.emovs.cast.models.MediaTrack;
import net.ericsson.emovs.utilities.models.EmpProgram;

import org.json.JSONObject;

import java.util.List;

/**
 * Empty class that implements IEmpCastListener just to avoid unnecessary and ugly code on the fronted side
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

    @Override
    public void onProgramChanged(EmpProgram program) {

    }

    @Override
    public void onEntitlementChange(JSONObject entitlementJson) {

    }
}
