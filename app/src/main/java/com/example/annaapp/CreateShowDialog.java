//package com.example.annaapp;
//
//import android.app.AlertDialog;
//import android.app.Dialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.EditText;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatDialogFragment;
//
//public class CreateShowDialog extends AppCompatDialogFragment {
//    private EditText edtAccessCode;
//    //private CreateShowListener listener;
//
//
//    @NonNull
//    @Override
//    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AccessDialogTheme);
//
//        LayoutInflater inflater = getActivity().getLayoutInflater();
//        View view = inflater.inflate(R.layout.access_code, null);
//
//
//        edtAccessCode = view.findViewById(R.id.edtAccessCode);
//
//
//        builder.setView(view)
//                .setTitle("Show Code")
//                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                    }
//                })
//                .setPositiveButton("done", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                        //listener.applyTexts(accessCode);
//                    }
//                });
//
//        return builder.create();
//
//    }
//
////    @Override
////    public void onAttach(@NonNull Context context) {
////        super.onAttach(context);
////
////        try {
////            //listener = (AccessCodeListener) context;
////        } catch (ClassCastException e) {
////            throw new ClassCastException(context.toString() + "must implement AccessCodeListener");
////        }
////    }
//
////    public interface AccessCodeListener{
////        void applyTexts(String accessCode);
////    }
//    }