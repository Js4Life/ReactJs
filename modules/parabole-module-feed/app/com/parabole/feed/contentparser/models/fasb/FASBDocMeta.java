package com.parabole.feed.contentparser.models.fasb;

import com.parabole.feed.contentparser.models.common.DocMetaInfo;

/**
 * Created by anish on 7/26/2016.
 */
public class FASBDocMeta extends DocMetaInfo {

    private String paraStartRegEx;

    private String paraIgnore;


    public String getParaStartRegEx() {
        return paraStartRegEx;
    }

    public void setParaStartRegEx(String paraStartRegEx) {
        this.paraStartRegEx = paraStartRegEx;
    }

    public String getParaIgnore() {
        return paraIgnore;
    }

    public void setParaIgnore(String paraIgnore) {
        this.paraIgnore = paraIgnore;
    }
}
