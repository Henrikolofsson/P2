package henrik.mau.p2;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import Entities.Group;
import Entities.Member;
import Entities.User;
import Fragment.DataFragment;
import Fragment.StartFragment;
import Fragment.MapFragment;

public class Controller {
    private MainActivity mainActivity;
    private DataFragment dataFragment;
    private StartFragment startFragment;
    private MapFragment mapFragment;

    private static String IP = "195.178.227.53";
    private static int port = 8443;
    private boolean isConnected;
    private InetAddress inetAddress;
    private Socket connectedSocket;
    private DataOutputStream dos;
    private DataInputStream dis;

    private JSONArray jsonArray;
    private ArrayList<String> groups = new ArrayList<>();
    private ArrayList<String> members = new ArrayList<>();
    private ArrayList<Member> realMembers = new ArrayList<>();
    private String activeGroup;
    private String userName;
    private User user;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location lastLocation;
    private boolean registeredUser = false;
    private Member member;

    private Handler handler = new Handler(Looper.getMainLooper());

    private HashMap<String, ArrayList<Member>> memberHash = new HashMap<String, ArrayList<Member>>();
    private ArrayList<Member> tempArray;

    public Controller(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        initializeFragments();
        setFragment("StartFragment");
        connect();
        initializeLocation();
    }

    private void initializeFragments() {
        initializeDataFragment();
        initializeStartFragment();
        initializeMapFragment();
    }

    private void initializeLocation(){
        locationManager = (LocationManager) mainActivity.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocListener();
        positionTimer();
    }

    private void initializeDataFragment() {
        dataFragment = (DataFragment) mainActivity.getFragment("DataFragment");
        if (dataFragment == null) {
            dataFragment = new DataFragment();
            mainActivity.addFragment(dataFragment, "DataFragment");
            dataFragment.setActiveFragment("StartFragment");
        }
        dataFragment.setController(this);
    }

    private void initializeStartFragment() {
        startFragment = (StartFragment) mainActivity.getFragment("StartFragment");
        if (startFragment == null) {
            startFragment = new StartFragment();
        }
        startFragment.setController(this);
    }

    private void initializeMapFragment() {
        mapFragment = (MapFragment) mainActivity.getFragment("MapFragment");
        if (mapFragment == null) {
            mapFragment = new MapFragment();
        }
        mapFragment.setController(this);
    }

    public boolean onBackPressed() {
        String activeFragment = dataFragment.getActiveFragment();

        if (activeFragment.equals("StartFragment")) {
            return false;
        }

        switch (activeFragment) {
            case "MapFragment":
                setFragment("StartFragment");
                break;

        }
        return true;

    }

    private void setFragment(String tag) {
        switch (tag) {
            case "StartFragment":
                setFragment(startFragment, tag);
                break;
            case "MapFragment":
                setFragment(mapFragment, tag);
                break;
        }
    }

    private void setFragment(Fragment fragment, String tag) {
        mainActivity.setFragment(fragment, tag);
        dataFragment.setActiveFragment(tag);
    }

    public void setGroups(ArrayList<String> groups) {
        this.groups = groups;
    }

    public void setRealMembers(ArrayList<Member> members){
        this.realMembers = members;
    }

    public ArrayList<Member> getRealMembers(){
        return realMembers;
    }

    public void setActiveGroup(String activeGroup){
        this.activeGroup = activeGroup;
    }

    public String getActiveGroup(){
        return activeGroup;
    }

    public ArrayList<String> getGroups() {
        return groups;
    }

