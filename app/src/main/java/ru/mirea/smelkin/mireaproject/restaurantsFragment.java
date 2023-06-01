package ru.mirea.smelkin.mireaproject;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import ru.mirea.smelkin.mireaproject.databinding.FragmentRestaurantsBinding;

public class restaurantsFragment extends Fragment {


    private FragmentRestaurantsBinding binding;
    private View root;
    private MapView mapView = null;
    private MyLocationNewOverlay locationNewOverlay;
    private static final int REQUEST_CODE_PERMISSION = 200;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRestaurantsBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        Configuration.getInstance().load(root.getContext().getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(root.getContext().getApplicationContext()));

        mapView = binding.mapView;
        mapView.setZoomRounding(true);
        mapView.setMultiTouchControls(true);

        CompassOverlay compassOverlay = new CompassOverlay(root.getContext().getApplicationContext(), new InternalCompassOrientationProvider(root.getContext().getApplicationContext()), mapView);
        compassOverlay.enableCompass();
        mapView.getOverlays().add(compassOverlay);


        int cOARSE_LOCATION = ContextCompat.checkSelfPermission(root.getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int fINE_LOCATION = ContextCompat.checkSelfPermission(root.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (cOARSE_LOCATION == PackageManager.PERMISSION_GRANTED || fINE_LOCATION == PackageManager.PERMISSION_GRANTED) {
            setMyLocation();
        }

        //маркер 1 - fарш
        Marker m1 = new Marker(mapView);
        m1.setPosition(new GeoPoint(55.738898, 37.411837));
        m1.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                Toast.makeText(root.getContext().getApplicationContext(),"#FARШ" + "\nЯрцевская ул., 19, Москва ТРЦ Кунцево Плаза", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        mapView.getOverlays().add(m1);
        m1.setIcon(ResourcesCompat.getDrawable(getResources(), org.osmdroid.library.R.drawable.osm_ic_follow_me_on, null));
        m1.setTitle("fарш");

        //маркер 2 - Сыто Пьяно
        Marker m2 = new Marker(mapView);
        m2.setPosition(new GeoPoint(55.763503, 37.606729));
        m2.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                Toast.makeText(root.getContext().getApplicationContext(),"Кафе" + "\nТверская ул., 17, Москва", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        mapView.getOverlays().add(m2);
        m2.setIcon(ResourcesCompat.getDrawable(getResources(), org.osmdroid.library.R.drawable.osm_ic_follow_me_on, null));
        m2.setTitle("Сыто Пьяно");

        //маркер 3-zotto
        Marker m3 = new Marker(mapView);
        m3.setPosition(new GeoPoint(56.029567, 35.504079));
        m3.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                Toast.makeText(root.getContext().getApplicationContext(),"ресторан" + "\n1-я Советская ул., 9, рабочий посёлок Шаховская", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        mapView.getOverlays().add(m3);
        m3.setIcon(ResourcesCompat.getDrawable(getResources(), org.osmdroid.library.R.drawable.osm_ic_follow_me_on, null));
        m3.setTitle("zotto");

        //маркер 4 - тануки
        Marker m4 = new Marker(mapView);
        m4.setPosition(new GeoPoint(55.795510, 37.705520));
        m4.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                Toast.makeText(root.getContext().getApplicationContext(),"японский ресторан" + "\nПреображенская ул., 5/7, Москва", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        mapView.getOverlays().add(m4);
        m4.setIcon(ResourcesCompat.getDrawable(getResources(), org.osmdroid.library.R.drawable.osm_ic_follow_me_on, null));
        m4.setTitle("тануки");

        //маркер 5 - Harat's pub
        Marker m5 = new Marker(mapView);
        m5.setPosition(new GeoPoint(55.738407, 37.413191));
        m5.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                Toast.makeText(root.getContext().getApplicationContext(),"паб\n" + "Ярцевская ул., 22, стр. 1, Москва", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        mapView.getOverlays().add(m5);
        m5.setIcon(ResourcesCompat.getDrawable(getResources(), org.osmdroid.library.R.drawable.osm_ic_follow_me_on, null));
        m5.setTitle("Harat's pub");


        return root;
    }

    protected void setMyLocation()
    {
        locationNewOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(root.getContext().getApplicationContext()), mapView);
        locationNewOverlay.enableMyLocation();
        mapView.getOverlays().add(this.locationNewOverlay);
        locationNewOverlay.runOnFirstFix(new Runnable() {
            public void run() {

                try {
                    double latitude = locationNewOverlay.getMyLocation().getLatitude();
                    double longitude = locationNewOverlay.getMyLocation().getLongitude();
                    Log.d("coord", String.valueOf(latitude));
                    Log.d("coord", String.valueOf(longitude));

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            IMapController mapController = mapView.getController();
                            mapController.setZoom(15.0);
                            GeoPoint startPoint = new GeoPoint(latitude, longitude);
                            mapController.setCenter(startPoint);
                        }
                    });
                }
                catch (Exception e) {}
            }
        });
    }
}