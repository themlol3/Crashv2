package com.example.test2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class ScoreBoard extends AppCompatActivity implements OnMapReadyCallback {

    private ImageView returnButton;
    private GoogleMap map;
    private LinearLayout scLayout;
    private LatLng afeka;
    private Polyline line;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_board);

        returnButton = findViewById(R.id.return2);
        scLayout = findViewById(R.id.scLayout);


        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        afeka = new LatLng(32.120998, 34.805779);

        SharedPreferences prefs = getSharedPreferences("CrashInfo", MODE_PRIVATE);
        String highlights = prefs.getString("highlights", "");
        String player = null;
        int distance = 0;

        if (highlights != "") {
            ArrayList<String> list = sortHighLights(highlights);

            int size = list.size();
            if(size > 10)
            {
                size = 10;
            }

            for (int i = 0; i < size; i++) {
                TextView nameTV = new TextView(this);
                TextView score = new TextView(this);
                LinearLayout newLayout = new LinearLayout(this);

                newLayout.setGravity(Gravity.CENTER);

                nameTV.setTextColor(Color.WHITE);
                nameTV.setTextSize(20);
                nameTV.setPadding(10, 10, 10, 10);
                score.setTextColor(Color.WHITE);
                score.setTextSize(20);
                score.setPadding(10, 10, 10, 10);
                nameTV.setText(i + 1 + ". " + list.get(i).split("::")[0]);
                String scoreText = list.get(i).split("::")[1];
                score.setText(scoreText);
                score.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goToNewMarker(Integer.parseInt(scoreText));
                    }
                });
                newLayout.addView(nameTV);
                newLayout.addView(score);
                scLayout.addView(newLayout);
            }
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.addMarker(new MarkerOptions().position(afeka).title("Afeka"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(afeka, 12));
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    private void goToNewMarker(int distance)
    {
        if(line != null)
        {
            line.remove();
        }
        LatLng marketLat = new LatLng(afeka.latitude, getLongitudeByMeters(afeka.longitude,distance));
         line = map.addPolyline(new PolylineOptions().add(afeka, marketLat)
                        .width(5).color(Color.RED));

        CameraPosition cameraPosition = new CameraPosition.Builder().target(
                marketLat).zoom(12).build();

        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private double getLongitudeByMeters(double lng, int distance)
    {
        return lng+distance*0.00001; //Some fake formula to make it look realistic.
    }


    private ArrayList sortHighLights(String highlights) {
        String[] split = highlights.split(",");
        int size = split.length;
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < size; i++) {
            list.add(split[i]);
        }
        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String p1, String p2) {
                Integer a = Integer.parseInt(p1.replaceAll("[^0-9]", ""));
                Integer b = Integer.parseInt(p2.replaceAll("[^0-9]", ""));
                return b.compareTo(a);
            }
        });

        return list;
    }

}