package net.ericsson.emovs.cast;

import android.net.Uri;

import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.google.android.gms.common.images.WebImage;

import net.ericsson.emovs.utilities.emp.EMPRegistry;
import net.ericsson.emovs.utilities.interfaces.IPlayable;
import net.ericsson.emovs.utilities.models.EmpAsset;
import net.ericsson.emovs.utilities.models.EmpChannel;
import net.ericsson.emovs.utilities.models.EmpImage;
import net.ericsson.emovs.utilities.models.EmpProgram;

/**
 * Created by Joao Coelho on 2017-12-04.
 */

public class EMPCastProvider {
    private CastContext castContext;
    private EmpReceiverChannel empReceiverChannel;

    private static class EMPCastProviderHolder {
        private final static EMPCastProvider sInstance = new EMPCastProvider();
    }

    public static EMPCastProvider getInstance() {
        return EMPCastProvider.EMPCastProviderHolder.sInstance;
    }

    public EMPCastProvider() {
        this.castContext = CastContext.getSharedInstance(EMPRegistry.applicationContext());
        this.empReceiverChannel = EmpReceiverChannel.getSharedInstance(this.castContext);
    }

    public CastSession getCastSession() {
        return this.castContext.getSessionManager().getCurrentCastSession();
    }

    // TODO: missing to pass castProperties
    public void startCasting(IPlayable playable, final Runnable onReady) {
        if (this.getCastSession() == null) {
            // TODO: return error
            return;
        }

        final RemoteMediaClient remoteMediaClient = this.getCastSession().getRemoteMediaClient();
        if (remoteMediaClient == null) {
            //displayAlertMessage("Unable to get remote media client");
            return;
        }

        remoteMediaClient.addListener(new RemoteMediaClient.Listener() {
            @Override
            public void onStatusUpdated() {
                if (onReady != null) {
                    onReady.run();
                }
                empReceiverChannel.refreshControls();

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

        MediaInfo media = buildMediaInfo(playable);
        EmpCustomData customData = new EmpCustomData(media.getContentId(), new EmpExposureSettings());

        if (playable instanceof EmpProgram) {
            customData.setProgramId(((EmpProgram) playable).programId);
        }

        remoteMediaClient.load(media, true, 0, customData.toJson());
    }

    public void stopCasting() {

    }

    public EmpReceiverChannel getReceiverChannel() {
        return this.empReceiverChannel;
    }

    private MediaInfo buildMediaInfo(IPlayable playable) {
        MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
        MediaInfo.Builder builder;
        String locale = EMPRegistry.locale();

        if (playable instanceof EmpAsset || playable instanceof EmpProgram) {
            EmpAsset asset = (EmpAsset) playable;
            movieMetadata.putString(MediaMetadata.KEY_TITLE, asset.localized.getTitle(locale));
            movieMetadata.putString(MediaMetadata.KEY_SUBTITLE, asset.localized.getDescriptions(locale));

            EmpImage image = asset.localized.getImage(locale, EmpImage.Orientation.LANDSCAPE);
            if (image != null && image.url != null) {
                movieMetadata.addImage(new WebImage(Uri.parse(image.url)));
            }

            builder = new MediaInfo.Builder(asset.assetId);
        }
        else if (playable instanceof EmpChannel) {
            EmpChannel channel = (EmpChannel) playable;

            movieMetadata.putString(MediaMetadata.KEY_TITLE, channel.localized.getTitle(locale));
            movieMetadata.putString(MediaMetadata.KEY_SUBTITLE, channel.localized.getDescriptions(locale));

            EmpImage image = channel.localized.getImage(locale, EmpImage.Orientation.LANDSCAPE);
            if (image != null && image.url != null) {

                movieMetadata.addImage(new WebImage(Uri.parse(image.url)));
            }

            builder = new MediaInfo.Builder(channel.channelId);
        }
        else {
            return null;
        }

        return builder.setMetadata(movieMetadata)
                .setStreamType(MediaInfo.STREAM_TYPE_NONE)
                .setContentType("video/mp4").build();
    }

}
