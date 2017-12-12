package net.ericsson.emovs.cast.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import net.ericsson.emovs.cast.EMPCastProvider;
import net.ericsson.emovs.cast.EmpReceiverChannel;
import net.ericsson.emovs.cast.models.MediaTrack;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Joao Coelho on 2017-12-11.
 */

public class TrackSelectorFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final ArrayList mSelectedItems = new ArrayList();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Track Selection")
                .setMultiChoiceItems(getTrackNames(), null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if (isChecked) {
                                    // TODO: select track
                                    //mSelectedItems.add(which);
                                } else if (mSelectedItems.contains(which)) {
                                    // TODO: mute track
                                    //mSelectedItems.remove(Integer.valueOf(which));
                                }
                            }
                        })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        return builder.create();
    }

    private CharSequence[] getTrackNames() {
        EmpReceiverChannel empReceiverChannel = EMPCastProvider.getInstance().getReceiverChannel();
        ArrayList<CharSequence> trackNames = new ArrayList<>();

        if (empReceiverChannel.audioTracks != null) {
            for (MediaTrack track : empReceiverChannel.audioTracks) {
                Locale locale = new Locale(track.getLanguage());
                trackNames.add("(audio) " + locale.getDisplayLanguage().substring(0, 1).toUpperCase() + locale.getDisplayLanguage().substring(1));
            }
        }

        if (empReceiverChannel.textTracks != null) {
            for (MediaTrack track : empReceiverChannel.textTracks) {
                Locale locale = new Locale(track.getLanguage());
                trackNames.add("(subs) " + locale.getDisplayLanguage().substring(0, 1).toUpperCase() + locale.getDisplayLanguage().substring(1));
            }
        }

        return trackNames.toArray(new CharSequence[trackNames.size()]);
    }
}
