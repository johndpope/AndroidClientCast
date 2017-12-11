package net.ericsson.emovs.cast.ui.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.media.widget.ExpandedControllerActivity;

import net.ericsson.emovs.cast.EMPCastProvider;
import net.ericsson.emovs.cast.EmptyEmpCastListener;
import net.ericsson.emovs.cast.R;
import net.ericsson.emovs.cast.interfaces.IEmpCastListener;
import net.ericsson.emovs.cast.models.MediaTrack;
import net.ericsson.emovs.cast.ui.TrackSelectorFragment;

import java.util.List;

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

public class ExpandedControlsActivity extends ExpandedControllerActivity {
    //IEmpCastListener empCastListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //if (this.empCastListener != null) {
        //    EMPCastProvider.getInstance().getReceiverChannel().removeListener(this.empCastListener);
        //}

        //this.empCastListener = new EmptyEmpCastListener() {
        //    @Override
        //    public void onTracksUpdated(List<MediaTrack> audioTracks, List<MediaTrack> subtitleTracks) {
        //    }
        //};

        ImageView ccBtn = geTracksButton();

        if (ccBtn != null) {
            ccBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });
            bindTrackSelectionFragment(ccBtn);
        }

        //EMPCastProvider.getInstance().getReceiverChannel().addListener(this.empCastListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.cast_menu, menu);
        CastButtonFactory.setUpMediaRouteButton(this, menu, R.id.media_route_menu_item);
        return true;
    }

    //@Override
    //protected void onDestroy() {
        //super.onDestroy();
        //if (this.empCastListener != null) {
        //    EMPCastProvider.getInstance().getReceiverChannel().removeListener(this.empCastListener);
        //}
    //}

    private ImageView geTracksButton() {
        for (int i = 0; i < getButtonSlotCount(); ++i) {
            if (getButtonTypeAt(i) == R.id.cast_button_type_closed_caption) {
                return getButtonImageViewAt(i);
            }
        }
        return null;
    }

    private void bindTrackSelectionFragment(ImageView  ccBtn) {
        ViewGroup ccBtnHolder = (ViewGroup) ccBtn.getParent();
        TrackSelectorFragment fragment = new TrackSelectorFragment();

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ft.add(ccBtnHolder.getId(), fragment);
        ft.commit();
    }
}
