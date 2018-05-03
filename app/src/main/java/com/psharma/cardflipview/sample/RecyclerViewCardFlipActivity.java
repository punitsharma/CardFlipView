package com.psharma.cardflipview.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class RecyclerViewCardFlipActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview_flip);
        List<FlipModel> list = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        for (int i = 0; i < 2; i++) {
            FlipModel model = new FlipModel();
            model.isFlipped = false;
            list.add(model);
        }
        recyclerView.setAdapter(new CardAdapter(list));
    }
}
