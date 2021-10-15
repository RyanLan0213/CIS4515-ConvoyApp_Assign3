package edu.temple.convoy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class DialogFra extends AppCompatDialogFragment {
    String Convoyid;
    String status;

    public DialogFra(String Convoyid, String status) {
        this.Convoyid = Convoyid;
        this.status = status;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (status.equals("Start")) {
            AlertDialog.Builder builder = new AlertDialog.Builder((getActivity()));
            builder.setTitle("The Convoy has been successfully created")
                    .setMessage("The Convoy id is " + Convoyid)
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
            return builder.create();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder((getActivity()));
            builder.setTitle("Do you really wanna End the Convoy?")
                    .setMessage("The Convoy id is " + Convoyid)
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
            return builder.create();

        }
    }

}
