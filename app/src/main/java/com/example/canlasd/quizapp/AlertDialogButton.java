package com.example.canlasd.quizapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;


public class AlertDialogButton extends DialogFragment {
    private int position;
    private AlertPositiveListener alertPositiveListener;



    interface AlertPositiveListener {
        void onNextClick(int position);
    }

    @Override
    public void onAttach(android.app.Activity activity) {
        super.onAttach(activity);
        try {
            alertPositiveListener = (AlertPositiveListener) activity;
        } catch (ClassCastException e) {
            // The hosting activity does not implement the interface
            // AlertPositiveListener
            throw new ClassCastException(activity.toString()
                    + " must implement AlertPositiveListener");
        }
    }


    private final OnClickListener positiveListener = new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            AlertDialog alert = (AlertDialog) dialog;
            // added 1 since item position starts at zero
            position = alert.getListView().getCheckedItemPosition() + 1;

            alertPositiveListener.onNextClick(position);
        }
    };


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // getting info from bundle passed from main activity
        Bundle bundle = getArguments();
        String main_question = bundle.getString("main_question");
        String[] question_list = bundle.getStringArray("array");

        // create alert dialog
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        b.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    MainActivity.timer.cancel();
                    getActivity().finish();

                    dismiss();
                }
                return true;
            }
        });



        // set custom title
        TextView custom_title = new TextView(this.getActivity());
        custom_title.setText(main_question);
        custom_title.setTextSize(20);
        custom_title.setPadding(10, 10, 10, 10);

        b.setCustomTitle(custom_title);
        b.setSingleChoiceItems(question_list, -1, null);
        b.setPositiveButton("NEXT", positiveListener);



        return b.create();
    }
}

