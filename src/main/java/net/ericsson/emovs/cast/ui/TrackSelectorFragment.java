package net.ericsson.emovs.cast.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import net.ericsson.emovs.cast.R;

import java.util.ArrayList;

/**
 * Created by Joao Coelho on 2017-12-11.
 */

public class TrackSelectorFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // TODO: refactor
        final ArrayList mSelectedItems = new ArrayList();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        CharSequence[] tackCodes = { "en", "es", "pt" };

        builder.setTitle("Track Selection")
                .setMultiChoiceItems(tackCodes, null,
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
}
