package com.example.monitorterremotos.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Terremoto {

    @SerializedName("id")
    private String id;
    @SerializedName("properties")
    private Properties properties;
    @SerializedName("geometry")
    private Geometry geometry;

    private int image;

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public Geometry getGeomtery() {
        return geometry;
    }

    public void setGeomtery(Geometry geomtery) {
        this.geometry = geomtery;
    }

    public static class Properties{
        @SerializedName("place")
        private String place;
        @SerializedName("mag")
        private float magnitude;
        @SerializedName("time")
        private String time;

        public String getPlace() {
            return place;
        }

        public void setPlace(String place) {
            this.place = place;
        }

        public float getMagnitude() {
            return magnitude;
        }

        public void setMagnitude(float magnitude) {
            this.magnitude = magnitude;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

    }

    public static class Geometry{

        @SerializedName("coordinates")
        private List<Float> coordinates;

        public List<Float> getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(List<Float> coordinates) {
            this.coordinates = coordinates;
        }
    }
}
