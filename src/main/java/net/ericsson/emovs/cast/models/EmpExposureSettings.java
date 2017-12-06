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

import net.ericsson.emovs.exposure.clients.exposure.ExposureClient;
import net.ericsson.emovs.utilities.emp.EMPRegistry;

import org.json.JSONException;
import org.json.JSONObject;

public class EmpExposureSettings {

    public EmpExposureSettings() {
    }

    public JSONObject toJson() {
        JSONObject exposureSettings = new JSONObject();

        try {
            exposureSettings.put("exposureApiURL", EMPRegistry.apiUrl());
            exposureSettings.put("customer", EMPRegistry.customer());
            exposureSettings.put("businessUnit", EMPRegistry.businessUnit());
            exposureSettings.put("sessionToken", ExposureClient.getInstance().getSessionToken());
        }
        catch (JSONException e) {

        }

        return exposureSettings;
    }
}
