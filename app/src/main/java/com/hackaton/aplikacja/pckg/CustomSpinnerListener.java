package com.hackaton.aplikacja.pckg;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

public class CustomSpinnerListener implements AdapterView.OnItemSelectedListener {
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        parent.setSelection(pos);
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

}
