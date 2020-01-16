package com.hasanoztunc.bscanner;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements BeaconConsumer, RangeNotifier {

    protected static final String TAG="MainActivity";
    BeaconManager beaconManager;

    BluetoothAdapter bluetoothAdapter;

    ListView lwUUID;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lwUUID=(ListView)findViewById(R.id.lwUUID);

        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        beaconManager=BeaconManager.getInstanceForApplication(MainActivity.this);

        if(bluetoothAdapter.getState()==BluetoothAdapter.STATE_OFF){
            Toast.makeText(getApplicationContext(),"Bluetooth is not enable!",Toast.LENGTH_LONG);
        }

        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));

        beaconManager.bind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        ArrayList<Identifier> identifiers=new ArrayList<Identifier>();
        identifiers.add(null);

        Region region=new Region("AllBeaconsRegion",identifiers);

        try {
            beaconManager.startRangingBeaconsInRegion(region);
        }catch (RemoteException e){
            e.printStackTrace();
        }

        beaconManager.addRangeNotifier(this);
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        if (beacons.size() > 0) {
            Log.i(TAG, "The first beacon I see is about "+beacons.iterator().next().getDistance()+" meters away.");
        }
    }

    public void onDestroy(){
        super.onDestroy();
        beaconManager.unbind(this);
    }
}
