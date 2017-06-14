package com.genix.foodgenix;

import javax.crypto.spec.DESKeySpec;

/**
 * Created by Anonymous on 6/14/2017.
 */

public class Makanan {
    public String namaMakanan;
    public String descMakanan;
    public String hargaMakanan;
    public boolean recommended;
    public int id;

    public Makanan(int id,String nama, String desc, String harga, boolean recommended) {
        this.namaMakanan = nama;
        this.descMakanan = desc;
        this.hargaMakanan = harga;
        this.recommended = recommended;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNamaMakanan(String namaMakanan) {
        this.namaMakanan = namaMakanan;
    }

    public void setHargaMakanan(String hargaMakanan) {
        this.hargaMakanan = hargaMakanan;
    }

    public void setRecommended(boolean recommended) {
        this.recommended = recommended;
    }

    public void setDescMakanan(String descMakanan) {
        this.descMakanan = descMakanan;
    }

    public String getHargaMakanan() {
        return hargaMakanan;
    }

    public String getNamaMakanan() {
        return namaMakanan;
    }

    public String getDescMakanan() {
        return descMakanan;
    }

    public boolean getRecommended() {
        return recommended;
    }
}

