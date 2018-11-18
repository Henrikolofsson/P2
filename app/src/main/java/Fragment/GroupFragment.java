package Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import henrik.mau.p2.Controller;
import henrik.mau.p2.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupFragment extends Fragment {
    private Controller controller;
    private TextView tvGroupName;
    private TextView tvGroupMembers;


    public GroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group, container, false);
        initializeComponents(view);
        return view;
    }

    private void initializeComponents(View view){
        tvGroupName = (TextView) view.findViewById(R.id.tvGroupName);
        tvGroupMembers = (TextView) view.findViewById(R.id.tvGroupMembers);
    }

    public void setController(Controller controller){
        this.controller = controller;
    }

}
