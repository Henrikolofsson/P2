package henrik.mau.p2;

import android.support.v4.app.Fragment;
import android.util.Log;

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
import Fragment.DataFragment;
import Fragment.StartFragment;
import Fragment.MapFragment;

public class Controller {
    private MainActivity mainActivity;
    private DataFragment dataFragment;
    private StartFragment startFragment;
    private MapFragment mapFragment;

    private static String IP = "195.178.227.53";
    private static int port = 7117;
    private boolean isConnected;
    private InetAddress inetAddress;
    private Socket connectedSocket;
    private DataOutputStream dos;
    private DataInputStream dis;

    private JSONArray jsonArray;
    private ArrayList<String> groups = new ArrayList<>();

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

    public ArrayList<String> getGroups() {
        return groups;
    }

    //----------------------------------------------------------------------------------------

    public void connect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    inetAddress = InetAddress.getByName(IP);
                    connectedSocket = new Socket(inetAddress, port);
                    dos = new DataOutputStream(connectedSocket.getOutputStream());
                    dis = new DataInputStream(connectedSocket.getInputStream());
                    isConnected = true;
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /*
    public void register(final String groupName, final String userName){
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

                    String message = dis.readUTF();

                    JSONObject objectReaded = new JSONObject(message);
                    checkInput(objectReaded);

                } catch(JSONException e){
                    e.printStackTrace();
                } catch(IOException e){
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
                    String groupName = jsonArray.getString(i);
                    groups.add(groupName);
                }

                for(int i = 0; i < groups.size(); i++){
                    Log.d("TESTGROUPS", groups.get(i));
                }
                setGroups(groups);
            }
            if(jsonObject.getString("type").equals("members")){

            }
        } catch(JSONException e){
            e.printStackTrace();
        }
    }

    public void checkMembers(){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "members");
            jsonObject.put("group", "Gandorf");
            String message = jsonObject.toString();
            dos.writeUTF(message);
            dos.flush();
            String fromServer = dis.readUTF();
            JSONObject objectReaded = new JSONObject(fromServer);
            checkInput(objectReaded);

        } catch(JSONException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }

    }
}
*/
