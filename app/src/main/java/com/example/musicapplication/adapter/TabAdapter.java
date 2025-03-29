package com.example.musicapplication.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.musicapplication.AlbumViewFragment;
import com.example.musicapplication.MusicPlayerFragment;
import com.example.musicapplication.SongsListFragment;

import java.util.ArrayList;
import java.util.List;

public class TabAdapter extends FragmentStateAdapter {
    private List<Fragment> fragments;
    private Bundle arguments;

    public TabAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        fragments = new ArrayList<>();
        fragments.add(new SongsListFragment());
        fragments.add(new MusicPlayerFragment());
        fragments.add(new AlbumViewFragment());
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = fragments.get(position);
        if (position == 1 || position == 2) {
            fragment.setArguments(arguments);
        }
        return fragment;
    }

    @Override
    public long getItemId(int position) {
        // Generate unique IDs for fragments based on position
        return fragments.get(position).hashCode();
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }

    public Fragment getFragment(int position) {
        return fragments.get(position);
    }

    public void replaceFragment(int position, Fragment fragment, Bundle arguments) {
        if (position >= 0 && position < fragments.size()) {
            fragments.set(position, fragment);
            fragment.setArguments(arguments);
            notifyDataSetChanged();
        }
    }

    public void updateMusicPlayerFragment(Bundle arguments) {
        this.arguments = arguments;
        notifyItemChanged(1);
    }
}
