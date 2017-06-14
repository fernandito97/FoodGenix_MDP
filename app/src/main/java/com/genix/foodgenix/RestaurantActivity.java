package com.genix.foodgenix;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RestaurantActivity extends AppCompatActivity {
    @Bind(R.id.listViewMakanan)
    public RecyclerView lvMakanan;
    AdapterMakanan adapter;
    @Bind(R.id.txtNamaResto)
    TextView txtNamaResto;
    @Bind(R.id.txtAlamat)
    TextView txtAlamat;
    @Bind(R.id.ratingRestoran)
    RatingBar ratingRestoran;
    @Bind(R.id.ratingNow)
    RatingBar ratingNow;
    @Bind(R.id.imageView)
    ImageView imageView;
    @Bind(R.id.navigation)
    BottomNavigationView navigation;
    int restaurant_id=0;
    RelativeLayout menuLayout,profileLayout;
    ArrayList<Makanan> arrMenu = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        menuLayout = (RelativeLayout)findViewById(R.id.menuLayout);
        profileLayout= (RelativeLayout)findViewById(R.id.profileLayout);
        restaurant_id = getIntent().getExtras().getInt("restaurantid");
        if(SaveSharedPreferences.isRestaurant(getApplicationContext())){
            ratingNow.setVisibility(View.GONE);
        }
        ratingNow.setIsIndicator(false);
        ratingNow.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                //userno ,restaurantno
                HashMap<String,String> params = new HashMap<>();
                params.put("USER_NO",SaveSharedPreferences.getUserID(getApplicationContext()));
                params.put("RESTAURANT_NO",String.valueOf(restaurant_id));
                params.put("RATE",String.valueOf(ratingNow.getRating()));
                new rateTask().execute(params);
            }
        });
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.profile){
                    getDataRestaurant();
                    profileLayout.setVisibility(View.VISIBLE);
                    menuLayout.setVisibility(View.GONE);
                    return true;
                }
                else if(item.getItemId() == R.id.menu){
                    menuLayout.setVisibility(View.VISIBLE);
                    profileLayout.setVisibility(View.GONE);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    lvMakanan.setLayoutManager(mLayoutManager);
                    lvMakanan.setItemAnimator(new DefaultItemAnimator());
                    lvMakanan.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    return true;
                }
                return false;
            }
        });
        getDataRestaurant();

    }
    private void getDataRestaurant(){
        arrMenu.clear();

        String response = EDWSRequest.Request("GET","restaurant/findById/"+restaurant_id,new HashMap<String, String>());
        try {
            JSONObject nodeRoot = new JSONObject(response);
            JSONObject node = nodeRoot.getJSONObject("result");
            txtAlamat.setText(node.getString("ADDRESS"));
            txtNamaResto.setText(node.getString("NAME"));
            Picasso.with(getApplicationContext()).load(EDWSRequest.SERVER_URL+"uploads/restaurant/"+node.getString("BIO")).error(R.drawable.restaurant_512).into(imageView);
            JSONArray menuArray = node.getJSONArray("menu");
            for(int i=0;i<menuArray.length();i++){
                JSONObject food = menuArray.getJSONObject(i);
                arrMenu.add(new Makanan(Integer.parseInt(food.getString("NO")),food.getString("NAME"),food.getString("NOTE"),food.getString("PRICE"),food.getString("RECOMMENDED").equals("1")));
            }
            String responseRating = EDWSRequest.Request("GET","restaurant/rating/"+node.getString("NO"),new HashMap<String, String>());
            JSONObject nodeResponse = new JSONObject(responseRating);
            float rating = Float.parseFloat(nodeResponse.getString("Rating"));
            ratingRestoran.setRating(rating);
            adapter = new AdapterMakanan(arrMenu);
            adapter.notifyDataSetChanged();

            HashMap<String,String> param = new HashMap<>();
            param.put("USER_NO", SaveSharedPreferences.getUserID(getApplicationContext()));
            param.put("RESTAURANT_NO", restaurant_id+"");
            String responseGetrate = EDWSRequest.Request("GET","getrate",param);
            ratingNow.setRating(Float.parseFloat(new JSONObject(responseGetrate).getString("result")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
    private class rateTask extends AsyncTask<HashMap<String,String>,Void,String> {
        @Override
        protected String doInBackground(HashMap<String, String>... params) {
            String response = EDWSRequest.Request("POST","rate",params[0]);
            return response;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ratingNow.setEnabled(false);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ratingNow.setEnabled(true);
            Toast.makeText(getApplicationContext(),"Thanks 4 rate!",Toast.LENGTH_SHORT).show();
            getDataRestaurant();
        }
    }
}
