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

public class MediaTrack {
    private final int mId;
    private final String mLabel;
    private final String mLanguage;
    private Boolean mActive;

    public MediaTrack(int id, String label, String language, Boolean active) {
        this.mId = id;
        this.mLabel = label;
        this.mLanguage = language;
        this.mActive = active;
    }

    public MediaTrack(int id, String label, String language) {
        this(id, label, language, false);
    }

    public int getId() {
        return mId;
    }

    public String getLabel() {
        return mLabel;
    }

    public String getLanguage() {
        return mLanguage;
    }

    public Boolean isActive() {
        return mActive;
    }

    public void setActive(Boolean active) {
        mActive = active;
    }

    @Override
    public String toString() {
        return this.mLabel;
    }
}
