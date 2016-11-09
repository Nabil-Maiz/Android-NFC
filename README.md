# Android-NFC

Voici des morceaux du code que vous pourrez copier-coller (mal fait avec le pdf)

Dans le manifest  :

    <uses-permission android:name="android.permission.NFC" />

    <uses-feature android:name="android.hardware.nfc" android:required="true"/>

    <intent-filter>
        <action android:name="android.nfc.action.TECH_DISCOVERED" />
        <category android:name="android.intent.category.DEFAULT" />
    </intent-filter>

    <meta-data android:name="android.nfc.action.TECH_DISCOVERED" android:resource="@xml/filter_nfc" />

fichier liste des technologies :

    <resources xmlns:xliff="urn:oasis:names:tc:xliff:document:1.2">
        <tech-list>
            <tech>android.nfc.tech.NfcA</tech>
            <tech>android.nfc.tech.MifareClassic</tech>
        </tech-list>
    </resources>

Main Activity  :

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
        Toast.makeText(this, "Card detected !",Toast.LENGTH_SHORT).show();

        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            Toast.makeText(this, "Etudiant : Nabil Maiz", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Error : Can't read card.", Toast.LENGTH_SHORT).show();
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