package com.hackaton.aplikacja.pckg;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.here.android.mpa.common.Image;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapMarker;

import java.io.IOException;
import java.util.ArrayList;

public class CustomDialogClass extends Dialog implements
        android.view.View.OnClickListener {

    private Activity c;
    private Dialog d;
    private Button btn_ok, btn_cancel;


    private Spinner spinner;

    private Map map;

    private MapMarker mapMarker;


    private ArrayList<String> categories;

    private TextView txtDesc;

    public CustomDialogClass(Activity a, CategoryClass[] category, Map map, MapMarker mapMarker) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        categories = new ArrayList<>();

        for (CategoryClass categoryClass : category) {
            categories.add(categoryClass.getName());
        }
        this.map = map;
        this.mapMarker = mapMarker;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_send_position);
        btn_ok = findViewById(R.id.btn_ok);
        btn_cancel = findViewById(R.id.btn_cancel);
        spinner = findViewById(R.id.spinner);

        btn_ok.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);

        spinner.setOnItemSelectedListener(new CustomSpinnerListener());
        spinner.setSelection(0);

        txtDesc = findViewById(R.id.txtDesc);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                Image image = new Image();
                try {
                    image.setImageResource(MainActivity.categories[spinner.getSelectedItemPosition()].getImage());
                } catch (IOException ignored) {

                }

                MainActivity.markers.add(new CustomMarkerMap(mapMarker, spinner.getSelectedItemPosition(), (String) txtDesc.getText(), MainActivity.max_id++));

                mapMarker.setIcon(image);

                System.out.println("OK");

                break;
            case R.id.btn_cancel:
                System.out.println("Anulowano");
                map.removeMapObject(mapMarker);
                break;
            default:
                break;
        }
        dismiss();
    }
}
