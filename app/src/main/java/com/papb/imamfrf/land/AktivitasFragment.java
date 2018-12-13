package com.papb.imamfrf.land;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AktivitasFragment extends android.support.v4.app.Fragment {

    public List<List_Item_Aktivitas> listItems = new ArrayList<List_Item_Aktivitas>();
    public RecyclerView recyclerView;
    public RecyclerView.Adapter adapter;
    private FirebaseDatabase db;
    private FirebaseAuth auth;
    ArrayList<String> tanggalSet;
    HashMap<String, String> pesanan = new HashMap<>();
    SharedPreferences sharedpreferences;
    boolean notif;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View inflate =inflater.inflate(R.layout.fragment_aktivitas,null);
        ((MainActivity)getActivity()).setActionBarTitle("Aktivitas");
        recyclerView = inflate.findViewById(R.id.recV_aktv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        db = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        listItems = new ArrayList<List_Item_Aktivitas>();

        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        notif = sharedpreferences.getBoolean("switch_notif", true);



        db.getReference("Pesanan").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot sn : dataSnapshot.getChildren()) {
                    String uid = sn.child("UID").getValue(String.class);
                    //retrieveTanggal("-LPHeF8T0evlSZofaFRa");
                    if (uid.equalsIgnoreCase(auth.getCurrentUser().getUid())) {
                        String tglAwal = sn.child("Tanggal").child("01-day").getValue(String.class);
                        String tglAkhir;
                        String jenis = sn.child("Jenis").getValue(String.class);
                        if (jenis.equals("1 Bulan")) {
                            tglAkhir = sn.child("Tanggal").child("04-day").getValue(String.class);
                        } else if (jenis.equals("2 Bulan")) {
                            tglAkhir = sn.child("Tanggal").child("08-day").getValue(String.class);
                        } else if (jenis.equals("3 Bulan")) {
                            tglAkhir = sn.child("Tanggal").child("12-day").getValue(String.class);
                        } else {
                            tglAkhir = "-";
                        }

                        String hari = "";
                        switch (sn.child("Hari").getValue(String.class)){
                            case "Senin" :
                                hari = "Monday";
                                break;
                            case "Selasa" :
                                hari = "Tuesday";
                                break;
                            case "Rabu" :
                                hari = "Wednesday";
                                break;
                            case "Kamis" :
                                hari = "Thursday";
                                break;
                            case "Jumat" :
                                hari = "Friday";
                                break;
                            case "Sabtu" :
                                hari = "Saturday";
                                break;
                            case "Minggu" :
                                hari = "Sunday";
                                break;
                        }

                        String next = "";
                        Date today = Calendar.getInstance().getTime();
                        Date date = null;
                        DataSnapshot dataSnapshot1 = sn.child("Tanggal");
                        for (DataSnapshot sn1 : dataSnapshot1.getChildren()) {
                            String strDate = sn1.getValue(String.class);
                            //Date date;
                            SimpleDateFormat format = new SimpleDateFormat("dd-MMMM-yyyy", Locale.ENGLISH);
                            try {
                                date = format.parse(strDate);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                           if (date.after(today)) {
                                next = (String) DateFormat.format("dd-MMMM-yyyy", date);
                                break;
                            }
                        }
                        Log.d("TES", next);
                        if (next.isEmpty()){
                            next = "COMPLETED";
                        }
                        //String today = (String) DateFormat.format("EEE", d);
                        listItems.add(new List_Item_Aktivitas(sn.child("Hari").getValue(String.class), jenis,
                                tglAwal, tglAkhir, next));

                        SimpleDateFormat dFormat = new SimpleDateFormat("EEEE");
                        String currentDay = dFormat.format(today);

                        if (notif == true){
                            try{
                                if (currentDay.equalsIgnoreCase(hari)){
                                    Intent intent = new Intent(getContext(), MainActivity.class);
                                    //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
                                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getActivity(), "land")
                                            .setContentIntent(pendingIntent)
                                            .setAutoCancel(true)
                                            .setSmallIcon(R.drawable.logo_land_mini)
                                            .setContentTitle("Hari Penjemputan Laundry")
                                            .setContentText("Hari ini laundry anda akan dijemput")
                                            .setLights(Color.RED, 1000, 300)
                                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                            .setVibrate(new long[]{100, 200, 300, 400, 500})
                                            .setDefaults(Notification.DEFAULT_VIBRATE)
                                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                                    NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                                    //Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                    notificationManager.notify(1, mBuilder.build());

                                }
                            }
                            catch (Exception e){

                            }

                        }

                    }
                }

                adapter = new aktivitasAdapter(listItems, getContext(), new aktivitasAdapter.OnItemClicked() {
                    @Override
                    public void onItemClick(int position) {

                    }
                });
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return inflate;
    }

}

