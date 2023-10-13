package ru.mirea.smelkin.mireaproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import ru.mirea.smelkin.mireaproject.databinding.FragmentAppListBinding;
import ru.mirea.smelkin.mireaproject.databinding.FragmentAudioBinding;

public class AppListFragment extends Fragment {
    private FragmentAppListBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAppListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        List<PackageInfoStruct> apps = getInstalledApps(true);

        ArrayList<HashMap<String, Object>> arrayList = new ArrayList<>();
        for (int i = 0; i < apps.size(); i++) {
            HashMap<String, Object> sensorTypeList = new HashMap<>();
            sensorTypeList.put("Name", apps.get(i).appname);
            sensorTypeList.put("Value", apps.get(i).pname);
            arrayList.add(sensorTypeList);
        }


        SimpleAdapter mHistory =
                new SimpleAdapter(getContext(), arrayList, android.R.layout.simple_list_item_2,
                        new String[]{"Name", "Value"},
                        new int[]{android.R.id.text1, android.R.id.text2});

        binding.listView.setAdapter(mHistory);

        return root;
    }

    private ArrayList<PackageInfoStruct> getInstalledApps(boolean getSysPackages) {

        @SuppressLint("QueryPermissionsNeeded") List<PackageInfo> packs = getActivity().getPackageManager().getInstalledPackages(0);
        ArrayList<PackageInfoStruct> res = new ArrayList<PackageInfoStruct>();

        for(int i=0;i < packs.size();i++) {
            PackageInfo p = packs.get(i);
            if ((!getSysPackages)) {
                continue ;
            }
            PackageInfoStruct newInfo = new PackageInfoStruct();
            newInfo.appname = p.applicationInfo.loadLabel(getActivity().getPackageManager()).toString();
            newInfo.pname = p.packageName;
            newInfo.versionName = p.versionName;
            newInfo.versionCode = p.versionCode;
            res.add(newInfo);
        }
        return res;
    }
}