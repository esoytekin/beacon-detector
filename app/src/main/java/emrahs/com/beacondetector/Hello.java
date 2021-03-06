package emrahs.com.beacondetector;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.ArrayMap;
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
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import emrahs.com.beacondetector.api.APIUtil;
import emrahs.com.beacondetector.api.ApiService;
import emrahs.com.beacondetector.api.OAuthService;
import emrahs.com.beacondetector.api.model.AccountDto;
import emrahs.com.beacondetector.api.response.AccessTokenResponse;
import emrahs.com.beacondetector.api.response.ResponseForAccountList;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by emrahsoytekin on 9.12.2017.
 */

public class Hello extends AppCompatActivity implements BeaconConsumer {
    public static final String TAG = "BeaconsEverywhere";
    private BeaconManager beaconManager;
    String uuid="11111111-1111-1111-1111-111111111111";

    Vibrator v ;

    TextView txtDistance;
    private Button btnStart;
    Region region;

    public static final String ALTBEACON = "m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25";
    public static final String ALTBEACON2 = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25";
    public static final String EDDYSTONE_TLM =  "x,s:0-1=feaa,m:2-2=20,d:3-3,d:4-5,d:6-7,d:8-11,d:12-15";
    public static final String EDDYSTONE_UID = "s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19";
    public static final String EDDYSTONE_URL = "s:0-1=feaa,m:2-2=10,p:3-3:-41,i:4-20v";
    public static final String IBEACON = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";

    private Status currentStatus =  Status.STOPPED;

    private Handler spHandler;
    private static final int REQUEST_CODE = 1234;

    private TextToSpeech textToSpeech;

    private OAuthService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btnStart = (Button) findViewById(R.id.btnStart);

        setupBeaconManager();

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        txtDistance = (TextView) findViewById(R.id.txtDistance);


        spHandler = new Handler();
        spHandler.postDelayed(updateLabel, 0);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonClick();
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

        region = new Region("mybeacon", Identifier.parse(uuid), null, null);

