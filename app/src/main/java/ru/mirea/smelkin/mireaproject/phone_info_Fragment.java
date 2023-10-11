package ru.mirea.smelkin.mireaproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.mirea.smelkin.mireaproject.databinding.FragmentPhoneInfoBinding;
import android.provider.Settings.Secure;




public class phone_info_Fragment extends Fragment {
    private FragmentPhoneInfoBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentPhoneInfoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        String android_id = Secure.getString(getContext().getContentResolver(), Secure.ANDROID_ID);

        binding.textView8.setText(android_id);

        return root;
    }
}