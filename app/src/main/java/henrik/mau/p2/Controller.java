package henrik.mau.p2;

import android.app.Dialog;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
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
import java.util.Timer;
import java.util.TimerTask;

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

    private Handler handler = new Handler(Looper.getMainLooper());

    public Controller(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        initializeFragments();
        setFragment("StartFragment");
        connect();
    }

    private void initializeFragments() {
        initializeDataFragment();
        initializeStartFragment();
        initializeMapFragment();
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

    public void setMembers(ArrayList<String> members){
        this.members = members;
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

    public void setMapFragment(){
        setFragment("MapFragment");
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

            }
            if(jsonObject.getString("type").equals("locations")){

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
            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask,0,5000);
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
}
