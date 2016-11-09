package com.lille1.univ.maizn.nfctechnology;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    NfcAdapter nfcAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(nfcAdapter != null && nfcAdapter.isEnabled()){
            Toast.makeText(this,"NFC available !",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "NFC not available !",Toast.LENGTH_LONG).show();
        }
        
    }

    
    @Override
    protected void onResume(){
        super.onResume();

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        IntentFilter [] intentFilters = new IntentFilter[]{};
        
        nfcAdapter.enableForegroundDispatch(this,pendingIntent,intentFilters,null);
    }
    
    @Override
    protected void onPause(){
        super.onPause();
            
        nfcAdapter.disableForegroundDispatch(this);
    }
    
    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);

        if(intent.hasExtra(NfcAdapter.EXTRA_TAG)){
            Toast.makeText(this,"Card detected !",Toast.LENGTH_SHORT).show();    
        }
        
        if(intent != null) {
            if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
                Toast.makeText(this, "Reading your NFC !", Toast.LENGTH_SHORT).show();
            } else if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {

                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                String[] techList = tag.getTechList();

                // On va tester les differents types reconnus par android
                for (int i = 0; i < techList.length; i++) {
                    if (techList[i].equals(MifareClassic.class.getName())) {

                        MifareClassic mifareClassicTag = MifareClassic.get(tag);
                        switch (mifareClassicTag.getType()) {
                            case MifareClassic.TYPE_CLASSIC:
                                Toast.makeText(this, "TYPE MIFARE CLASSIC !", Toast.LENGTH_SHORT).show();
                                break;
                            case MifareClassic.TYPE_PLUS:
                                Toast.makeText(this, "TYPE MIFARE PLUS !", Toast.LENGTH_SHORT).show();
                                break;
                            case MifareClassic.TYPE_PRO:
                                Toast.makeText(this, " TYPE MIFARE PRO !", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    } else if (techList[i].equals(MifareUltralight.class.getName())) {
                        //MIFAIRE ULTRALIGHT
                        MifareUltralight mifareUlTag = MifareUltralight.get(tag);
                        switch (mifareUlTag.getType()) {
                            case MifareUltralight.TYPE_ULTRALIGHT:
                                Toast.makeText(this, "TYPE MIFARE ULTRALIGHT !", Toast.LENGTH_SHORT).show();
                                break;
                            case MifareUltralight.TYPE_ULTRALIGHT_C:
                                Toast.makeText(this, "TYPE MIFARE ULTRALIGHT_C !", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    } else if (techList[i].equals(IsoDep.class.getName())) {
                        Toast.makeText(this, "TYPE ISODEP !", Toast.LENGTH_SHORT).show();

                    } else if (techList[i].equals(Ndef.class.getName())) {
                        Toast.makeText(this, "TYPE NDEF !", Toast.LENGTH_SHORT).show();
                    }
                }
            }else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
                Toast.makeText(this, "TECH DISCOVERED", Toast.LENGTH_SHORT).show();
            }
            }
    }


}