    public boolean isServicesOK(){
        Log.d("IsServiceOk", "Checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mainActivity);

        if(available == ConnectionResult.SUCCESS){
            Log.d("ServiceOK", "Service is right version");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d("ServiceNotOK", "Service resulted in error, but can't solve it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(mainActivity, available, 9001);
        } else {
            Toast.makeText(mainActivity, "You cant make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public void setLastLocation(Location location){
        this.lastLocation = location;
    }

    public Location getLastLocation(){
        return lastLocation;
    }

    public void setMapFragment(){
        setFragment("MapFragment");
    }

    public void setStartFragment(){
        setFragment("StartFragment");
    }

    public void updateMapMarkers(){
        mapFragment.setMembers(getRealMembers());
        mapFragment.updateMarkers();
    }


    //----------------------------------------------------------------------------------------

    public void connect() {
        InputListener inputListener = new InputListener();
        final Thread inputListenerThread = new Thread(inputListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    inetAddress = InetAddress.getByName(IP);
                    connectedSocket = new Socket(inetAddress, port);
                    dos = new DataOutputStream(connectedSocket.getOutputStream());
                    dis = new DataInputStream(connectedSocket.getInputStream());
                    isConnected = true;

                    if(connectedSocket != null){
                        inputListenerThread.start();
                        startTimer();
                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void register(final String groupName, final String userName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject json = new JSONObject();
                    json.put("type", "register");
                    json.put("group", groupName);
                    json.put("member", userName);
                    String jsonObject = json.toString();
                    dos.writeUTF(jsonObject);
                    dos.flush();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void unregister(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject json = new JSONObject();
                    json.put("type", "unregister");
                    json.put("id", user.getId());
                    Log.d("USERID2", json.toString()); //HÄR
                    dos.writeUTF(json.toString());
                    dos.flush();
                    memberHash.clear();
                    startFragment.unregistered();
                } catch(JSONException e){
                    e.printStackTrace();
                } catch(IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void getAvailableGroups(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject json = new JSONObject();
                    json.put("type", "groups");
                    String jsonObject = json.toString();
                    dos.writeUTF(jsonObject);
                    dos.flush();
                } catch(JSONException e){
                    e.printStackTrace();
                } catch(IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void getGroupMembers(final String groupname){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject object = new JSONObject();
                    object.put("type", "members");
                    object.put("group", groupname);
                    dos.writeUTF(object.toString());
                    dos.flush();
                }catch(JSONException e){
                    e.printStackTrace();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void checkInput(JSONObject jsonObject){
        try {
            if(jsonObject.getString("type").equals("register")){
                String userid = jsonObject.getString("id");
                String usergroup = jsonObject.getString("group");
                user = new User();
                user.setId(userid);
                user.setGroup(usergroup);
                user.setUserName(startFragment.getUserName());
                Log.d("USERINFO", user.getUserName() +" " + user.getId());
               registeredUser = true;
            }
            if(jsonObject.getString("type").equals("groups")){
                jsonArray = jsonObject.getJSONArray("groups");
                groups.clear();
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject individualGroup = jsonArray.getJSONObject(i);
                    groups.add(individualGroup.getString("group"));
                }
                setGroups(groups);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        startFragment.setContent(groups);
                    }
                });
            }
            if(jsonObject.getString("type").equals("members")){
                String groupName = jsonObject.getString("group");
                JSONArray individualObjectArray = jsonObject.getJSONArray("members");

                members.clear();
                for(int i = 0; i < individualObjectArray.length(); i++){
                        JSONObject jsonMember = individualObjectArray.getJSONObject(i);
                        members.add(jsonMember.getString("member"));
                    }

            }
            if(jsonObject.getString("type").equals("locations")){
                String group = jsonObject.getString("group");
                JSONArray jsonArray = jsonObject.getJSONArray("location");
                tempArray = new ArrayList<>();

                   for(int i = 0; i <= jsonArray.length()-1; i++){
                       JSONObject memberObject = jsonArray.getJSONObject(i);
                       String memberName = memberObject.getString("member");
                       String longitude = memberObject.getString("longitude");
                       String latitude = memberObject.getString("latitude");
                       member = new Member(memberName, Double.parseDouble(longitude), Double.parseDouble(latitude));
                       tempArray.add(member);
                       Log.d("Group", group);
                       for(Member m : tempArray){
                           Log.d("Member", m.getName());
                       }
                   }
                   memberHash.put(group, tempArray);
            }
            if(jsonObject.getString("type").equals("unregister")){
                Log.d("UNREGISTER:", "TRUE");
                Log.d("KOLLAUNREGISTER", jsonObject.getString("id"));
                registeredUser = false;
            }
            if(jsonObject.getString("type").equals("exception")){
                String exception = jsonObject.getString("message");
                Log.d("EXCEPTION", exception);

            }
        } catch(JSONException e){
            e.printStackTrace();
        }
    }

    public void startTimer(){
        TimerTask timerTask = new TimerTask(){
            @Override
            public void run(){
                getAvailableGroups();
                Log.d("WTF","XxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxX");
            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask,5000,5000);
    }

    public void positionTimer(){
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                while(registeredUser){
                    sendPosition(user.getId());
                }
            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask, 1000, 10000);
    }

    public void sendPosition(final String id){
        new Thread(new Runnable() {
            @Override
            public void run() {
                    try {
                        if (lastLocation != null) {
                              JSONObject positionObject = new JSONObject();
                              positionObject.put("type", "location");
                              positionObject.put("id", id);
                              positionObject.put("longitude", Double.toString(lastLocation.getLongitude()));
                              positionObject.put("latitude", Double.toString(lastLocation.getLatitude()));
                              dos.writeUTF(positionObject.toString());
                              dos.flush();
                        }
                           } catch(JSONException e){
                              e.printStackTrace();
                          } catch(IOException e){
                            e.printStackTrace();
                    }
                    }
        }).start();
    }

    public void disconnect(){
        try {
            connectedSocket.close();
        } catch(IOException e){
            e.printStackTrace();
        }
        connectedSocket = null;

    }

    protected void onResume(){
        if(ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
        }
    }

    private class InputListener implements Runnable {
        @Override
        public void run(){
            while(true){
                try {
                    JSONObject incomingObject;
                    if(dis.available() > 0){
                        incomingObject = new JSONObject(dis.readUTF());
                        checkInput(incomingObject);
                    }
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    private class LocListener implements android.location.LocationListener {
        @Override
        public void onLocationChanged(Location location) {
          lastLocation = location;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    public void initMap(String activeGroup){
        setActiveGroup(activeGroup);
        ArrayList<Member> memberlist = memberHash.get(activeGroup);
        if(memberlist!=null){
            setRealMembers(memberlist);
        }
        for(Member m : realMembers){
            Log.d("MEMBER", "NAME " + m.getName() + " LATITUDE " + m.getLatitude() + " LONGITUDE " + m.getLongitude());
        }
        mapFragment.setMembers(realMembers);
        setMapFragment();
    }

}
