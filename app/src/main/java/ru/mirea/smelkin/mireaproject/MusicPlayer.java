package ru.mirea.smelkin.mireaproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.mirea.smelkin.mireaproject.databinding.FragmentMusicPlayerBinding;

public class MusicPlayer extends Fragment {

    private FragmentMusicPlayerBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMusicPlayerBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        return root;
    }
}