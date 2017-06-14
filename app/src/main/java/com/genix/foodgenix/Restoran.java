package com.genix.foodgenix;

/**
 * Created by Anonymous on 6/13/2017.
 */

public class Restoran {

    public String namaRestoran;
    public String alamatRestoran;
    public String img;
    public float rating;
    public int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Restoran(int id,String nama, String alamat, String img, float rating) {
        this.namaRestoran = nama;
        this.alamatRestoran = alamat;
        this.img = img;
        this.rating = rating;
        this.id = id;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setNamaRestoran(String namaRestoran) {
        this.namaRestoran = namaRestoran;
    }

    public void setAlamatRestoran(String alamatRestoran) {
        this.alamatRestoran = alamatRestoran;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getNamaRestoran() {
        return namaRestoran;
    }

    public String getAlamatRestoran() {
        return alamatRestoran;
    }

    public String getImg() {
        return img;
    }
}
