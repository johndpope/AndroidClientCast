package net.ebstv.emp.castsenderdemo.auth;
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

import android.os.AsyncTask;
import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class EmpAuthProvider {
    private static final String EMP_URL = "%s/v1/customer/%s/businessunit/%s";
    private static final int HTTP_CONNECT_TIMEOUT = 4000;
    private static final int HTTP_READ_TIMEOUT = 8000;

    private final String empEndpoint;
    private String mAuthToken;

    public EmpAuthProvider(String empUrl, String customer, String businessUnit) {
        empEndpoint = String.format(EMP_URL, empUrl, customer, businessUnit);
    }

    public String getAuthToken() {
        return mAuthToken;
    }

    public void AuthenticateAsync(DeviceInfo device, String userName, String password, @Nullable final EmpAuthenticationListener listener) {
        try {
            JSONObject authRequest = new JSONObject()
                    .put("deviceId", device.deviceId)
                    .put("rememberMe", false)
                    .put("username", userName)
                    .put("password", password)
                    .put("device", new JSONObject()
                            .put("height", device.height)
                            .put("width", device.width)
                            .put("model", device.model)
                            .put("name", device.name)
                            .put("os", device.os)
                            .put("osVersion", device.osVersion)
                            .put("manufacturer", device.manufacturer)
                            .put("type", device.type));

            final AsyncTask<JSONObject, Void, JSONObject> loginTask = new AsyncTask<JSONObject, Void, JSONObject>() {
                private Exception backgroundException;

                @Override
                protected JSONObject doInBackground(JSONObject... params) {
                    try {
                        return post("/auth/login", params[0]);
                    } catch (Exception e) {
                        backgroundException = e;
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(JSONObject response) {
                    if(null != listener) {
                        if (null != backgroundException) {
                            listener.onAuthError(backgroundException.getMessage());
                            return;
                        }

                        try {
                            String authToken = response.getString("sessionToken");

                            // TODO parse error JSON and throw error

                            mAuthToken = authToken;
                            listener.onAuthSuccess(mAuthToken);
                        } catch (JSONException e) {
                            listener.onAuthError("Error while parsing authentication response");
                        }
                    }
                }
            };

            loginTask.execute(authRequest);
        } catch (JSONException e) {
            if(null != listener) {
                listener.onAuthError("Error while building authentication message: " + e.getMessage());
            }
        }
    }

    public void LogoutAsync() {
        final AsyncTask<Void, Void, Void> loginTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    HttpURLConnection connection = getHttpConnection(empEndpoint + "/auth/session", "DELETE");
                    connection.getResponseMessage();
                } catch (Exception e) {}
                return null;
            }
        };
    }

    private JSONObject post(String resource, JSONObject data) throws Exception {
        HttpURLConnection connection = getHttpConnection(empEndpoint + resource, "POST");

        OutputStream outputStream = connection.getOutputStream();
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            try {
                writer.write(data.toString());
            } catch (Exception e0) {
                throw e0;
            } finally {
                try {
                    writer.close();
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                outputStream.close();
            } catch (Exception e) {
            }
        }

        int httpResponseCode = connection.getResponseCode();
        StringBuilder response = new StringBuilder();

        InputStream responseStream = (httpResponseCode != 200) ? connection.getErrorStream() : connection.getInputStream();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(responseStream, "UTF-8"));
            try {
                String line;
                while(null != (line = br.readLine())) {
                    response.append(line);
                }
            } catch (Exception e0) {
                throw e0;
            } finally {
                try {
                    br.close();
                } catch (Exception e) {
                }
            }
        } catch (Exception e0) {
            throw e0;
        } finally {
            try {
                responseStream.close();
            } catch (Exception e) {
            }
        }

        return new JSONObject(response.toString());

    }

    private HttpURLConnection getHttpConnection(String url, String method) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) ((new URL(url).openConnection()));

        connection.setRequestMethod(method);
        connection.setDoOutput(!method.equalsIgnoreCase("delete"));
        connection.setDoInput(true);
        connection.setConnectTimeout(HTTP_CONNECT_TIMEOUT);
        connection.setReadTimeout(HTTP_READ_TIMEOUT);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");

        if (mAuthToken != null) {
            connection.setRequestProperty("Authorization", "Bearer " + mAuthToken);
        }

        connection.connect();

        return connection;
    }
}
