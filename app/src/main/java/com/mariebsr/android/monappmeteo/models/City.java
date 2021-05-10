package com.mariebsr.android.monappmeteo.models;

import android.location.Location;

import com.mariebsr.android.monappmeteo.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class City {

    public String mName;
    public String mDescription;
    public String mTemperature;
    public int mWeatherIcon;
    public int mIdCity;
    public double mLatitude;
    public double mLongitude;
    public int mWeatherResIconWhite;
    public int mWeatherResIconGrey;
    public String mStringJson;
    public String mCountry;

    public City(String mName, String mDescription, String mTemperature, int mWeatherIcon) {
        this.mName = mName;
        this.mDescription = mDescription;
        this.mTemperature = mTemperature;
        this.mWeatherIcon = mWeatherIcon;
    }

   //pour récupérer les infos du json, il faut bien le lire dans le navigateur
    //la structure est json objet qui contient, json objet ou tableau de json, qui contient int ou double ou string

    public City(String stringJson) throws JSONException {
        mStringJson = stringJson;
        JSONObject json = new JSONObject(stringJson);
        JSONObject details = json.getJSONArray("weather").getJSONObject(0);
        JSONObject main = json.getJSONObject("main");
        JSONObject coord = json.getJSONObject("coord");
        mIdCity = json.getInt("id");
        mName = json.getString("name");
        mCountry = json.getJSONObject("sys").getString("country");
        mTemperature = String.format("%.0f", main.getDouble("temp")) + " ℃";
        mDescription = Util.capitalize(details.getString("description"));
        mWeatherResIconWhite = Util.setWeatherIcon(details.getInt("id"), json.getJSONObject("sys").getLong("sunrise") * 1000, json.getJSONObject("sys").getLong("sunset") * 1000);
        mWeatherResIconGrey = Util.setWeatherIcon(details.getInt("id"));
        mLatitude = coord.getDouble("lat");
        mLongitude = coord.getDouble("lon");
    }





}
