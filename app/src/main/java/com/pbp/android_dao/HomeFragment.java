package com.pbp.android_dao;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;

import com.google.android.material.navigation.NavigationBarView;
import com.pbp.android_dao.entity.AppDatabase;
import com.pbp.android_dao.entity.Gedung;
import com.pbp.android_dao.entity.GedungWithRuangans;
import com.pbp.android_dao.entity.Ruangan;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    AppDatabase db;
    private Spinner spinner;

    public HomeFragment(AppDatabase db) {
        this.db = db;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        spinner = (Spinner) getView().findViewById(R.id.spinnerGedung);
        loadGedungToSpinner();
        setDaftarRuangBySpinnerSelect("Semua Gedung");
    }

    private void loadGedungToSpinner() {
        System.out.println("load spinner");
        AsyncTask.execute(new Runnable() {
            List<Gedung> allGedung;
            @Override
            public void run() {
                allGedung = db.gedungDAO().getAll();
                allGedung.add(0, new Gedung("All", "Semua Gedung"));

                // Create spinner with all available gedung
                // Create an ArrayAdapter using the string array and a default spinner layout
                ArrayAdapter<Gedung> adapter = new ArrayAdapter<Gedung>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, allGedung);
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // Apply the adapter to the spinner
                spinner.setAdapter(adapter);
            }
        });
    }

    private void setDaftarRuangBySpinnerSelect(String gedungName) {
        AsyncTask.execute(new Runnable() {
            List<GedungWithRuangans> gedungWithRuangans;
            ArrayList<Ruangan> ruangans = new ArrayList<>();

            @Override
            public void run() {
                // Fetch ruangan from db
                if (gedungName.equals("Semua Gedung")) {
                    gedungWithRuangans = db.gedungDAO().getAllGedungWithRuangan();
                } else {
                    Gedung currentGedung = db.gedungDAO().findByName(gedungName);
                    gedungWithRuangans = db.gedungDAO().getGedungWithRuangan(currentGedung.getKodeGedung());
                }

                // Append every ruangan to ArrayList<Ruangan>
                for (GedungWithRuangans x : gedungWithRuangans) {
                    ruangans.addAll(x.ruangans);
                }

                // Insert ruangan to daftar ruang
                if (!ruangans.isEmpty()) {
                    ListView daftarRuangView = (ListView) getView().findViewById(R.id.daftarRuangLayout);
                    RuanganListItemAdapter adapter = new RuanganListItemAdapter(ruangans, getActivity().getApplicationContext());
                    daftarRuangView.setAdapter(adapter);
                }

//                // Debug purpose
//                for (Ruangan x : ruangans) {
//                    System.out.println(x.getKodeRuangan() + ": " + x.getNama());
//                }
            }
        });
    }
}