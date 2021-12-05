package com.example.lgmvip_covidtracker;


import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.util.ArrayList;
import java.util.List;

public class VPAdapter extends RecyclerView.Adapter<VPAdapter.VPFragment> {

    List<Model> modelList;

    public VPAdapter(List<Model> modelList) {
        this.modelList = modelList;
    }


    @NonNull
    @Override
    public VPFragment onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vpview,parent,false);
        VPAdapter.VPFragment holder = new VPAdapter.VPFragment(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull VPFragment holder, int position) {
        Model data = modelList.get(position);
        holder.LocNameView.setText(data.getDistrict());
        holder.ActiveCasesView.setText(Integer.toString(data.getActive()));
        holder.RecoveredCasesView.setText(Integer.toString(data.getRecovered()));
        holder.DeathCasesView.setText(Integer.toString(data.getDeceased()));
        holder.TotalCasesView.setText(Integer.toString(data.getConfirmed()));
        float activper=(float)data.getActive()/data.getConfirmed();
        float recper=(float)data.getRecovered()/data.getConfirmed();
        float decper=(float)data.getDeceased()/data.getConfirmed();
        System.out.println(activper+" /// "+recper+" /// "+decper);
        holder.pieChart.addPieSlice(new PieModel("Active",activper, Color.parseColor("#3F51B5")));
        holder.pieChart.addPieSlice(new PieModel("Recovered",recper, Color.parseColor("#2E7D32")));
        holder.pieChart.addPieSlice(new PieModel("Dead",decper, Color.parseColor("#DD2C00")));
        holder.pieChart.startAnimation();
    }

    @Override
    public int getItemCount() {
        if(modelList==null)
            modelList=new ArrayList<>();
        return modelList.size();
    }

    class VPFragment extends RecyclerView.ViewHolder{
        PieChart pieChart;
        TextView LocNameView;
        TextView ActiveCasesView;
        TextView RecoveredCasesView;
        TextView DeathCasesView;
        TextView TotalCasesView;
        public VPFragment(@NonNull View itemView) {
            super(itemView);
            pieChart=itemView.findViewById(R.id.piechart);
            LocNameView=itemView.findViewById(R.id.LocationName);
            ActiveCasesView=itemView.findViewById(R.id.ActiveCasesFrag);
            TotalCasesView = itemView.findViewById(R.id.TotalCasesFrag);
            RecoveredCasesView=itemView.findViewById(R.id.RecoveredFrag);;
            DeathCasesView=itemView.findViewById(R.id.DeathsFrag);;
        }

    }

}
