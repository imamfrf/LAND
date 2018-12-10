package com.papb.imamfrf.land;
public class List_Item_Aktivitas {

    private String hari, jenis, tglAwal, tglAkhir, next;
    //private int imgId;

    //constructor item yang akan dibuat


    public List_Item_Aktivitas(String hari, String jenis, String tglAwal, String tglAkhir, String next) {
        this.hari = hari;
        this.jenis = jenis;
        this.tglAwal = tglAwal;
        this.tglAkhir = tglAkhir;
        this.next = next;
    }

    public String getNoOrder() {
        return hari;
    }

    public String getJenis() {
        return jenis;
    }

    public String getTglAwal() {
        return tglAwal;
    }

    public String getTglAkhir() {
        return tglAkhir;
    }

    public String getNext() {
        return next;
    }
}

