package com.genix.foodgenix;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Anonymous on 6/13/2017.
 */

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.MyViewHolder>{
    ClickListener clickListener;
    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener {
        void ItemClicked(View v, int position);
    }

    public Context context;
    public ArrayList<Restoran> data;

    TextView txtNama, txtAlamat;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtNama;
        public TextView txtAlamat;
        public ImageView imageView;
        public RatingBar ratingBar;
        public MyViewHolder(View view) {
            super(view);
            this.txtNama = (TextView) view.findViewById(R.id.txtNamaResto);
            this.txtAlamat = (TextView) view.findViewById(R.id.txtAlamat);
            this.imageView = (ImageView)view.findViewById(R.id.imageView);
            this.ratingBar = (RatingBar)view.findViewById(R.id.ratingRestoran);
        }
    }
    public RestaurantAdapter(ArrayList<Restoran> data) {
        this.data = data;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.restaurant_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Restoran resto = data.get(position);
        holder.txtNama.setText(resto.getNamaRestoran());
        holder.txtAlamat.setText(resto.getAlamatRestoran());
        holder.ratingBar.setRating(resto.getRating());
        Picasso.with(holder.imageView.getContext()).load(EDWSRequest.SERVER_URL+"uploads/restaurant/"+resto.getImg()).error(R.drawable.restaurant_512).into(holder.imageView);
        final int dataposition = position;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.ItemClicked(v,dataposition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    /*
    @Nullable
    @Override
    public Restoran getItem(int position) {
        return data.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Restoran resto = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater
                    .from(getContext())
                    .inflate(R.layout.restaurant_list_row,parent, false);
        }

        txtNama = (TextView) convertView.findViewById(R.id.txtNamaResto);
        txtAlamat = (TextView) convertView.findViewById(R.id.txtAlamat);
        txtNama.setText(resto.getNamaRestoran());
        txtAlamat.setText(resto.getAlamatRestoran());
        
        RatingBar rating;
        rating = (RatingBar) convertView.findViewById(R.id.ratingRestoran);
        rating.setRating(Float.parseFloat("5"));

        Typeface custom_font = Typeface.createFromAsset(getContext().getAssets(), "fonts/Lobster.ttf");
        TextView txtNamaRestoran = (TextView) convertView.findViewById(R.id.txtNamaResto);
        txtNamaRestoran.setTypeface(custom_font);
        return convertView;

    }
    */
}
