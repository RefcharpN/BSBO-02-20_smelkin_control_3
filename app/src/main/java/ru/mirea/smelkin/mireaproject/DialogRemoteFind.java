package ru.mirea.smelkin.mireaproject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DialogRemoteFind extends DialogFragment {


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("внимание").setMessage("обнаружено приложение удалённого доступа. Приложение может использоваться хакерами для кражи данных").setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((MainActivity2) getActivity()).onOkClicked();
                        dialog.cancel();
                    }
                })
                .setNeutralButton("заглушка", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((MainActivity2) getActivity()).onContinueClicked();
                        dialog.cancel();
                    }
                });
        return builder.create();
    }
}