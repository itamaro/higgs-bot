package com.higgsbot.wifidirect;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.higgsbot.wifidirect.DeviceListFragment.DeviceActionListener;
import com.higgsbot.robodroid.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A fragment that manages a particular peer and allows interaction with device
 * i.e. setting up network connection and transferring data.
 */
public class DeviceDetailFragment extends Fragment implements ConnectionInfoListener {

	private View mContentView = null;
    private WifiP2pDevice device;
    private WifiP2pInfo info;
    ProgressDialog progressDialog = null;
    private Boolean mMustStop = false;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContentView = inflater.inflate(R.layout.device_detail, null);

        mContentView.findViewById(R.id.btn_disconnect).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ((DeviceActionListener) getActivity()).disconnect();
                    }
                });

        return mContentView;
    }
    
    @Override
    public void onPause() {
        super.onPause();
        this.mMustStop = true; // Stop the infinite loop
    }
    
    @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        
        this.info = info;
        this.getView().setVisibility(View.VISIBLE);
        
        // The owner IP is now known.
        TextView view = (TextView) mContentView.findViewById(R.id.group_owner);
        view.setText(getResources().getString(R.string.group_owner_text)
                + ((info.isGroupOwner == true) ? getResources().getString(R.string.yes)
                        : getResources().getString(R.string.no)));
        
        // InetAddress from WifiP2pInfo structure.
        view = (TextView) mContentView.findViewById(R.id.group_ip);
        view.setText("Group Owner IP - " + info.groupOwnerAddress.getHostAddress());
        
        // hide the connect button
        mContentView.findViewById(R.id.btn_connect).setVisibility(View.GONE);
        
        if (info.groupFormed && info.isGroupOwner) {

        } 
    }

    /**
     * Updates the UI with device data
     * 
     * @param device the device to be displayed
     */
    public void showDetails(WifiP2pDevice device) {
        this.device = device;
        this.getView().setVisibility(View.VISIBLE);
        TextView view = (TextView) mContentView.findViewById(R.id.group_ssid);
        view.setText(device.deviceAddress);
        view = (TextView) mContentView.findViewById(R.id.group_passphrase);
        view.setText(device.toString());
    }

    /**
     * Clears the UI fields after a disconnect or direct mode disable operation.
     */
    public void resetViews() {
        mContentView.findViewById(R.id.btn_connect).setVisibility(View.VISIBLE);
        TextView view = (TextView) mContentView.findViewById(R.id.group_ssid);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.group_passphrase);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.group_owner);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.status_text);
        view.setText(R.string.empty);
        this.getView().setVisibility(View.GONE);
    }

    public void setGroupInfo(String ssid, String passPhrase) {
    	TextView ssidView = (TextView) mContentView.findViewById(R.id.group_ssid);
    	ssidView.setText("SSID: " + ssid);
    	TextView passPhraseView = (TextView) mContentView.findViewById(R.id.group_passphrase);
    	passPhraseView.setText("passphrase : " + passPhrase);
    }
}
