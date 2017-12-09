package emrahs.com.beacondetector;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

/**
 * Created by emrahsoytekin on 9.12.2017.
 */

public class Hello extends AppCompatActivity implements BeaconConsumer {
    public static final String TAG = "BeaconsEverywhere";
    private BeaconManager beaconManager;
//    String uuid="A9D05B20-6431-4E41-B572-416E198CCC73";
    String uuid="11111111-1111-1111-1111-111111111111";

    Vibrator v ;

    TextView txtDistance;
    Button btnStart;
    Button btnStop;
    Region region;

    public static final String ALTBEACON = "m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25";
    public static final String ALTBEACON2 = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25";
    public static final String EDDYSTONE_TLM =  "x,s:0-1=feaa,m:2-2=20,d:3-3,d:4-5,d:6-7,d:8-11,d:12-15";
    public static final String EDDYSTONE_UID = "s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19";
    public static final String EDDYSTONE_URL = "s:0-1=feaa,m:2-2=10,p:3-3:-41,i:4-20v";
    public static final String IBEACON = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";

    private String global_distance = "";

    private Handler spHandler;
    private static final int REQUEST_CODE = 1234;

    private TextToSpeech textToSpeech;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        beaconManager = BeaconManager.getInstanceForApplication(this);

//        beaconManager.getBeaconParsers().add(new BeaconParser()
//                .setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(ALTBEACON));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(ALTBEACON2));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(EDDYSTONE_TLM));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(EDDYSTONE_UID));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(EDDYSTONE_URL));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(IBEACON));

        beaconManager.bind(this);

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        txtDistance = (TextView) findViewById(R.id.txtDistance);
        btnStart = (Button) findViewById(R.id.btnStart);
        btnStop = (Button) findViewById(R.id.btnStop);

        global_distance = "waiting...";

        spHandler = new Handler();
        spHandler.postDelayed(updateLabel, 0);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (beaconManager.checkAvailability() ){
                    try {
                        beaconManager.startMonitoringBeaconsInRegion(region);
                        beaconManager.updateScanPeriods();
                        global_distance="started..";
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    beaconManager.stopRangingBeaconsInRegion(region);
                    global_distance = "stopped..";
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });


        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.getDefault());
                }
            }
        });


    }

    final Runnable updateLabel = new Runnable() {
        @Override
        public void run() {

            spHandler.postDelayed(this, 1000);

            txtDistance.setText(global_distance);

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }


    @Override
    public void onBeaconServiceConnect() {
        region = new Region("myBeaons", Identifier.parse(uuid), null, null);

        beaconManager.setMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                try {
                    Log.d(TAG, "didEnterRegion");
                    global_distance = "start";
                    beaconManager.startRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void didExitRegion(Region region) {
                try {
                    Log.d(TAG, "didExitRegion");
                    global_distance="exit";
                    beaconManager.stopRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void didDetermineStateForRegion(int i, Region region) {

            }
        });

        beaconManager.setRangeNotifier(new RangeNotifier() {

            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                /*

                for(Beacon oneBeacon : beacons) {
                    Log.d(TAG, "distance: " + oneBeacon.getDistance() + " id:" + oneBeacon.getId1() + "/" + oneBeacon.getId2() + "/" + oneBeacon.getId3());
                }
                */

                Iterator iterator = beacons.iterator();
                if (iterator.hasNext()) {
                    Beacon beacon = beacons.iterator().next();

                    double distance = beacon.getDistance();
                    Identifier id = beacon.getId1();
                    String acc = BeaconUtil.getDistance(distance);
                    global_distance = acc;

                    if (acc.equals(BeaconUtil.FAR)) {
                        v.vibrate(100);
                    } else if (acc.equals(BeaconUtil.NEAR)) {
                        v.vibrate(700);
                    } else if (acc.equals(BeaconUtil.CLOSE)) {
                        v.vibrate(1000);
                    } else {
                        try {
                            beaconManager.stopRangingBeaconsInRegion(region);
                            CharSequence cs = "L";
                            recognizeSpeech(cs);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        //you are close enough
                        // cekmek istediginiz tutari soyleyiniz

                    }
                    Log.d(TAG, acc);
                }


            }
        });

        try {
            beaconManager.startMonitoringBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    private void updateLabel(String msg) {
        txtDistance.setText(msg);
    }

    public boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net = cm.getActiveNetworkInfo();
        if (net!=null && net.isAvailable() && net.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            final ArrayList<String> matches_text = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            String match = matches_text.get(0);

            if (requestCode == REQUEST_CODE) {
                try {
                    int elem = Integer.parseInt(match);

                    CharSequence y = elem + " lira ?";
                    approveWithdraw(y);

                } catch(Exception e) {
                    e.printStackTrace();
                    CharSequence x = "Anlaşılamadı, tekrar deneyin";
                    recognizeSpeech(x);


                }

            } else if (requestCode == 123) {

                if (match.toLowerCase().equals("evet")) {
                    Log.d(TAG, "begin transaction");
                }

            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void approveWithdraw(CharSequence c)  {
        textToSpeech.speak(c,TextToSpeech.QUEUE_FLUSH,null,null);

        while(true) {
            if (!textToSpeech.isSpeaking()) {
                break;
            }

        }
        if(isConnected()){
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            startActivityForResult(intent, 123);
        }
        else{
            Toast.makeText(getApplicationContext(), "Please Connect to Internet", Toast.LENGTH_LONG).show();
        }

    }
    private void recognizeSpeech(CharSequence cs) {
//        String cs = "Lütfen çekmek istediğiniz tutarı söyleyiniz.";
        textToSpeech.speak(cs,TextToSpeech.QUEUE_FLUSH,null,null);

        while(true) {
            if (!textToSpeech.isSpeaking()) {
                break;
            }

        }
        if(isConnected()){
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            startActivityForResult(intent, REQUEST_CODE);
        }
        else{
            Toast.makeText(getApplicationContext(), "Please Connect to Internet", Toast.LENGTH_LONG).show();
        }

    }

//    public void onInit(int status) {
//        if(status == TextToSpeech.SUCCESS) {
//            textToSpeech.setOnUtteranceCompletedListener(this);
//        }
//    }
//
//    public void onUtteranceCompleted(String utteranceId) {
//        Log.i(TAG, utteranceId); //utteranceId == "SOME MESSAGE"
//    }
}