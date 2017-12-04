package net.ebstv.emp.castsenderdemo;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.gms.cast.CastStatusCodes;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.google.android.gms.common.images.WebImage;


import net.ebstv.emp.castsenderdemo.auth.DeviceInfo;
import net.ebstv.emp.castsenderdemo.auth.EmpAuthProvider;
import net.ebstv.emp.castsenderdemo.auth.EmpAuthenticationListener;
import net.ericsson.emovs.cast.EmpCastListener;
import net.ericsson.emovs.cast.EmpCustomData;
import net.ericsson.emovs.cast.EmpExposureSettings;
import net.ericsson.emovs.cast.EmpReceiverChannel;
import net.ericsson.emovs.cast.MediaTrack;
import net.ericsson.emovs.cast.R;

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

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final String EMP_URL = "https://psempexposureapi.ebsd.ericsson.net";
    private static final String EMP_CUSTOMER = "BlixtGroup";
    private static final String EMP_BU = "Blixt";
    private static final String EMP_USERNAME = "blixtuser2";
    private static final String EMP_PASSWORD = "blixtuser2";
    private static final String EMP_VOD_ASSET_ID = "NSA2_qwerty";//"1467632767_qwerty";
    private static final String EMP_LIVE_ASSET_ID = "750837_qwerty";
    private static final String EMP_PROGRAM_ID = null;

    private Spinner mAudioSpinner;
    private ArrayAdapter<MediaTrack> mAudioTracks;
    private Spinner mSubtitlesSpinner;
    private ArrayAdapter<MediaTrack> mSubtitlesTracks;
    private Button mPlayVodButton;
    private Button mPlayLiveButton;

    private EmpAuthProvider mAuthProvider;
    private String mSessionToken;
    private CastContext mCastContext;
    private CastSession mCastSession;
    private SessionManagerListener<CastSession> mSessionManagerListener;
    private EmpCastListener mEmpListener;
    private EmpReceiverChannel mEmpReceiverChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupCastListener();
        setupEmpListener();

        mCastContext = CastContext.getSharedInstance(this);
        mCastSession = mCastContext.getSessionManager().getCurrentCastSession();
        mEmpReceiverChannel = EmpReceiverChannel.getSharedInstance(mCastContext);

        loadViews();

        mAuthProvider = new EmpAuthProvider(EMP_URL, EMP_CUSTOMER, EMP_BU);
        EmpLogin();
    }

    private void loadViews() {
        mAudioTracks = new ArrayAdapter<>(this.getApplicationContext(), android.R.layout.simple_spinner_dropdown_item);
        mAudioSpinner = (Spinner) findViewById(R.id.audioSpinner);
        mAudioSpinner.setAdapter(mAudioTracks);
        mAudioSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MediaTrack selectedItem = (MediaTrack)mAudioSpinner.getSelectedItem();
                if( selectedItem.isActive() ) { return; }

                mEmpReceiverChannel.selectAudioTrack(selectedItem.getLanguage());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mSubtitlesTracks = new ArrayAdapter<>(this.getApplicationContext(), android.R.layout.simple_spinner_dropdown_item);
        mSubtitlesSpinner = (Spinner) findViewById(R.id.subtitlesSpinner);
        mSubtitlesSpinner.setAdapter(mSubtitlesTracks);
        mSubtitlesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MediaTrack selectedItem = (MediaTrack)mSubtitlesSpinner.getSelectedItem();
                if( selectedItem.isActive() ) { return; }
                
                if(selectedItem.getId() > -1) {
                    mEmpReceiverChannel.showTextTrack(selectedItem.getLanguage());
                } else {
                    mEmpReceiverChannel.hideTextTrack();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mPlayVodButton = (Button)findViewById(R.id.btn_play_vod);
        mPlayVodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadRemoteMedia(buildMediaInfo(EMP_VOD_ASSET_ID));
            }
        });

        mPlayLiveButton = (Button)findViewById(R.id.btn_play_live);
        mPlayLiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadRemoteMedia(buildMediaInfo(EMP_LIVE_ASSET_ID));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.cast_menu, menu);
        CastButtonFactory.setUpMediaRouteButton(getApplicationContext(), menu, R.id.media_route_menu_item);
        return true;
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart was called");
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() was called");
        mCastContext.getSessionManager().removeSessionManagerListener(
                mSessionManagerListener, CastSession.class);
        mEmpReceiverChannel.removeListener(mEmpListener);
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume() was called");
        mCastContext.getSessionManager().addSessionManagerListener(
                mSessionManagerListener, CastSession.class);
        mEmpReceiverChannel.addListener(mEmpListener);
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop() was called");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy is called");
        EmpLogout();
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(TAG, "onConfigurationChanged is called");
        super.onConfigurationChanged(newConfig);
    }

    private void EmpLogin() {
        mAuthProvider.AuthenticateAsync(DeviceInfo.collect(this), EMP_USERNAME, EMP_PASSWORD,
                new EmpAuthenticationListener() {
                    @Override
                    public void onAuthSuccess(String sessionToken) {
                        mSessionToken = sessionToken;
                        updatePlayButton();
                    }

                    @Override
                    public void onAuthError(String message) {
                        displayAlertMessage(message);
                    }
                });
    }

    private void EmpLogout() {
        if (null != mSessionToken) {
            mAuthProvider.LogoutAsync();
        }
    }

    private void setupCastListener() {
        mSessionManagerListener = new SessionManagerListener<CastSession>() {

            @Override
            public void onSessionEnded(CastSession session, int error) {
                Log.d(TAG, "onSessionEnded (" + CastStatusCodes.getStatusCodeString(error) + ")");
                onApplicationDisconnected();
            }

            @Override
            public void onSessionResumed(CastSession session, boolean wasSuspended) {
                Log.d(TAG, "onSessionResumed");
                onApplicationConnected(session);
            }

            @Override
            public void onSessionResumeFailed(CastSession session, int error) {
                Log.d(TAG, "onSessionResumeFailed (" + CastStatusCodes.getStatusCodeString(error) + ")");
                onApplicationDisconnected();
            }

            @Override
            public void onSessionStarted(CastSession session, String sessionId) {
                Log.d(TAG, "onSessionStarted");
                onApplicationConnected(session);
            }

            @Override
            public void onSessionStartFailed(CastSession session, int error) {
                Log.d(TAG, "onSessionStartFailed (" + CastStatusCodes.getStatusCodeString(error) + ")");
                onApplicationDisconnected();
            }

            @Override
            public void onSessionStarting(CastSession session) {
                Log.d(TAG, "onSessionStarting");
            }

            @Override
            public void onSessionEnding(CastSession session) {
                Log.d(TAG, "onSessionEnding");
//                if(mCastSession != null && mCastSession.getRemoteMediaClient() != null) {
//                    mCastSession.getRemoteMediaClient().stop();
//                }
            }

            @Override
            public void onSessionResuming(CastSession session, String sessionId) {
                Log.d(TAG, "onSessionResuming");
            }

            @Override
            public void onSessionSuspended(CastSession session, int reason) {
                Log.d(TAG, "onSessionSuspended");
            }

            private void onApplicationConnected(CastSession castSession) {
                mCastSession = castSession;
                updatePlayButton();
                invalidateOptionsMenu();
            }

            private void onApplicationDisconnected() {
                updatePlayButton();
                invalidateOptionsMenu();
            }
        };
    }

    private void setupEmpListener() {
        mEmpListener = new EmpCastListener() {
            @Override
            public void onTracksUpdated(List<MediaTrack> audioTracks, List<MediaTrack> subtitleTracks) {
                mAudioTracks.clear();
                for (MediaTrack track: audioTracks) {
                    mAudioTracks.add(track);
                }
                for (int ii = 0; ii < mAudioSpinner.getCount(); ii++) {
                    if (((MediaTrack)mAudioSpinner.getItemAtPosition(ii)).isActive()) {
                        mAudioSpinner.setSelection(ii);
                        break;
                    }
                }

                Boolean isSubsActive = false;
                mSubtitlesTracks.clear();
                for (MediaTrack track: subtitleTracks) {
                    mSubtitlesTracks.add(track);
                    isSubsActive = isSubsActive & track.isActive();
                }
                mSubtitlesTracks.add(new MediaTrack(-1, "Off", "", !isSubsActive));
                for (int ii = 0; ii < mSubtitlesSpinner.getCount(); ii++) {
                    if (((MediaTrack)mSubtitlesSpinner.getItemAtPosition(ii)).isActive()) {
                        mSubtitlesSpinner.setSelection(ii);
                        break;
                    }
                }
            }

            @Override
            public void onVolumeChanged(int volume, boolean muted) {

            }

            @Override
            public void onTimeshiftEnabled(boolean timeshiftEnabled) {
                Log.d(TAG, "onTimeshiftEnabled = " + timeshiftEnabled);
            }

            @Override
            public void onDurationChange(int duration) {

            }

            @Override
            public void onLive(boolean isLive) {
                Log.d(TAG, "onLive = " + isLive);
            }

            @Override
            public void onError(String errorCode, String errorMessage) {
                displayAlertMessage(String.format("Chromecast Error > %s [code:%s]", errorMessage, errorCode));
            }
        };
    }

    private void updatePlayButton() {
        if( null != mCastSession && mCastSession.isConnected() && null != mSessionToken) {
            mPlayVodButton.setEnabled(true);
            mPlayLiveButton.setEnabled(true);
        } else {
            mPlayVodButton.setEnabled(false);
            mPlayLiveButton.setEnabled(false);
        }
    }

    private void loadRemoteMedia(MediaInfo media) {
        if (mCastSession == null) {
            displayAlertMessage("No active cast session");
            return;
        }

        final RemoteMediaClient remoteMediaClient = mCastSession.getRemoteMediaClient();
        if (remoteMediaClient == null) {
            displayAlertMessage("Unable to get remote media client");
            return;
        }

        remoteMediaClient.addListener(new RemoteMediaClient.Listener() {
            @Override
            public void onStatusUpdated() {
                mEmpReceiverChannel.refreshControls();

                // Chromecast API does not handle this well, se we are using a custom message for now.
                /*Log.d(TAG, "status idle reason updated to: " + remoteMediaClient.getIdleReason());
                if(remoteMediaClient.getIdleReason() == MediaStatus.IDLE_REASON_ERROR) {
                    JSONObject customData = remoteMediaClient.getMediaStatus().getCustomData();
                    if (customData != null) {
                        try {
                            String errMessage = customData.has("message") ? customData.getString("message") : "";
                            String errCode = customData.has("code") ? Integer.toString(customData.getInt("code")) : "N/A";
                            displayAlertMessage(String.format("Chromecast Error > %s [code:%s]", errMessage, errCode));
                        } catch (JSONException e) {
                            displayAlertMessage("Unknown chromecast error");
                            e.printStackTrace();
                        }

                    } else {
                        displayAlertMessage("Unknown chromecast error");
                    }
                }*/
             }

            @Override
            public void onMetadataUpdated() {
            }

            @Override
            public void onQueueStatusUpdated() {
            }

            @Override
            public void onPreloadStatusUpdated() {
            }

            @Override
            public void onSendingRemoteMediaRequest() {
            }

            @Override
            public void onAdBreakStatusUpdated() {
            }
        });

        EmpExposureSettings empExposureSettings = new EmpExposureSettings(EMP_URL, mSessionToken, EMP_CUSTOMER, EMP_BU);
        EmpCustomData customData = new EmpCustomData(media.getContentId(), empExposureSettings);
        customData.setProgramId(EMP_PROGRAM_ID);
        remoteMediaClient.load(media, true, 0, customData.toJson());
    }

    protected void displayAlertMessage(final String message){
        AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
        dlg.setMessage(message)
                .setTitle("Error")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .create()
                .show();
    }

    private MediaInfo buildMediaInfo(String assetId) {
        MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);

        movieMetadata.putString(MediaMetadata.KEY_TITLE, "Batman");
        movieMetadata.putString(MediaMetadata.KEY_SUBTITLE, "Bad blood");
        movieMetadata.addImage(new WebImage(Uri.parse("http://emp.ebsf.fr/MEDIA/batman.png")));

        // receiver decides streamType
        return new MediaInfo.Builder(assetId)
                .setMetadata(movieMetadata)
                .setStreamType(MediaInfo.STREAM_TYPE_NONE)
                .setContentType("video/mp4")
                .build();
    }
}
