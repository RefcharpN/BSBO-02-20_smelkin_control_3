package ru.mirea.smelkin.mireaproject;

import android.content.Intent;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import ru.mirea.smelkin.mireaproject.databinding.FragmentPlayBinding;


public class play extends Fragment {

    private FragmentPlayBinding binding;
    private int play_status = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPlayBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        binding.left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(root, "предыдущая композиция", Toast.LENGTH_SHORT).show();
            }
        });

        binding.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(play_status == 0)
                {
                    Intent serviceIntent = new Intent(root.getContext(), MyServicePlayer.class);
                    ContextCompat.startForegroundService(root.getContext(), serviceIntent);
                    play_status = 1;
                    binding.play.setImageResource(R.drawable.baseline_stop_24);
                }
                else
                {
                    root.getContext().stopService(new Intent(root.getContext(), MyServicePlayer.class));
                    play_status = 0;
                    binding.play.setImageResource(R.drawable.ic_media_play);
                }

            }
        });

        binding.right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(root, "следующая композиция", Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }
}