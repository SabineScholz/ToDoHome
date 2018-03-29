package com.example.android.todohome;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;

public class WarningDialogFragment extends DialogFragment {

    private static final String LOG_TAG = WarningDialogFragment.class.getSimpleName() + " TEST";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.warning_message);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.d(LOG_TAG, "cancel");
            }
        });
        builder.setPositiveButton(R.string.discard_changes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.d(LOG_TAG, "discard changes");
                NavUtils.navigateUpFromSameTask(getActivity());
            }
        });

        setCancelable(false);
        return builder.create();
    }
}