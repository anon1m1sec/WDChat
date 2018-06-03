package com.ts.wfd.chat;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.os.Looper.getMainLooper;

public class Connectivity extends Fragment implements PeerListListener, ConnectionInfoListener{

    WifiP2pManager mManager;
    Channel mChannel;
    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;
    Button mSearch;
    ConnectivityFragment fr;
    Button connectButton;
    TextView deviceInfo;
    TextView deviceInfoStatus;
    Button chatButton;
    Button addChatButton;

    private List peers = new ArrayList();
    private String deviceInf = "";

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {




        mManager = (WifiP2pManager) getActivity().getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(getActivity(), getMainLooper(), null);
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        /*final String[] items = new String[] {
                "No peers available. Please run search"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, items);*/

        View v = inflater.inflate(R.layout.connectivity_activity,container,false);

        fr = SingletClass.getConnectivityFragment();

        SingletClass.setConnectivity(this);
        chatButton = (Button) v.findViewById(R.id.go_chat);
        addChatButton = (Button)  v.findViewById(R.id.add_chat);
        addChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Device device = SingletClass.getDevice();
                if(device != null)
                {
                    Log.d("WDChat","deviceinf_connectivity: " + deviceInf);
                    String nick = device.getNick(deviceInf);
                    Log.d("WDChat","nick_connectivity: " + nick);
                    if(nick != null) {
                        device.addChat(nick);
                        Toast.makeText(getActivity(), "Chat added",
                                Toast.LENGTH_SHORT).show();
                    }
                    //if(device.checkChat())
                }
            }
        });
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                startActivity(intent);
            }
        });
        connectButton = (Button) v.findViewById(R.id.connect_button);
        deviceInfo = (TextView) v.findViewById(R.id.device_info);
        deviceInfoStatus = (TextView) v.findViewById(R.id.device_info_status);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //connect();
                int position = -1;
                for(int i=0;i<peers.size();i++)
                {
                    WifiP2pDevice device = (WifiP2pDevice) peers.get(i);
                    if(deviceInfo.getText().equals(device.deviceName)) position = i;
                }
                if(position > -1)  connect(position);
            }
        });

        mSearch = (Button) v.findViewById(R.id.search_button);
        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RunSearchDevices();
            }
        });

        mManager = (WifiP2pManager) getActivity().getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(getActivity(), getMainLooper(), null);



        getActivity().setTitle(R.string.connect_title);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mReceiver);
    }


    public void RunSearchDevices()
    {
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                //Log.d(TAG,"Peers was found");
                // Code for when the discovery initiation is successful goes here.
                // No services have actually been discovered yet, so this method
                // can often be left blank.  Code for peer discovery goes in the
                // onReceive method, detailed below.


                //mManager.requestPeers(mChannel,peersListener);

                /*peersListener.onPeersAvailable(DeviceList);
                int size = DeviceList.getDeviceList().size();

                Log.d(TAG,"Peers size = " + size);*/
            }

            @Override
            public void onFailure(int reasonCode) {
                //Log.d(TAG,"Peers not found");
                Toast.makeText(getActivity(), "Clients not found!",
                        Toast.LENGTH_SHORT).show();
                // Code for when the discovery initiation fails goes here.
                // Alert the user that something went wrong.
            }
        });
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList)
    {
        peers.clear();
        //List peers = new ArrayList();
        List<String> lst = new ArrayList<>();
        peers.addAll(peerList.getDeviceList());
        int size = peers.size();
        for(int i=0;i<size;i++)
        {
            WifiP2pDevice device = (WifiP2pDevice) peers.get(i);
            lst.add(device.deviceName);
            //fr.
        }
        fr.updateUI2(lst);
    }

    @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {
        //System.setProperty("http.proxyHost","192.168.49.3");
        //Log.d("WDChat","proxy: " + System.getProperty("http.proxyHost"));
        //WifiManager m = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //m.getConfiguredNetworks();
        //WifiConfiguration conf = new WifiConfiguration();
        //conf.setHttpProxy();
        //enableChatButton();
        String name = SingletClass.getDeviceName();
        if (info.groupFormed && info.isGroupOwner) {
            Toast.makeText(getActivity(), "Connection established. I am group owner",
                    Toast.LENGTH_SHORT).show();
            if(SingletClass.getDevice() == null) {
                Intent intent = new Intent(getActivity(), MessageService.class);
                intent.putExtra("name",name);
                intent.putExtra("address",info.groupOwnerAddress.getHostAddress());
                intent.putExtra("isAdmin",true);
                getActivity().startService(intent);
                //SingletClass.getConnectivity().
                //startService(new Intent(this, MessageService.class));
                //Device device = new Device(name,info.groupOwnerAddress.getHostAddress(),true, getContext());
                //SingletClass.setDevice(device);
            }

        } else if (info.groupFormed) {
            Toast.makeText(getActivity(), "Connection established. I am not a group owner",
                    Toast.LENGTH_SHORT).show();
            if(SingletClass.getDevice() == null) {
                Intent intent = new Intent(getActivity(), MessageService.class);
                intent.putExtra("name",name);
                intent.putExtra("address",info.groupOwnerAddress.getHostAddress());
                intent.putExtra("isAdmin",false);
                getActivity().startService(intent);
                //Device device = new Device(name, info.groupOwnerAddress.getHostAddress(),false, getContext());
                //SingletClass.setDevice(device);
            }
            /*Intent intent = new Intent(getActivity(), ChatActivity.class);
            startActivity(intent);*/
        }
    }

    public void updateInfoState(String deviceName, String deviceStatus)
    {
        connectButton.setEnabled(true);
        deviceInfo.setText(deviceName);
        deviceInf = deviceName;
        deviceInfoStatus.setText(deviceStatus);
    }

    public int getStatus(String deviceName)
    {
        int result = 0;
        for(int i = 0;i < peers.size();i++)
        {
            WifiP2pDevice device = (WifiP2pDevice) peers.get(i);
            //if(device.status == WifiP2pDevice.)
            if(device.deviceName.equals(deviceName)) result = device.status;
        }
        Log.d("WDDebug", "Status - " + String.valueOf(result));
        return result;
    }

    public void connect(int i)
    {
        WifiP2pDevice device = (WifiP2pDevice) peers.get(i);
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;




        mManager.connect(mChannel, config, new ActionListener() {

            @Override
            public void onSuccess() {
                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
                addChatButton.setEnabled(true);
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(getActivity(), "Connect failed. Retry.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*public void enableChatButton()
    {
        chatButton.setEnabled(true);
    }*/

}
