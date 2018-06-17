package com.hackaton.aplikacja.pckg;

import android.Manifest;
import android.app.Notification;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.ViewObject;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapFragment;
import com.here.android.mpa.mapping.MapGesture;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.hackaton.aplikacja.pckg.MainCategoryEnum.AUTOBUS;
import static com.hackaton.aplikacja.pckg.MainCategoryEnum.KIEROWCA;
import static com.hackaton.aplikacja.pckg.MainCategoryEnum.PIESZY;
import static com.hackaton.aplikacja.pckg.MainCategoryEnum.ROWERZYSTA;

public class MainActivity extends AppCompatActivity {


    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    // permissions request code
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;

    /**
     * Permissions that need to be explicitly requested from end user.
     */
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public static CategoryClass[] categories;

    // map embedded in the map fragment
    private Map map = null;

    // map fragment embedded in this activity
    private MapFragment mapFragment = null;
    private LinearLayout collapsablelayout, pointLayout, mainCollapse;

    private TextView txtZaznacz;

    public static ArrayList<CustomMarkerMap> markers;
    public static int max_id = 0;

    private RelativeLayout showMarkerLayout;
    private TextView showMarkerCategory, showMarkerImg, showMarkerDesc;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermissions();

        categories = new CategoryClass[]{
                new CategoryClass("Wypadek", R.drawable.blank, KIEROWCA, AUTOBUS, ROWERZYSTA),
                new CategoryClass("Blokada drogi", R.drawable.znakicon, KIEROWCA, AUTOBUS, ROWERZYSTA),
                new CategoryClass("Akty wandalizmu", R.drawable.blank),
                new CategoryClass("Uszkodzenia na drodze", R.drawable.blank),
                new CategoryClass("Bezpańskie zwierzęta", R.drawable.pieseicon, PIESZY, ROWERZYSTA),
                new CategoryClass("Utrudnienia ruchu", R.drawable.blank, KIEROWCA, AUTOBUS, ROWERZYSTA),
                new CategoryClass("Zmiana rozkładu", R.drawable.blank, AUTOBUS),
                new CategoryClass("Zmiana organizacji ruchu", R.drawable.trafficicon, KIEROWCA, AUTOBUS, ROWERZYSTA),
                new CategoryClass("Inne", R.drawable.blank, KIEROWCA, AUTOBUS, ROWERZYSTA, PIESZY),
        };
    }

    private void initialize() {
        setContentView(R.layout.activity_main);
        collapsablelayout = (LinearLayout) findViewById(R.id.collapsableLinear);
        pointLayout = findViewById(R.id.point_layout);
        mainCollapse = (LinearLayout) findViewById(R.id.main_collapse);
        txtZaznacz = findViewById(R.id.txt_zaznacz);
        showMarkerLayout = findViewById(R.id.show_marker);
        // Search for the map fragment to finish setup by calling init().

        markers = new ArrayList<>();

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapfragment);
        mapFragment.init(new OnEngineInitListener() {
            @Override
            public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {
                if (error == OnEngineInitListener.Error.NONE) {
                    // retrieve a reference of the map from the map fragment
                    map = mapFragment.getMap();
                    // Set the map center to the Vancouver region (no animation)
                    map.setCenter(new GeoCoordinate(52.55863, 19.68246, 0.0),
                            Map.Animation.NONE);
                    // Set the zoom level to the average between min and max
                    map.setZoomLevel(16);

                    mapFragment.getMapGesture().addOnGestureListener(detectMarkerListener());
                } else {
                    Log.e(LOG_TAG, "Cannot initialize MapFragment (" + error + ")");
                }
            }
        });
    }

    /**
     * Checks the dynamically controlled permissions and requests missing permissions from end user.
     */
    protected void checkPermissions() {
        final List<String> missingPermissions = new ArrayList<String>();
        // check all required dynamic permissions
        for (final String permission : REQUIRED_SDK_PERMISSIONS) {
            final int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        if (!missingPermissions.isEmpty()) {
            // request all missing permissions
            final String[] permissions = missingPermissions
                    .toArray(new String[missingPermissions.size()]);
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                    grantResults);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int index = permissions.length - 1; index >= 0; --index) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        // exit the app if one permission is not granted
                        Toast.makeText(this, "Required permission '" + permissions[index]
                                + "' not granted, exiting", Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }
                }
                // all permissions were granted
                initialize();
                break;
        }
    }

    public void addNew(View view) {
        if (collapsablelayout.getVisibility() == View.GONE) {
//            expand();
            collapsablelayout.setVisibility(View.VISIBLE);
        } else {
//            collapse();

            collapsablelayout.setVisibility(View.GONE);
        }
    }

    public void getPoint(View view) {
        pointLayout.setVisibility(View.VISIBLE);
        mainCollapse.setVisibility(View.GONE);
        txtZaznacz.setVisibility(View.VISIBLE);
        collapsablelayout.setVisibility(View.GONE);

        pointListener = pointListener();

        pointMapMarker = new MapMarker();
        mapFragment.getMapGesture().addOnGestureListener(pointListener);
        System.out.println("tak");
    }

    private MapGesture.OnGestureListener detectMarkerListener() {
        MapGesture.OnGestureListener yourGestureHandlerImpementation = new MapGesture.OnGestureListener.OnGestureListenerAdapter() {

            @Override
            public boolean onMapObjectsSelected(List<ViewObject> list) {
                for (ViewObject viewObject : list) {
                    if (viewObject.getBaseType() == ViewObject.Type.USER_OBJECT) {
                        MapObject mapObject = (MapObject) viewObject;

                        if (mapObject.getType() == MapObject.Type.MARKER) {

                            MapMarker window_marker = ((MapMarker) mapObject);

//                            markers.get(Integer.parseInt(window_marker.getTitle()))

                            showMarkerLayout.setVisibility(View.VISIBLE);



                            return false;
                        }
                    }
                }
                return true;
            }
        };
        return yourGestureHandlerImpementation;
    }

    private MapGesture.OnGestureListener pointListener;

    private MapMarker pointMapMarker;
    private MapGesture.OnGestureListener pointListener() {
        MapGesture.OnGestureListener yourGestureHandlerImpementation = new MapGesture.OnGestureListener.OnGestureListenerAdapter() {

            @Override
            public boolean onTapEvent(PointF pointF) {

                GeoCoordinate c = map.pixelToGeo(pointF);

                Image image = new Image();
                try {
                    image.setImageResource(R.drawable.blank);
                } catch (IOException e) {
                    finish();
                }


                pointMapMarker.setCoordinate(c);
                pointMapMarker.setIcon(image);
                pointMapMarker.setTitle("tak");
                map.addMapObject(pointMapMarker);


                return true;
            }
        };
        return yourGestureHandlerImpementation;
    }

    private MapGesture.OnGestureListener routePointAListener() {
        MapGesture.OnGestureListener yourGestureHandlerImpementation = new MapGesture.OnGestureListener.OnGestureListenerAdapter() {

            @Override
            public boolean onTapEvent(PointF pointF) {
                GeoCoordinate c = map.pixelToGeo(pointF);

                System.out.println(c);

                Image image = new Image();
                try {
                    image.setImageResource(R.drawable.pieseicon);
                } catch (IOException e) {
                    finish();
                }

                MapMarker mapMarker = new MapMarker();
                mapMarker.setCoordinate(c);
                mapMarker.setIcon(image);
                map.addMapObject(mapMarker);

                return true;
            }
        };
        return yourGestureHandlerImpementation;
    }

    private MapGesture.OnGestureListener routePointBListener() {
        MapGesture.OnGestureListener yourGestureHandlerImpementation = new MapGesture.OnGestureListener.OnGestureListenerAdapter() {

            @Override
            public boolean onTapEvent(PointF pointF) {
                GeoCoordinate c = map.pixelToGeo(pointF);

                System.out.println(c);

                Image image = new Image();
                try {
                    image.setImageResource(R.drawable.pieseicon);
                } catch (IOException e) {
                    finish();
                }

                MapMarker mapMarker = new MapMarker();
                mapMarker.setCoordinate(c);
                mapMarker.setIcon(image);
                map.addMapObject(mapMarker);

                return true;
            }
        };
        return yourGestureHandlerImpementation;
    }


    private void showDialog(Map map) {
        CustomDialogClass cdd = new CustomDialogClass(MainActivity.this, categories, map, pointMapMarker);
        cdd.show();
    }


    public void getRoute(View view) {
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.custom_not);
        contentView.setImageViewResource(R.id.image, R.mipmap.ic_launcher);
        contentView.setTextViewText(R.id.title, "Zdarzenie w okolicy");
        contentView.setTextViewText(R.id.text, "This is a custom layout");

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "default")
                .setSmallIcon(R.drawable.cafe)
                .setCustomContentView(contentView)
                .setCustomBigContentView(contentView)
                .setCustomHeadsUpContentView(contentView)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_ALL);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, mBuilder.build());

    }

    public void getRegion(View view) {

    }

    public void pointDecline(View view) {
        mainCollapse.setVisibility(View.VISIBLE);
        txtZaznacz.setVisibility(View.GONE);
        pointLayout.setVisibility(View.GONE);
        mapFragment.getMapGesture().removeOnGestureListener(pointListener);

        map.removeMapObject(pointMapMarker);
    }

    public void pointAccept(View view) {
        mainCollapse.setVisibility(View.VISIBLE);
        txtZaznacz.setVisibility(View.GONE);
        pointLayout.setVisibility(View.GONE);
        mapFragment.getMapGesture().removeOnGestureListener(pointListener);

        showDialog(map);

        Toast.makeText(getApplicationContext(),
                "Dodano nowe zdarzenie.",
                Toast.LENGTH_SHORT).show();
    }
}
