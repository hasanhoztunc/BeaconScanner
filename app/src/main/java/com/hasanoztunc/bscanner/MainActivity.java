package com.hasanoztunc.bscanner;

import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements BeaconConsumer, RangeNotifier {

    protected static final String TAG="MainActivity";
    BeaconManager beaconManager;

    BluetoothAdapter bluetoothAdapter;

    ArrayList<Identifier> arrayUUID;
    ArrayList<String> arrayName;
    ArrayList<Integer> arrayTx;
    ListView lwUUID;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lwUUID=(ListView)findViewById(R.id.lwUUID);
        arrayUUID=new ArrayList<Identifier>();
        arrayName=new ArrayList<String>();
        arrayTx=new ArrayList<Integer>();


        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        beaconManager=BeaconManager.getInstanceForApplication(MainActivity.this);

        if(bluetoothAdapter.getState()==BluetoothAdapter.STATE_OFF){
            Toast.makeText(getApplicationContext(),"Bluetooth is not enable!",Toast.LENGTH_LONG).show();

            AlertDialog.Builder alert=new AlertDialog.Builder(MainActivity.this);
            alert.setTitle("Bluetooth is not enable");
            alert.setMessage("Do you wanna enable bluetooth");
            alert.setNegativeButton("No",null);
            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    bluetoothAdapter.enable();
                }
            });
            alert.show();
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

        ArrayAdapter arrayAdapter=new ArrayAdapter(MainActivity.this,android.R.layout.simple_list_item_1,arrayUUID);
        lwUUID.setAdapter(arrayAdapter);

        lwUUID.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent details=new Intent(MainActivity.this,DetailsActivity.class);

                details.putExtra("uuid", arrayUUID.get(i).toString());
                details.putExtra("name",arrayName.get(i));
                details.putExtra("tx",arrayTx.get(i).toString());

                startActivity(details);
            }
        });

        if (beacons.size() > 0) {
            Log.i(TAG, "The first beacon I see is about "+beacons.iterator().next().getDistance()+" meters away.");
            Identifier uuid=beacons.iterator().next().getId1();
            if(!arrayUUID.contains(uuid)){
                arrayUUID.add(uuid);
                arrayName.add(beacons.iterator().next().getBluetoothName());
                arrayTx.add((int) beacons.iterator().next().getDistance());
            }

        }
    }

    public void onDestroy(){
        super.onDestroy();
        beaconManager.unbind(this);
    }
}
