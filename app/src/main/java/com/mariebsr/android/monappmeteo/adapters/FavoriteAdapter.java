package com.mariebsr.android.monappmeteo.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.mariebsr.android.monappmeteo.R;
import com.mariebsr.android.monappmeteo.models.City;
import com.mariebsr.android.monappmeteo.utils.Util;

import java.util.ArrayList;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<City> mCities;


    public FavoriteAdapter(Context mContext, ArrayList<City> mCities) {
        this.mContext = mContext;
        this.mCities = mCities;
    }

    // Classe holder qui contient la vue d’un item
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextViewCityName;
        public TextView mTextViewCityDesc;
        public TextView mTextViewCityTemp;
        public ImageView mImageView;
        public City mCity;


        public ViewHolder(View view) {
            super(view);
            //action sur le long clic
            view.setOnLongClickListener(mOnLongClickListener);
            mImageView = (ImageView) view.findViewById(R.id.image_view_city_weather);
            mTextViewCityDesc = (TextView) view.findViewById(R.id.text_view_city_desc);
            mTextViewCityTemp = (TextView) view.findViewById(R.id.text_view_city_temp);
            mTextViewCityName = (TextView) view.findViewById(R.id.text_view_city_name);
        }

        //ce qui se passe quand on fait un clic long
        private View.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {

                //nouvelle fenetre de dialogue s'ouvre
                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                //un message s'affiche
                builder.setMessage("Supprimer " + mCity.mName + " ?");
                //un bouton OK s'affiche
                //si on clic dessus, il efface la ville
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        mCities.remove(mCity);
                        Util.saveFavouriteCities(mContext,mCities);
                        notifyDataSetChanged();
                        //notifyItemRemoved(position);
                        //notifyItemRangeChanged(position, mCities.size());

                    }
                });
                //un bouton annuler s'affiche, si on clic dessus il ne se passe rien
                builder.setNegativeButton(android.R.string.cancel, null);

                builder.create().show();
                return false;
            }
        };

    }

    @Override
    public FavoriteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite_city, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        City city = mCities.get(position);
        holder.mTextViewCityName.setText(city.mName);
        holder.mTextViewCityDesc.setText(city.mDescription);
        holder.mTextViewCityTemp.setText(city.mTemperature);
        holder.mImageView.setImageResource(city.mWeatherIcon);
        //reference de la ville qui servira pour les clics long et court
        holder.mCity = city;

    }

    @Override
    public int getItemCount() {
        return mCities.size();
    }


}