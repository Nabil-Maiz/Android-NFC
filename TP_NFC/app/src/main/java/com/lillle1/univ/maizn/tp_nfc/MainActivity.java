package com.lillle1.univ.maizn.tp_nfc;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {

    NfcAdapter mAdapter;
    IntentFilter[] mFilters;
    String[][] mTechLists;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdapter = NfcAdapter.getDefaultAdapter(this);

        if(mAdapter != null && mAdapter.isEnabled()){
            Toast.makeText(this,"NFC available !",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this,"NFC not available !",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        Toast.makeText(this, "Card detected",Toast.LENGTH_SHORT).show();
        resolveIntent(intent);
    }

    protected void resolveIntent(Intent intent) {

        String action = intent.getAction();
        String datas = "";

        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {

            Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            MifareClassic mfc = MifareClassic.get(tagFromIntent);
            byte[] data;

            try {
                mfc.connect();
                boolean auth = false;

                int secCount = mfc.getSectorCount();
                int bCount = 0;
                int bIndex = 0;
                for (int j = 0; j < secCount; j++) {

                    auth = mfc.authenticateSectorWithKeyA(j, MifareClassic.KEY_DEFAULT);

                    if (auth) {

                        bCount = mfc.getBlockCountInSector(j);
                        bIndex = 0;
                        for (int i = 0; i < bCount; i++) {
                            bIndex = mfc.sectorToBlock(j);

                            data = mfc.readBlock(bIndex);

                            datas += getHexString(data);
                            Log.i("TAG", getHexString(data));
                            bIndex++;
                        }
                    } else {
                        Toast.makeText(this, "authentication failed", Toast.LENGTH_SHORT).show();
                    }
                }
                Toast.makeText(this,datas,Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Toast.makeText(this, "Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    protected void onResume(){
        super.onResume();

        PendingIntent mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);

        try {
            ndef.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }

        mFilters = new IntentFilter[] {
                ndef,
        };

        mTechLists = new String[][]{new String[]{MifareClassic.class.getName()}};

        mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);
    }

    @Override
    public void onPause() {
        super.onPause();
        mAdapter.disableForegroundDispatch(this);
    }

    public String getHexString(byte[] raw)
            throws UnsupportedEncodingException
    {
        char[] hex = new char[2 * raw.length];
        int index = 0;

        for (byte b : raw) {
            int v = b & 0xFF;
            hex[index++] = Character.forDigit(v >>> 4, 16);
            hex[index++] = Character.forDigit(v & 0xF, 16);
        }
        return new String(hex);
    }
}
