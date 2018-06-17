package com.hackaton.aplikacja.pckg;


import com.here.android.mpa.mapping.MapMarker;

public class CustomMarkerMap {
    private MapMarker markerMap;
    private int category;
    private String desc, id;

    public CustomMarkerMap(MapMarker markerMap, int category, String desc, int id) {
        this.markerMap = markerMap;
        this.category = category;
        this.desc = desc;
    }

    public MapMarker getMarkerMap() {
        return markerMap;
    }

    public int getCategory() {
        return category;
    }

    public String getDesc() {
        return desc;
    }

    public int getId() {
        return Integer.parseInt(id);
    }

    public void setId(String id) {
        this.id = id;
    }
}
