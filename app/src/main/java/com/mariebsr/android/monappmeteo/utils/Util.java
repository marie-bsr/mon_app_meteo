package com.mariebsr.android.monappmeteo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.mariebsr.android.monappmeteo.R;
import com.mariebsr.android.monappmeteo.models.City;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Util {
    public static final String KEY_MESSAGE = "key_message";
    private static final String PREFS_NAME = "";
    private static final String PREFS_FAVORITE_CITIES = "";
    public static final String OPEN_WEATHER_MAP_API_COORDINATES = "http://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&units=metric&lang=fr&appid=01897e497239c8aff78d9b8538fb24ea";
    public static final String OPEN_WEATHER_MAP_API_CITY_NAME = "http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&lang=fr&appid=01897e497239c8aff78d9b8538fb24ea";
    public static final String OPEN_WEATHER_MAP_API_FAVOURITE = "http://api.openweathermap.org/data/2.5/group?id=%s&units=metric&lang=fr&appid=01897e497239c8aff78d9b8538fb24ea";
    public static final String OPEN_WEATHER_MAP_API_FORECAST = "http://api.openweathermap.org/data/2.5/forecast/daily?id=%s&units=metric&cnt=5&lang=fr&appid=01897e497239c8aff78d9b8538fb24ea";

    public static boolean isActiveNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }


    // méthodes de correspondance

    /*
     * Méthode qui initialise l'icon blanc présent dans la MainActivity
     * */
    public static int setWeatherIcon(int actualId, long sunrise, long sunset) {

        int id = actualId / 100;
        int icon = R.drawable.weather_sunny_white;

        if (actualId == 800) {
            long currentTime = new Date().getTime();

            if (currentTime >= sunrise && currentTime < sunset) {
                icon = R.drawable.weather_sunny_white;
            } else {
                icon = R.drawable.weather_clear_night_white;
            }
        } else {
            switch (id) {
                case 2:
                    icon = R.drawable.weather_thunder_white;
                    break;
                case 3:
                    icon = R.drawable.weather_drizzle_white;
                    break;
                case 7:
                    icon = R.drawable.weather_foggy_white;
                    break;
                case 8:
                    icon = R.drawable.weather_cloudy_white;
                    break;
                case 6:
                    icon = R.drawable.weather_snowy_white;
                    break;
                case 5:
                    icon = R.drawable.weather_rainy_white;
                    break;
            }
        }

        return icon;
    }

    /*
     * Méthode qui initialise l'icon gris présent dans le forecast et dans la liste des favoris.
     * */
    public static int setWeatherIcon(int actualId) {

        int id = actualId / 100;
        int icon = R.drawable.weather_sunny_grey;

        if (actualId != 800) {
            switch (id) {
                case 2:
                    icon = R.drawable.weather_thunder_grey;
                    break;
                case 3:
                    icon = R.drawable.weather_drizzle_grey;
                    break;
                case 7:
                    icon = R.drawable.weather_foggy_grey;
                    break;
                case 8:
                    icon = R.drawable.weather_cloudy_grey;
                    break;
                case 6:
                    icon = R.drawable.weather_snowy_grey;
                    break;
                case 5:
                    icon = R.drawable.weather_rainy_grey;
                    break;
            }
        }

        return icon;
    }


    public static String setDay(long ts) {

        String day = "";

        Date date = new Date(ts * 1000);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int numDay = calendar.get(Calendar.DAY_OF_WEEK);

        switch (numDay) {
            case Calendar.SUNDAY:
                day = "dim.";
                break;
            case Calendar.MONDAY:
                day = "lun.";
                break;
            case Calendar.TUESDAY:
                day = "mar.";
                break;
            case Calendar.WEDNESDAY:
                day = "mer.";
                break;
            case Calendar.THURSDAY:
                day = "jeu.";
                break;
            case Calendar.FRIDAY:
                day = "ven.";
                break;
            case Calendar.SATURDAY:
                day = "sam.";
                break;
        }

        return day;
    }

    public static String capitalize(String s) {
        if (s == null) return null;
        if (s.length() == 1) {
            return s.toUpperCase();
        }
        if (s.length() > 1) {
            return s.substring(0, 1).toUpperCase() + s.substring(1);
        }
        return "";
    }

    public static void saveFavouriteCities(Context context, ArrayList<City> cities) {
        JSONArray jsonArrayCities = new JSONArray();
        for (int i = 0; i < cities.size(); i++) {
            jsonArrayCities.put(cities.get(i).mStringJson);
        }
        SharedPreferences preferences = context.getSharedPreferences(Util.PREFS_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Util.PREFS_FAVORITE_CITIES, jsonArrayCities.toString());
        editor.apply();
    }

    public static ArrayList<City> initFavoriteCities(Context context) {
        ArrayList<City> cities = new ArrayList<>();
        SharedPreferences preferences = context.getSharedPreferences(Util.PREFS_NAME,
                Context.MODE_PRIVATE);


        try {
            JSONArray jsonArray = new JSONArray(preferences.getString(Util.PREFS_FAVORITE_CITIES,
                    ""));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObjectCity = new JSONObject(jsonArray.getString(i));
                City city = new City(jsonObjectCity.toString());
                cities.add(city);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return cities;
    }

    public static boolean isSuccessful(String stringJson) {

        try {
            JSONObject json = new JSONObject(stringJson);

            int cod = json.getInt("cod");
            if (cod != 200)
                return false;

        } catch (Exception e) {
            return false;
        }

        return true;
    }


}
