package net.ericsson.emovs.cast;
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

import org.json.JSONException;
import org.json.JSONObject;

public class EmpCustomData {
    public final EmpExposureSettings exposureSettings;
    public final String assetId;
    public String programId;
    public boolean useLastViewedOffset;
    public boolean timeshiftEnabled;


    public EmpCustomData(String assetId, EmpExposureSettings settings) {
        this.assetId = assetId;
        this.exposureSettings = settings;
        this.timeshiftEnabled = true;
        this.useLastViewedOffset = false;
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
        } catch (JSONException e) {
        }

        return customData;
    }
}
