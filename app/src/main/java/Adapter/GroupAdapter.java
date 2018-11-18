package Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import henrik.mau.p2.Controller;
import henrik.mau.p2.R;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.Holder> {
    private LayoutInflater inflater;
    private List<String> content;
    private Controller controller;

    public GroupAdapter(Context context){
        this(context, new ArrayList<String>());
    }

    public GroupAdapter(Context context, List<String> content){
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.content = content;
    }

    public void setContent(List<String> content){
        this.content = content;
        super.notifyDataSetChanged();
    }

    public void setController(Controller controller){
        this.controller = controller;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = inflater.inflate(R.layout.fragment_group, viewGroup, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.tvGroupName.setText(content.get(position));
        //lägg till medlemmar
    }

    @Override
    public int getItemCount() {
        return content.size();
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvGroupName;
        private TextView tvGroupMembers;

        public Holder(@NonNull View itemView) {
            super(itemView);
            tvGroupName = (TextView) itemView.findViewById(R.id.tvGroupName);
            tvGroupMembers = (TextView) itemView.findViewById(R.id.tvGroupMembers);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