        service = new OAuthService();

    }

    private void buttonClick() {
        if (currentStatus == Status.STOPPED) {
            setupBeaconManager();
            Drawable d = ContextCompat.getDrawable(getApplicationContext(), R.drawable.on);
            btnStart.setBackgroundResource(R.drawable.on);
        } else {
            unsetBeaconManager();
            Drawable d = ContextCompat.getDrawable(getApplicationContext(), R.drawable.off);
            btnStart.setBackgroundResource(R.drawable.off);
        }
    }

    private void setupBeaconManager()
    {
        beaconManager = BeaconManager.getInstanceForApplication(this);

        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(ALTBEACON));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(ALTBEACON2));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(EDDYSTONE_TLM));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(EDDYSTONE_UID));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(EDDYSTONE_URL));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(IBEACON));

        beaconManager.setForegroundScanPeriod(5000l);
        beaconManager.setBackgroundScanPeriod(5000l);
        beaconManager.setForegroundBetweenScanPeriod(1100l);
        beaconManager.setBackgroundBetweenScanPeriod(1100l);

        if (!beaconManager.isBound(this))
            beaconManager.bind(this);

        currentStatus = Status.STARTED;

    }

    private void unsetBeaconManager()
    {
        if (beaconManager.isBound(this))
        {
            try
            {
                beaconManager.stopRangingBeaconsInRegion(new Region("apr", null, null, null));
                beaconManager.unbind(this);
            }
            catch (RemoteException e)
            {
                Log.i(TAG, "RemoteException = "+e.toString());
            }
        }

        currentStatus = Status.STOPPED;
    }

    final Runnable updateLabel = new Runnable() {
        @Override
        public void run() {

            spHandler.postDelayed(this, 1000);

            txtDistance.setText(currentStatus.getLabel());

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unsetBeaconManager();
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                Log.i(TAG,"didRangeBeaconsInRegion, number of beacons detected = "+beacons.size());
                for (Beacon beacon :
                        beacons) {
                    Log.i(TAG, "didRangeBeaconsInRegion, " + beacon.getId1());
                    if (beacon.getId1().toString().equals(uuid)) {
                        Log.d(TAG,"processing beacon: " + beacon);
                        prosessBeacon(beacon);

                    }
                }
            }

        });
        try
        {
            beaconManager.startRangingBeaconsInRegion(new Region("apr", null, null, null));
        }
        catch (RemoteException e)
        {
            Log.i(TAG, "RemoteException = "+e.toString());
        }
    }


    private void prosessBeacon(Beacon beacon) {
        double distance = beacon.getDistance();
        Identifier id = beacon.getId1();
        Status acc = BeaconUtil.getDistance(distance);
        currentStatus = acc;

        if (acc == Status.FAR) {
            v.vibrate(100);
        } else if (acc == Status.NEAR) {
            v.vibrate(700);
        } else if (acc == Status.CLOSE) {
            v.vibrate(1000);
        } else {
            Log.d(TAG,"starting transaction request!");
            unsetBeaconManager();
            CharSequence cs = "Lütfen çekmek istediğiniz tutarı söyleyiniz.";
            recognizeSpeech(cs);

        }
        Log.d(TAG, acc.getLabel());
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


            if (requestCode == REQUEST_CODE) {
                for (String m :
                        matches_text) {
                    try {
                        int elem = Integer.parseInt(m);
                        CharSequence y = "Hesabınızdan " + elem + " lira çekilecek. Onaylıyorsanız evet, onaylamiyorsaniz hayir?";
                        approveWithdraw(y);
                        return;
                    } catch(Exception e) {
                        e.printStackTrace();
                    }

                }
                CharSequence x = "Anlaşılamadı, lütfen tekrar deneyin.";
                recognizeSpeech(x);

            } else if (requestCode == 123) {
                boolean match = false;

                for (String m :
                        matches_text) {
                    if (m.toLowerCase().equals("evet")) {
                        match = true;
                        break;
                    }
                }

                if (match) {
                    Log.d(TAG, "begin transaction");
                    callWs();
                    textToSpeech.speak("Makineden paranızı alabilirsiniz.",TextToSpeech.QUEUE_FLUSH,null,null);
                } else {
                    Log.w(TAG, "transaction cancelled");
                    textToSpeech.speak("İşleminiz isteğiniz üzerine iptal edilmiştir.",TextToSpeech.QUEUE_FLUSH,null,null);
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

    public static void getAccountList(Context context) {
        List<String> currencyList = new ArrayList<>();
        currencyList.add("TL");
        Map<String, Object> map = new ArrayMap<>();

        //TODO: müşteri no yaz
        map.put("customerNumber", "10000972");
        map.put("includedCurrencyCode", currencyList);

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), (new JSONObject(map)).toString());
        ApiService service1 = new ApiService();
        try {

            Call<ResponseForAccountList> a =  service1.getInstance(context).getAccountList(body);
            a.clone().enqueue(new Callback<ResponseForAccountList>() {
                @Override
                public void onResponse(Call<ResponseForAccountList> call, Response<ResponseForAccountList> response) {
                    response.isSuccessful();
                    List<AccountDto> list = response.body().getResponse().getRet().getAccountList();

                    for (AccountDto acc :
                            list) {
                        Log.d(TAG, acc.toString());
                    }

                    //callback.onSuccess(list);
                }

                @Override
                public void onFailure(Call<ResponseForAccountList> call, Throwable t) {
                    String a = t.getMessage();
                    System.console();
                    // callback.onFail(t.getMessage());
                }
            });

        } catch (Exception ex) {
            System.out.println("Access Token : " + ex.getMessage());
        }
    }

    private void callWs(){
        try {
            Call<AccessTokenResponse> call = service.getAccessToken(getApplicationContext()).getAccessToken(APIUtil.SCOPE, APIUtil.GRANT_TYPE, APIUtil.CLIENT_ID, APIUtil.CLIENT_SECRET);
            call.clone().enqueue(new Callback<AccessTokenResponse>() {
                @Override
                public void onResponse(Call<AccessTokenResponse> call, Response<AccessTokenResponse> response) {
                    APIUtil.ACCEESS_TOKEN = response.body().getAccess_token();
                    getAccountList(getApplicationContext());

                }

                @Override
                public void onFailure(Call<AccessTokenResponse> call, Throwable t) {
                    String a = t.getMessage();
                    Log.e(TAG,t.getMessage());
                }
            });

        } catch (Exception ex) {
            System.out.println("Access Token : " + ex.getMessage());
        }

    }

}
