package net.ericsson.emovs.cast.models;
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

import android.text.TextUtils;

import net.ericsson.emovs.cast.models.EmpExposureSettings;
import net.ericsson.emovs.utilities.emp.EMPRegistry;
import net.ericsson.emovs.utilities.models.EmpImage;

import org.json.JSONException;
import org.json.JSONObject;

public class EmpCustomData {
    // These properties shall be sent to receiver
    public final EmpExposureSettings exposureSettings;
    public String assetId;
    public String programId;
    public boolean useLastViewedOffset;
    public boolean timeshiftEnabled;

    public String audioLanguage;
    public String textLanguage;
    public long startTime;
    public Long absoluteStartTime;
    public Long maxBitrate;
    public boolean autoplay;

    // These properties are for android internal use - no need to send them to receiver
    public String locale;
    public String imageType;
    public EmpImage.Orientation imageOrientation;

    public EmpCustomData() {
        this.assetId = assetId;
        this.exposureSettings = new EmpExposureSettings();
        this.timeshiftEnabled = true;
        this.useLastViewedOffset = false;
        this.locale = EMPRegistry.locale();
        this.imageOrientation = EmpImage.Orientation.LANDSCAPE;
        this.autoplay = true;
        this.startTime = 0;
    }

    public void setProgramId(String programId) {
        this.programId = programId;
    }

    public JSONObject toJson() {
        JSONObject customData = new JSONObject();

        try {
            customData.put("assetId", assetId);

            if(!TextUtils.isEmpty(programId)) {
                customData.put("programId", programId);
            }

            customData.put("ericssonexposure", exposureSettings.toJson());
            customData.put("timeShiftDisabled", !timeshiftEnabled);
            customData.put("useLastViewedOffset", useLastViewedOffset);
            customData.put("startTime", startTime);
            customData.put("autoplay", autoplay);

            if (audioLanguage != null) {
                customData.put("audioLanguage", audioLanguage);
            }

            if (textLanguage != null) {
                customData.put("textLanguage", textLanguage);
            }

            if (absoluteStartTime != null) {
                customData.put("absoluteStartTime", absoluteStartTime);
            }

            if (maxBitrate != null) {
                customData.put("maxBitrate", maxBitrate);
            }
        }
        catch (JSONException e) {
        }

        return customData;
    }
}
