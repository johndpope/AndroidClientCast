package net.ericsson.emovs.cast.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.widget.CheckedTextView;
import android.widget.ListView;

import net.ericsson.emovs.cast.EMPCastProvider;
import net.ericsson.emovs.cast.EmpReceiverChannel;
import net.ericsson.emovs.cast.R;
import net.ericsson.emovs.cast.models.MediaTrack;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Joao Coelho on 2017-12-11.
 */

public class TrackSelectorFragment extends DialogFragment {
    boolean[] selectedItems;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final ArrayList mSelectedItems = new ArrayList();
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.TrackSelectorCustomDialog));

        CharSequence[] trackNames = getTrackNames();

        if (this.selectedItems == null || this.selectedItems.length != trackNames.length) {
            this.selectedItems = new boolean[trackNames.length];
        }

        builder.setTitle("Track Selection")
                .setMultiChoiceItems(trackNames, this.selectedItems,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if (isChecked) {
                                    selectedItems[which] = true;
                                } else {
                                    for (int i = 0; i < selectedItems.length; ++i) {
                                        if (i == which) {
                                            selectedItems[which] = false;
                                            break;
                                        }
                                    }
                                }
                                unselectOlderTracks((AlertDialog) dialog, which);
                                selectReceiverTrack();
                            }
                        })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        Log.d("", "");
                    }
                });

        Dialog dialog = builder.create();
        bindReceiverTrackSelection((AlertDialog) dialog);
        return dialog;
    }

    private void unselectOlderTracks(AlertDialog dialog, int which) {
        EmpReceiverChannel empReceiverChannel = EMPCastProvider.getInstance().getReceiverChannel();
        ListView dialogItems = dialog.getListView();
        boolean isSelected = this.selectedItems[which];
        if(isSelected == false) {
            return;
        }
        if (which < empReceiverChannel.audioTracks.size()) {
            for (int j = 0; j < empReceiverChannel.audioTracks.size(); ++j) {
                if(j != which) {
                    dialogItems.setItemChecked(j, false);
                    this.selectedItems[j] = false;
                }
            }
        }
        else {
            for (int j = empReceiverChannel.audioTracks.size(); j < this.selectedItems.length; ++j) {
                if(j != which) {
                    dialogItems.setItemChecked(j, false);
                    this.selectedItems[j] = false;
                }
            }
        }
    }

    private void selectReceiverTrack() {
        EmpReceiverChannel empReceiverChannel = EMPCastProvider.getInstance().getReceiverChannel();

        for (int i = 0; i < empReceiverChannel.audioTracks.size(); ++i) {
            if (this.selectedItems[i]) {
                empReceiverChannel.selectAudioTrack(empReceiverChannel.audioTracks.get(i).getLanguage());
                break;
            }
        }

        for (int i = 0; i < empReceiverChannel.textTracks.size(); ++i) {
            if (this.selectedItems[empReceiverChannel.audioTracks.size() + i]) {
                empReceiverChannel.showTextTrack(empReceiverChannel.textTracks.get(i).getLanguage());
                break;
            }
        }
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

    private void bindReceiverTrackSelection(AlertDialog dialog) {
        EmpReceiverChannel empReceiverChannel = EMPCastProvider.getInstance().getReceiverChannel();
        ListView dialogItems = dialog.getListView();

        int n = 0;
        if (empReceiverChannel.audioTracks != null) {
            for (MediaTrack track : empReceiverChannel.audioTracks) {
                if (track.isActive()) {
                    this.selectedItems[n] = true;
                    dialogItems.setItemChecked(n, true);
                }
                ++n;
            }
        }

        if (empReceiverChannel.textTracks != null) {
            for (MediaTrack track : empReceiverChannel.textTracks) {
                if (track.isActive()) {
                    this.selectedItems[n] = true;
                    dialogItems.setItemChecked(n, true);
                }
                ++n;
            }
        }
    }
}
