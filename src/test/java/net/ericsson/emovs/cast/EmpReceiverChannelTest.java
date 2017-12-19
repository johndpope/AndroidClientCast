package net.ericsson.emovs.cast;


import com.google.android.gms.cast.framework.CastContext;

import net.ericsson.emovs.cast.models.EmpCustomData;
import net.ericsson.emovs.cast.models.EmpExposureSettings;
import net.ericsson.emovs.exposure.clients.exposure.ExposureClient;
import net.ericsson.emovs.utilities.emp.EMPRegistry;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


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

@RunWith(RobolectricTestRunner.class)
public class EmpReceiverChannelTest {

    @Test
    public void testMessageReceived() throws Exception {
        EmpReceiverChannel receiverChannel = new EmpReceiverChannel(null) {
            protected void EmpReceiverChannel(CastContext ctx) {}
        };

        EmptyEmpCastListener listener = new EmptyEmpCastListener();
        receiverChannel.addListener(listener);

        String volumeChanged = "{\"type\":\"volumechange\",\"data\":{\"volume\":1,\"muted\":false}}";
        String timeshiftEnabled = "{\"type\":\"timeShiftEnabled\",\"data\":true}";
        String isLive = "{\"type\":\"isLive\",\"data\":false}";
        String durationChanged = "{\"type\":\"durationchange\",\"data\":0}";
        String tracksUpdated = "{\"type\":\"tracksupdated\",\"data\":{\"tracksInfo\":{\"tracks\":[{\"id\":1,\"type\":\"audio\",\"label\":\"en\",\"language\":\"en\"},{\"id\":2,\"type\":\"text\",\"label\":\"en\",\"language\":\"en\"}],\"activeTrackIds\":[]}}}";
        String error = "{\"type\":\"error\",\"data\":{\"message\":\"N/A\",\"code\":-1001}}";

        // TODO: check listeners are receiving the updates
        receiverChannel.onMessageReceived(null, "urn:x-cast:com.ericsson.cast.receiver", volumeChanged);
        receiverChannel.onMessageReceived(null, "urn:x-cast:com.ericsson.cast.receiver", timeshiftEnabled);
        receiverChannel.onMessageReceived(null, "urn:x-cast:com.ericsson.cast.receiver", isLive);
        receiverChannel.onMessageReceived(null, "urn:x-cast:com.ericsson.cast.receiver", durationChanged);
        receiverChannel.onMessageReceived(null, "urn:x-cast:com.ericsson.cast.receiver", tracksUpdated);
        receiverChannel.onMessageReceived(null, "urn:x-cast:com.ericsson.cast.receiver", error);
    }

}