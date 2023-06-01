package ru.mirea.smelkin.mireaproject;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.mirea.smelkin.mireaproject.databinding.FragmentCameraBinding;
import ru.mirea.smelkin.mireaproject.databinding.FragmentGyroBinding;

public class GyroFragment extends Fragment implements SensorEventListener {

    private FragmentGyroBinding binding;
    private TextView horizont_view;
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentGyroBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        horizont_view = binding.horizontView;

        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }


    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float valueAzimuth = event.values[0];
//            float valuePitch = event.values[1];
//            float valueRoll = event.values[2];

            int inclination = (int) Math.round(Math.toDegrees(Math.acos(valueAzimuth)));

            if (inclination < 25 || inclination > 155)
            {
                horizont_view.setText("не вертикально");
            }
            else
            {
                horizont_view.setText("вертикально");
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}