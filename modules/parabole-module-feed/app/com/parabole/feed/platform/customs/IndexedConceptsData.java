package com.parabole.feed.platform.customs;

import java.util.List;

/**
 * Created by Sagir on 21-07-2016.
 */
public class IndexedConceptsData implements IndexedData {

    private String uriKey;
    private List<String> listedItems;


    public IndexedConceptsData() {

    }

    public void setItems(String uriKey, List<String> listedItems) {
        this.uriKey = uriKey;
        this.listedItems = listedItems;
    }

    public IndexedConceptsData(String uriKey, List<String> listedItems) {
        this.uriKey = uriKey;
        this.listedItems = listedItems;
    }

    public String getUriKey() {
        return uriKey;
    }

    public void setUriKey(String uriKey) {
        this.uriKey = uriKey;
    }

    public List<String> getListedItems() {
        return listedItems;
    }

    public void setListedItems(List<String> listedItems) {
        this.listedItems = listedItems;
    }
}
