package emrahs.com.beacondetector;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.PubNubException;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int COARSE_LOCATIONPERMISSION_ID = 2;
    private BluetoothLeScanner mBluetoothLeScanner;
    private ScanFilter mScanFilter;
    private ScanSettings mScanSettings;

    private String publishKey = "pub-c-3dcba17c-ad60-4be4-aa00-4842ab6bd8bd";
    private String subscribeKey = "sub-c-48b88996-dc82-11e7-87a3-b28930076838";
    private String secretKey = "sec-c-MTcxM2VkMjMtYzcwMS00ZTVkLWFkODYtYmZjMjZlOTljZTIx";
    private PubNub pubNub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        if (!PermissionUtils.isAccessCoarseLocationGranted(MainActivity.this)) {
            Log.d("permission", "asking for ACCESS_COARSE_LOCATION");
            String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION};
            PermissionUtils.requestPermissions(this, COARSE_LOCATIONPERMISSION_ID, permissions);
        }

        PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setPublishKey(publishKey);
        pnConfiguration.setSubscribeKey(subscribeKey);
        pnConfiguration.setSecure(false);
        this.pubNub = new PubNub(pnConfiguration);


        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Log.e("##", "device does not support bluetooth");
            return;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        setScanFilter();
        setScanSettings();

        mBluetoothLeScanner.startScan(Arrays.asList(mScanFilter), mScanSettings, mScanCallback);
    }

    private void setScanFilter() {
        ScanFilter.Builder mBuilder = new ScanFilter.Builder();
        ByteBuffer mManufacturerData = ByteBuffer.allocate(23);
        ByteBuffer mManufacturerDataMask = ByteBuffer.allocate(24);
//        byte[] uuid = getIdAsByte(UUID.fromString("0CF052C297CA407C84F8B62AAC4E9020"));
        byte[] uuid = getIdAsByte(UUID.fromString("A9D05B20-6431-4E41-B572-416E198CCC73"));
        mManufacturerData.put(0, (byte)0xBE);
        mManufacturerData.put(1, (byte)0xAC);
        for (int i=2; i<=17; i++) {
            mManufacturerData.put(i, uuid[i-2]);
        }
        for (int i=0; i<=17; i++) {
            mManufacturerDataMask.put((byte)0x01);
        }
        mBuilder.setManufacturerData(224, mManufacturerData.array(), mManufacturerDataMask.array());
        mScanFilter = mBuilder.build();


    }

    private void setScanSettings() {
        ScanSettings.Builder mBuilder = new ScanSettings.Builder();
        mBuilder.setReportDelay(0);
        mBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER);
        mScanSettings = mBuilder.build();
    }

    protected ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            ScanRecord mScanRecord = result.getScanRecord();
//            int[] manufacturerData = mScanRecord.getManufacturerSpecificData(224);
            byte[] manufacturerSpecificData = mScanRecord.getManufacturerSpecificData(224);
            int mRssi = result.getRssi();
            int txPowerLevel = mScanRecord.getTxPowerLevel();

            double accurracy = calculateDistance(txPowerLevel, mRssi);
            String distance = getDistance(accurracy);
            Log.d("##","distance is " + distance);
            if (distance.equals("Near")) {
                getAdOfTheDay(2,3);
            }
        }
    };

    private byte[] getIdAsByte(UUID uuid) {

        long hi = uuid.getMostSignificantBits();
        long lo = uuid.getLeastSignificantBits();

        return ByteBuffer.allocate(16).putLong(hi).putLong(lo).array();


    }


    public double calculateDistance(int txPower, double rssi) {
        if (rssi == 0) {
            return -1.0; // if we cannot determine accuracy, return -1.
        }
        double ratio = rssi*1.0/txPower;
        if (ratio < 1.0) {
            return Math.pow(ratio,10);
        }
        else {
            double accuracy =  (0.89976)*Math.pow(ratio,7.7095) + 0.111;
            return accuracy;
        }
    }

    private String getDistance(double accuracy) {
        if (accuracy == -1.0) {
            return "Unknown";
        } else if (accuracy < 1) {
            return "Immediate";
        } else if (accuracy < 3) {
            return "Near";
        } else {
            return "Far";
        }
    }

    public void getAdOfTheDay(int major, int minor) {
        String channel = "YourCompany_"+major+"_"+minor;
        this.pubNub.subscribe().channels(Arrays.asList("")).execute();
        this.pubNub.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {

            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {
                System.out.println(message.getMessage());

            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {

            }
        });
    }

    public String getMajor(long[] mScanRecord) {
        String major = String.valueOf((mScanRecord[25] & 0xff) * 0x100 + (mScanRecord[26] & 0xff));
        return major;
    }

    public String getMinor(long[] mScanRecord) {
        String minor = String.valueOf((mScanRecord[27] & 0xff) * 0x100 + (mScanRecord[28] & 0xff));
        return minor;
    }

}
