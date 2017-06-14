package com.genix.foodgenix;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;


public class AdapterMakanan extends RecyclerView.Adapter<AdapterMakanan.MyViewHolder> {
    public Context context;
    public ArrayList<Makanan> data;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtNamaMakanan;
        public TextView txtHarga;
        public TextView txtDeskripsi;
        public ImageView imageView;
        public MyViewHolder(View view) {
            super(view);
            this.txtNamaMakanan = (TextView)view.findViewById(R.id.txtNamaMakanan);
            this.txtHarga = (TextView)view.findViewById(R.id.txtHarga);
            this.txtDeskripsi = (TextView)view.findViewById(R.id.txtDeskripsi);
        }
    }

    public AdapterMakanan(ArrayList<Makanan> data) {
        this.data = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.menu_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Makanan makanan = data.get(position);
        holder.txtNamaMakanan.setText(makanan.getNamaMakanan());
        holder.txtDeskripsi.setText(makanan.getDescMakanan());
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        Double rego = Double.parseDouble(makanan.getHargaMakanan());
        DecimalFormat dFormat = new DecimalFormat("####,###,###.00");
        holder.txtHarga.setText("Rp. "+dFormat.format(rego));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

}
