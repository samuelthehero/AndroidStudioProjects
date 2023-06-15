package com.example.monitorterremotos.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Data {

    private int count, status;
    @SerializedName("features")
    private List<Terremoto> features;
    private List<Terremoto.Properties> properties;
    private List<Terremoto.Geometry> geometryList;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<Terremoto> getFeatures() {
        return features;
    }

    public void setFeatures(List<Terremoto> features) {
        this.features = features;
    }

    public List<Terremoto.Properties> getProperties() {
        return properties;
    }

    public void setProperties(List<Terremoto.Properties> properties) {
        this.properties = properties;
    }

    public List<Terremoto.Geometry> getGeometryList() {
        return geometryList;
    }

    public void setGeometryList(List<Terremoto.Geometry> geometryList) {
        this.geometryList = geometryList;
    }
}