package com.example.lgmvip_facedetection;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ResultDialog extends DialogFragment {
    Button okBtn;
    RecyclerView recyclerView;
    RVAdapter rvAdapter;
    Context context;
    List<FaceModel> faceModels;

    public ResultDialog(Context ctx , List<FaceModel> fModelList) {
        faceModels = fModelList;
        context=ctx;
    }

    @Nullable
    @Override
    public View
    onCreateView(@NonNull LayoutInflater inflater,
                 @Nullable ViewGroup container,
                 @Nullable Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.fragment_resultdialog, container,false);
        okBtn = view.findViewById(R.id.result_ok_button);
        recyclerView = view.findViewById(R.id.faces_rv);

        ShowData();

        okBtn.setOnClickListener(
                v -> dismiss());

        return view;
    }

    void ShowData()
    {
        rvAdapter = new RVAdapter(faceModels);
        recyclerView.setAdapter(rvAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(manager);

    }
}