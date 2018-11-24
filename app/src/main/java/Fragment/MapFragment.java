package Fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import Entities.Member;
import henrik.mau.p2.Controller;
import henrik.mau.p2.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {
    private Controller controller;
    private MapView mapView;
    private GoogleMap googleMap;
    private ArrayList<Member> members;
    private Button btnLeaveGroup;
    private String userName;
    private Handler handler = new Handler(Looper.getMainLooper());

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        initializeComponents(view);
        return view;
    }

    private void initializeComponents(View view){
        mapView = view.findViewById(R.id.mapView);
        btnLeaveGroup = view.findViewById(R.id.btnLeaveGroup);
    }

    public void setController(Controller controller){
        this.controller = controller;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    public void setMembers(ArrayList<Member> members){
        this.members = members;
    }

    public void setUser(String username){
        this.userName = username;
    }
}
