package com.higgsbot.wifidirect;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
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
import android.widget.Toast;

import com.higgsbot.wifidirect.DeviceListFragment.DeviceActionListener;
import com.higgsbot.robodrive.ArmControlActivity;
import com.higgsbot.robodrive.DriverActivity;
import com.higgsbot.robodrive.R;

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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContentView = inflater.inflate(R.layout.device_detail, null);
        mContentView.findViewById(R.id.btn_connect).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;
                config.wps.setup = WpsInfo.PBC;
                
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                
                progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel",
                        "Connecting to :" + device.deviceAddress, true, true);
                ((DeviceActionListener) getActivity()).connect(config);
            }
        });

        mContentView.findViewById(R.id.btn_disconnect).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ((DeviceActionListener) getActivity()).disconnect();
                    }
                });

        mContentView.findViewById(R.id.btn_start_driver).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), DriverActivity.class);
                        intent.putExtra(DriverActivity.EXTRAS_GROUP_OWNER_ADDRESS, info.groupOwnerAddress.getHostAddress());
                        intent.putExtra(DriverActivity.EXTRAS_GROUP_OWNER_PORT, 8988);
                        startActivity(intent);
                    }
                });
        
        mContentView.findViewById(R.id.btn_start_arm_control).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), ArmControlActivity.class);
                        intent.putExtra(ArmControlActivity.EXTRAS_GROUP_OWNER_ADDRESS, info.groupOwnerAddress.getHostAddress());
                        intent.putExtra(ArmControlActivity.EXTRAS_GROUP_OWNER_PORT, 8989);
                        startActivity(intent);
                    }
                });
        return mContentView;
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

        // InetAddress from WifiP2pInfo struct.
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText("Group Owner IP - " + info.groupOwnerAddress.getHostAddress());

        if (info.groupFormed) { 
            mContentView.findViewById(R.id.btn_start_driver).setVisibility(View.VISIBLE);
            mContentView.findViewById(R.id.btn_start_arm_control).setVisibility(View.VISIBLE);
        }

        // hide the connect button
        mContentView.findViewById(R.id.btn_connect).setVisibility(View.GONE);
        
        /*
        // After the group negotiation, we assign the group owner as the file
        // server. The file server is single threaded, single connection server
        // socket.
        if (info.groupFormed && info.isGroupOwner) {
            //new DriverAsyncTask(getActivity(), mContentView.findViewById(R.id.status_text)).execute();
            //new ArmControlAsyncTask(getActivity()).execute();
            
        } else if (info.groupFormed) {
            // The other device acts as the client. 
            mContentView.findViewById(R.id.btn_start_driver).setVisibility(View.VISIBLE);
            mContentView.findViewById(R.id.btn_start_arm_control).setVisibility(View.VISIBLE);
            ((TextView) mContentView.findViewById(R.id.status_text)).setText(getResources()
                    .getString(R.string.client_text));
        }

        // hide the connect button
        mContentView.findViewById(R.id.btn_connect).setVisibility(View.GONE);*/
    }

    /**
     * Updates the UI with device data
     * 
     * @param device the device to be displayed
     */
    public void showDetails(WifiP2pDevice device) {
        this.device = device;
        this.getView().setVisibility(View.VISIBLE);
        TextView view = (TextView) mContentView.findViewById(R.id.device_address);
        view.setText(device.deviceAddress);
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText(device.toString());

    }

    /**
     * Clears the UI fields after a disconnect or direct mode disable operation.
     */
    public void resetViews() {
        mContentView.findViewById(R.id.btn_connect).setVisibility(View.VISIBLE);
        TextView view = (TextView) mContentView.findViewById(R.id.device_address);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.group_owner);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.status_text);
        view.setText(R.string.empty);
        mContentView.findViewById(R.id.btn_start_driver).setVisibility(View.GONE);
        mContentView.findViewById(R.id.btn_start_arm_control).setVisibility(View.GONE);
        this.getView().setVisibility(View.GONE);
    }

    /**
     * A simple server socket that accepts connection and writes some data on
     * the stream.
     */
    /*
    public static class DriverAsyncTask extends AsyncTask<Void, Void, String> {

        private Context context;
        private TextView statusText;

        public DriverAsyncTask(Context context, View statusText) {
            this.context = context;
            this.statusText = (TextView) statusText;
        }


        @Override
        protected String doInBackground(Void... params) {
            String msg = null;
        	try {
                ServerSocket serverSocket = new ServerSocket(8988);
                Log.d(WiFiDirectActivity.TAG, "Server: Socket opened");
                
                Socket client = serverSocket.accept();
                Log.d(WiFiDirectActivity.TAG, "Server: connection done");
                
                //while (true) {
                	InputStream inputstream = client.getInputStream();
                	Log.d(WiFiDirectActivity.TAG, "SERVER:" + inputstream.toString());
                	byte[] buffer = new byte[1024];
                	inputstream.read(buffer);
                	inputstream.close();
                	msg = new String(buffer);
                	
                //}
                
            } catch (IOException e) {
                Log.e(WiFiDirectActivity.TAG, e.getMessage());
            }
            
            return msg;
        }
        
        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                statusText.setText("File copied - " + result);
            }
        }
        @Override
        protected void onPreExecute() {
            statusText.setText("Opening a server socket");
        }
    }
    
    public static class ArmControlAsyncTask extends AsyncTask<Void, Void, Void> {

        private Context context;

        public ArmControlAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                ServerSocket serverSocket = new ServerSocket(8989);
                Log.d(WiFiDirectActivity.TAG, "Server: Socket opened");
                
                Socket client = serverSocket.accept();
                Log.d(WiFiDirectActivity.TAG, "Server: connection done");
                
                while (true) {
                	InputStream inputstream = client.getInputStream();
                	byte[] buffer = new byte[1024];
                	inputstream.read(buffer);
                	inputstream.close();
                	Toast.makeText(context, new String(buffer), Toast.LENGTH_LONG).show();
                }
                
            } catch (IOException e) {
                Log.e(WiFiDirectActivity.TAG, e.getMessage());
            }
            
            return null;
        }
    }*/
}
