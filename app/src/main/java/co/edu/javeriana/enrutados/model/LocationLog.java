package co.edu.javeriana.enrutados.model;

import org.json.JSONException;
import org.json.JSONObject;

public class LocationLog {
    private String date;
    private String lat;
    private String lng;

    public LocationLog(String date, String lat, String lng) {
        this.date = date;
        this.lat = lat;
        this.lng = lng;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();

        try {
            obj.put("latitud", getLat());
            obj.put("longitud", getLng());
            obj.put("fecha", getDate());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj;
    }
}
