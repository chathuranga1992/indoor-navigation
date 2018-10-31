package com.example.samit.wifilist;

        import android.app.Activity;
        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;
        import android.content.IntentFilter;
        import android.net.wifi.ScanResult;
        import android.net.wifi.WifiManager;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.Button;
        import android.widget.ListView;
        import android.widget.SimpleAdapter;
        import android.widget.TextView;
        import android.widget.Toast;

        import java.text.DecimalFormat;
        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.List;


public class MainActivity extends Activity implements View.OnClickListener
{
    WifiManager wifi;
    ListView lv;
    // TextView textStatus;
    Button mapbtn;
    Button buttonScan;

    int size = 0;
    List<ScanResult> results;
    String Router1= "9c:b2:b2:4a:53:e7";
    String Router2= "8c:79:67:56:65:15";
    String Router3= "b0:55:08:e7:e1:29";
    String bssid;

    String ITEM_KEY = "key";
    ArrayList<HashMap<String, String>> arraylist = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> arraylist2 = new ArrayList<HashMap<String, String>>();
    SimpleAdapter adapter;

    /* Called when the activity is first created. */

    public void init(){

        mapbtn=(Button)findViewById(R.id.mapbtn);
        mapbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent map= new Intent(MainActivity.this,Map.class);
                startActivity(map);
            }
        });

    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        init();
        //textStatus = (TextView) findViewById(R.id.textStatus);
        buttonScan = (Button) findViewById(R.id.buttonScan);
        buttonScan.setOnClickListener(this);
        lv = (ListView)findViewById(R.id.list);

        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled() == false)
        {
            Toast.makeText(getApplicationContext(), "wifi is disabled..making it enabled", Toast.LENGTH_LONG).show();
            wifi.setWifiEnabled(true);
        }
        this.adapter = new SimpleAdapter(MainActivity.this, arraylist, R.layout.row, new String[] { ITEM_KEY }, new int[] { R.id.list_value });
        lv.setAdapter(this.adapter);

        registerReceiver(new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context c, Intent intent)
            {
                results = wifi.getScanResults();
                size = results.size();


            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    public void onClick(View view)
    {
        arraylist.clear();
        wifi.startScan();

        Toast.makeText(this, "Scanning...." + size, Toast.LENGTH_SHORT).show();
        try
        {

            for (ScanResult scanResult : results)
            {
                bssid=scanResult.BSSID.toString();
                if(bssid.equals(Router1) || bssid.equals(Router2) || bssid.equals(Router3)){
                HashMap<String, String> item = new HashMap<String, String>();
                DecimalFormat df = new DecimalFormat("#.##");
                int level = WifiManager.calculateSignalLevel(scanResult.level, 5);

                   item.put(ITEM_KEY, scanResult.SSID + "  " + df.format(calculateDistance((double)scanResult.level, scanResult.frequency)));

                    arraylist.add(item);

                adapter.notifyDataSetChanged();
            }}
        }
        catch (Exception e)
        { }
    }

    public double calculateDistance(double levelInDb, double freqInMHz)    {
        double exp = (27.55 - (20 * Math.log10(freqInMHz)) + Math.abs(levelInDb)) / 20.0;
        return Math.pow(10.0, exp);
    }

}
