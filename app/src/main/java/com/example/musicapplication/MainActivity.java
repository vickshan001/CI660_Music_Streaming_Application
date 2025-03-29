package com.example.musicapplication;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.musicapplication.adapter.TabAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private TabAdapter tabAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPagerMain);
        tabLayout = findViewById(R.id.tabLayout);

        tabAdapter = new TabAdapter(this);
        viewPager.setAdapter(tabAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setIcon(R.drawable.baseline_library_music_24);
                tab.setText("Songs");
            } else if (position == 1) {
                tab.setIcon(R.drawable.baseline_music_note_24);
                tab.setText("Player");
            }else if (position == 2) {
                tab.setIcon(R.drawable.baseline_album_24);
                tab.setText("Album");
            }
        }).attach();
    }

}

