package com.mariebsr.android.monappmeteo.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.mariebsr.android.monappmeteo.R;
import com.mariebsr.android.monappmeteo.adapters.FavoriteAdapter;
import com.mariebsr.android.monappmeteo.models.City;
import com.mariebsr.android.monappmeteo.utils.Util;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FavoriteActivity extends AppCompatActivity {

    private TextView mTextViewMessage;
    private ArrayList<City> mCities;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private Context mContext;
    private OkHttpClient mOkHttpClient;
    private Handler mHandler;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext = this;
        mHandler = new Handler();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);


                View v = LayoutInflater.from(mContext).inflate(R.layout.dialog_add_favorite, null);
                final EditText editTextCity = (EditText) v.findViewById(R.id.edit_text_dialog);
                //ajout d'une vue
                builder.setView(v);
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //ce qui se passe quand on clic sur OK:

                        //City city = new City(editTextCity.getText().toString(), "Pluies modérées", "20°C", R.drawable.weather_rainy_grey);
                        //dans TP4, ici on fera un appel vers l'API
                    callAPI(editTextCity.getText().toString());

                    }
                });
                //pas de listener car on ne veut rien fait au clic sur ce bouton
                builder.setNegativeButton(android.R.string.cancel, null);

                builder.create().show();
            }
        });

        Log.d("TAG", "FavoriteActivity: onCreate()");

        /*
        mTextViewMessage = findViewById(R.id.text_view_message);
        Bundle extras = getIntent().getExtras();
        String strMessage = extras.getString(Util.KEY_MESSAGE);
        mTextViewMessage.setText( "Message : " + strMessage);
*/


        mCities = new ArrayList<>();
        mCities = Util.initFavoriteCities(mContext);
        /*
        City city1 = new City("Montréal", "Légères pluies", "22°C", R.drawable.weather_rainy_grey);
        City city2 = new City("New York", "Ensoleillé", "22°C", R.drawable.weather_sunny_grey);
        City city3 = new City("Paris", "Nuageux", "24°C", R.drawable.weather_foggy_grey);
        City city4 = new City("Toulouse", "Pluies modérées", "20°C", R.drawable.weather_rainy_grey);
        City city5 = new City("Los Angeles", "Nuageux", "24°C", R.drawable.weather_foggy_grey);

        mCities.add(city1);
        mCities.add(city2);
        mCities.add(city3);
        mCities.add(city4);
        mCities.add(city5);
*/

       mRecyclerView = findViewById(R.id.my_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new FavoriteAdapter(mContext, mCities);
        mRecyclerView.setAdapter(mAdapter);
    }


    public void callAPI( String cityName) {
        mOkHttpClient = new OkHttpClient();
        String URL = "http://api.openweathermap.org/data/2.5/weather?q="+ cityName + "&units=metric&lang=fr&appid=c6fec219b455eb8944e301f190bf5554";
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

                            renderFavoriteCity(stringJson);

                        }
                    });

                } else{
                    mHandler.post(new Runnable() {
                        public void run() {
                            //mettre un toast message d'erreur
                        }
                    });
                }
            }
        });
    }

    private void renderFavoriteCity(String jsonString) {

        try {
            City favCity = new City(jsonString);
            mCities.add(favCity);
            mAdapter.notifyDataSetChanged();
            Util.saveFavouriteCities(mContext,mCities);



        } catch (JSONException e) {
            //toast erreur ville introuvable
        }
    }


    protected void onDestroy() {

        super.onDestroy();
        Log.d("TAG", "FavoriteActivity: onDestroy()");
    }

    protected void onStart() {

        super.onStart();
        Log.d("TAG", "FavoriteActivity: onStart()");
    }
    protected void onRestart() {

        super.onRestart();
        Log.d("TAG", "FavoriteActivity: onRestart()");
    }
    protected void onResume() {

        super.onResume();
        Log.d("TAG", "FavoriteActivity: onResume()");
    }
    protected void onPause() {

        super.onPause();
        Log.d("TAG", "FavoriteActivity: onPause()");
    }
    protected void onStop() {

        super.onStop();
        Log.d("TAG", "FavoriteActivity: onStop()");
    }

}
