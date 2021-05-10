package com.mariebsr.android.monappmeteo.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mariebsr.android.monappmeteo.R;
import com.mariebsr.android.monappmeteo.activities.FavoriteActivity;
import com.mariebsr.android.monappmeteo.models.City;
import com.mariebsr.android.monappmeteo.utils.Util;

import org.json.JSONException;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private FloatingActionButton mFloatingButtonFavorite;
    //private EditText mEditTextMessage;

    private LinearLayout mLinearLayoutMain;
    private TextView mTextViewNoConnection;
    private TextView mTextViewError;
    private TextView mTextViewCity;
    private TextView mTextViewDetails;
    private TextView mTextViewCurrentTemperature;
    private ImageView mImageViewWeatherIcon;

    private Context mContext;
    private OkHttpClient mOkHttpClient;
    private Handler mHandler;
    private City mCurrentCity;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private Location mCurrentLocation;
    final private int REQUEST_CODE = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("TAG", "MainActivity: onCreate()");

        mContext = this;
        mHandler = new Handler();
        mLinearLayoutMain = (LinearLayout) findViewById(R.id.linear_layout_current_city);
        mTextViewNoConnection = (TextView) findViewById(R.id.text_view_no_connection);
        mTextViewError = (TextView) findViewById(R.id.text_view_error);
        mFloatingButtonFavorite = (FloatingActionButton) findViewById(R.id.floating_action_button_favorite);
        //mEditTextMessage = (EditText) findViewById(R.id.edit_text_message);
        mTextViewCity = (TextView) findViewById(R.id.text_view_city_name);
        mTextViewDetails = (TextView) findViewById(R.id.text_view_city_desc);
        mTextViewCurrentTemperature = (TextView) findViewById(R.id.text_view_city_temp);
        mImageViewWeatherIcon = (ImageView) findViewById(R.id.image_view_city_weather);

        mFloatingButtonFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, FavoriteActivity.class);
                //intent.putExtra(Util.KEY_MESSAGE, mEditTextMessage.getText().toString());
                startActivity(intent);
            }
        });

        initLocationListener();

        //connexion internet
        if (Util.isActiveNetwork(mContext)) {
            Log.d("TAG", "Je suis connecté");

            //lancement geolocalisation
            updateWeatherDataCoordinatesFromMyLocation();


                // Location location = new Location("dummyprovider");
            //location.setLongitude(0.67);
            //location.setLatitude(47.42);
           // callAPI(location);
        } else{
        Log.d("TAG", "Je ne suis pas connecté");
        updateViewNoConnection();
    }

}

    private void initLocationListener() {
        mLocationListener = new LocationListener() {
// quand le GPS a trouvé une position, il déclanche méthode cidessous
            @Override
            public void onLocationChanged(Location location) {

                mCurrentLocation = location;

                Log.d("lol", "onLocationChanged: " + location);


                callAPI(location);

                //arrete l'appel en continu au GPS
                mLocationManager.removeUpdates(mLocationListener);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
            }
        };

    }

    private void updateWeatherDataCoordinates() {
        //String[] params = {String.valueOf(LAT), String.valueOf(LNG)};
        String[] params = {String.valueOf(mCurrentLocation.getLatitude()), String.valueOf(mCurrentLocation.getLongitude())};


        String s = String.format(Util.OPEN_WEATHER_MAP_API_COORDINATES, params);
        Request request = new Request.Builder().url(s).build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                final String stringJson = response.body().string();

                if (response.isSuccessful() && Util.isSuccessful(stringJson)) {

                    mHandler.post(new Runnable() {
                        public void run() {
                            renderCurrentWeather(stringJson);
                        }
                    });
                } else {
                    mHandler.post(new Runnable() {
                        public void run() {
                            updateViewError();
                        }
                    });
                }
            }
        });
    }

    public void updateViewNoConnection() {
        mLinearLayoutMain.setVisibility(View.INVISIBLE);
        mFloatingButtonFavorite.setVisibility(View.INVISIBLE);
        mTextViewNoConnection.setVisibility(View.VISIBLE);
    }

    public void updateViewError() {
        mLinearLayoutMain.setVisibility(View.INVISIBLE);
        mFloatingButtonFavorite.setVisibility(View.INVISIBLE);
        mTextViewError.setVisibility(View.VISIBLE);
    }

    public void callAPI( Location location) {
        mOkHttpClient = new OkHttpClient();
        String URL = "http://api.openweathermap.org/data/2.5/weather?lat="+ location.getLatitude()+ "&lon="+location.getLongitude()+ "&units=metric&lang=fr&appid=c6fec219b455eb8944e301f190bf5554";
        Request request = new Request.Builder().url(URL).build();
        //Request request1 = new Request.Builder().url("http://api.openweathermap.org/data/2.5/weather?lat=47.42&lon=0.67&units=metric&lang=fr&appid=c6fec219b455eb8944e301f190bf5554").build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String stringJson = response.body().string();
                    Log.d("TAG", stringJson);

                    mHandler.post(new Runnable() {
                        public void run() {

                            renderCurrentWeather(stringJson);

                        }
                    });

                } else{
                    mHandler.post(new Runnable() {
                        public void run() {
                            updateViewError();
                        }
                    });
                }
            }
        });
    }

    private void renderCurrentWeather(String jsonString) {

        try {
            mCurrentCity = new City(jsonString);

            mTextViewCity.setText(mCurrentCity.mName.toUpperCase(Locale.US));
            mTextViewDetails.setText(mCurrentCity.mDescription);
            mTextViewCurrentTemperature.setText(mCurrentCity.mTemperature);
            mImageViewWeatherIcon.setImageResource(mCurrentCity.mWeatherResIconWhite);

            mLinearLayoutMain.setVisibility(View.VISIBLE);
            mFloatingButtonFavorite.setVisibility(View.VISIBLE);


        } catch (JSONException e) {
            updateViewError();
        }
    }




    protected void onDestroy() {

        super.onDestroy();
        Log.d("TAG", "MainActivity: onDestroy()");
    }

    protected void onStart() {

        super.onStart();
        Log.d("TAG", "MainActivity: onStart()");
    }

    protected void onRestart() {

        super.onRestart();
        Log.d("TAG", "MainActivity: onRestart()");
    }

    protected void onResume() {

        super.onResume();
        Log.d("TAG", "MainActivity: onResume()");
    }

    protected void onPause() {

        super.onPause();
        Log.d("TAG", "MainActivity: onPause()");
    }

    protected void onStop() {

        super.onStop();
        Log.d("TAG", "MainActivity: onStop()");
    }

    public void updateWeatherDataCoordinatesFromMyLocation() {

        //si permission not granted, pop up qui demande si on accepte
        //l'OS nous donne la réponse, appelle onRequestPermissionsResult
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE);
        } else {
            mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
// si ok, le listener nous fournit l'information
            //GPS provider pour émulateur, sinon Network provider
            //FINE au lieu de COARSE


            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    updateWeatherDataCoordinatesFromMyLocation();
                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "Location Permission Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}







