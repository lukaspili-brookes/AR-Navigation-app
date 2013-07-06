package com.siu.android.arapp.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.siu.android.arapp.R;
import com.siu.android.arapp.activity.NavigationActivity;

/**
 * Created by lukas on 6/28/13.
 */
public class SavePointDialog extends DialogFragment {

    private View mDialogView;
    private EditText mDistanceEditText;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mDialogView = LayoutInflater.from(getActivity()).inflate(R.layout.save_point_dialog, null, false);
        mDistanceEditText = (EditText) mDialogView.findViewById(R.id.store_checkpoint_dialog_distance_edittext);

        return new AlertDialog.Builder(getActivity())
                .setTitle("Save point")
                .setView(mDialogView)
                .setPositiveButton("Save checkpoint", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        savePoint(false);
                    }
                })
                .setNeutralButton("Save destination", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        savePoint(true);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mDistanceEditText.setText("");
                        getDialog().dismiss();
                    }
                })
                .create();

    }

    @Override
    public void onStart() {
        super.onStart();

        getDialog().setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);
    }

    private void savePoint(boolean destination) {
        String distanceInput = mDistanceEditText.getText().toString();
        if (distanceInput.equals("")) {
            Toast.makeText(getActivity(), "Distance required", Toast.LENGTH_SHORT).show();
            return;
        }

        ((NavigationActivity) getActivity()).calculateEndCheckpoint(Integer.valueOf(distanceInput), destination);
    }
}