package com.example.annaapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

public class AccessCodeDialog extends AppCompatDialogFragment {
    private EditText edtAccessCode;
    private AccessCodeListener listener;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AccessDialogTheme);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.access_code, null);


        edtAccessCode = view.findViewById(R.id.edtAccessCode);


        builder.setView(view)
                .setTitle("Show Code")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton("done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String accessCode = edtAccessCode.getText().toString();
                        listener.applyTexts(accessCode);
                    }
                });

        return builder.create();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (AccessCodeListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement AccessCodeListener");
        }
    }

    public interface AccessCodeListener{
        void applyTexts(String accessCode);
    }
    }