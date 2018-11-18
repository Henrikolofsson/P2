package Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import Adapter.GroupAdapter;
import henrik.mau.p2.Controller;
import henrik.mau.p2.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class StartFragment extends Fragment {
    private Controller controller;
    private EditText etUser;
    private EditText etGroup;
    private Switch switchRegister;
    private RecyclerView rvGroups;
    private GroupAdapter groupAdapter;
    private List<String> content = new ArrayList<>();

    public StartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_start, container, false);
        initializeComponents(view);
        registerListener();

        groupAdapter = new GroupAdapter(getActivity(), content);
        groupAdapter.setController(controller);
        rvGroups.setAdapter(groupAdapter);
        return view;
    }

    private void initializeComponents(View view){
        etUser = (EditText) view.findViewById(R.id.etUser);
        etGroup = (EditText) view.findViewById(R.id.etGroup);
        switchRegister = (Switch) view.findViewById(R.id.switchRegister);
        rvGroups = (RecyclerView) view.findViewById(R.id.rvGroups);
    }

    private void registerListener(){
        switchRegister.setOnCheckedChangeListener(new SwitchListener());
    }

    public void setController(Controller controller){
        this.controller = controller;
    }

    public void setContent(ArrayList<String> content){
        this.content = content;
    }

    private class SwitchListener implements Switch.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked){
                
            } else {

            }
        }
    }

    public void update(){
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(content!=null){
            groupAdapter.setContent(content);
        }
    }
}
