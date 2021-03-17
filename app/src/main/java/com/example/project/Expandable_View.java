package com.example.project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class Expandable_View extends AppCompatActivity {

    RecyclerView recyclerView ;
    List<Versions> versionsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expandable__view);

        recyclerView = findViewById(R.id.recyclerView);
        initData();
        setRecyclerView();
    }

    private void setRecyclerView() {
        versionsAdapter versionsAdapter = new versionsAdapter(versionsList);
        recyclerView.setAdapter(versionsAdapter);
        recyclerView.setHasFixedSize(true);
    }

    private void initData() {

        versionsList = new ArrayList<>();

        versionsList.add(new Versions("Is this ِAPP used outside Egypt? ","NO, its exclusive in Egypt."));
        versionsList.add(new Versions("Can I trust the Garage on my car?","Yes, the Garage can only opened by App users."));
        versionsList.add(new Versions("Is that a Free Service?","No, you should Pay before you go."));
        versionsList.add(new Versions("Is there will be another services soon?","Yes, we intend to add Gas and Cleaning Services. "));

    }
}
