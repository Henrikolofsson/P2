package Fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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
    private GoogleMap map;
    private ArrayList<Member> members;
    private Button btnLeaveGroup;
    private String userName;
    private Handler handler = new Handler(Looper.getMainLooper());
    private LatLng latLng;
    private Marker marker;

    public MapFragment() {
        // Required empty public constructor
    }

    //controller, init, onCreateView, membersset, membersset, onviewcreated, onmapready

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
        Log.d("HAPPENING:1","HAPPENING:1");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,      //4
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        initializeComponents(view);
        Log.d("HAPPENING:","HAPPENING:2");
        return view;
    }

    public void updateMarkers(){
        setMapMarkers();
        Log.d("HAPPENING:","HAPPENING:3");
    }

    private void setMapMarkers(){            //3
        handler.post(new Runnable() {
            @Override
            public void run() {
               // googleMap.clear();
                Log.d("HAPPENING:","HAPPENING:4");

                if(map==null){
                    Log.d("whythefuck","isthisfuckernull");
                }

                    for (int i = 0; i < members.size(); i++) {
                        Log.d("Checking la", members.get(i).getLatitude() +" " + members.get(i).getLongitude());
                        latLng = new LatLng(members.get(i).getLongitude(), members.get(i).getLatitude());
                       // marker = map.addMarker(new MarkerOptions().position(latLng).title(members.get(i).getName()));
                       // CameraPosition camPos = new CameraPosition.Builder().target(new LatLng(members.get(i).getLatitude(), members.get(i).getLongitude())).zoom(15).build();
                       // map.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));

                        map.addMarker(new MarkerOptions().position(latLng).title(members.get(i).getName()));
                        CameraPosition campos = new CameraPosition.Builder().target(latLng).zoom(15).build();
                        map.animateCamera(CameraUpdateFactory.newCameraPosition(campos));
                        //map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        Log.d("LATLONG", "LATITUDE = " + Double.toString(latLng.latitude) + " LONGITUDE = " + Double.toString(latLng.longitude));

                    }

                   // for(int i = 0; i < members.size(); i++){
                   //     LatLng sydney = new LatLng(-33.852, 151.211);
                   //     map.addMarker(new MarkerOptions().position(sydney)
                   //             .title("Marker in Sydney"));
                  //      map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                //    }
                }
        });
    }

    private void initializeComponents(View view){           //5
        mapView = view.findViewById(R.id.mapView);
        btnLeaveGroup = view.findViewById(R.id.btnLeaveGroup);
        btnLeaveGroup.setOnClickListener(new LeaveGroupButtonListener());
        Log.d("HAPPENING:","HAPPENING:5");
    }

    public void setController(Controller controller){   //1
        this.controller = controller;
        Log.d("HAPPENING:","HAPPENING:6");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
            this.map = googleMap;
            Log.d("HAPPENING:", "HAPPENING:11");
            setMapMarkers();
    }

    public void setMembers(ArrayList<Member> members){  //2
        this.members = members;
        for(int i = 0; i < members.size(); i++){
            Log.d("HAPPENING:","HAPPENING:8");
        }
    }

    public void setUser(String username){
        this.userName = username;
        Log.d("HAPPENING:","HAPPENING:9");
    }

    private class LeaveGroupButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            controller.unregister();
            controller.setStartFragment();
            Log.d("HAPPENING:","HAPPENING:10");
        }
    }
}
