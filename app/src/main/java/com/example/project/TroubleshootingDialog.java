package com.example.project;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class TroubleshootingDialog extends AppCompatDialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("TroubleShooting").setMessage("Is Parkir not detecting your parking spots automatically?                         \n  " +
                "\n1)Make sure you have accepted the location permission, In case you have not, " +
                "You will be prompted to accept the location permission again when you open the app next time.                             \n    " +
                "\n2)If the device battery level is less than 15% or if power saving modes are enabled, Location services associated " +
                "with parkir will not operate properly due to Android and/or manufacturer battery protocols. ").setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        return  builder.create();
    }
}
