package com.higgsbot.wifidirect;

import org.projectproto.objtrack.ObjTrackActivity;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.higgsbot.robodroid.R;
import com.higgsbot.wifidirect.DeviceListFragment.DeviceActionListener;

public class WiFiDirectActivity extends Activity implements ChannelListener, DeviceActionListener {

    public static final String TAG = "wifidirectdemo";
    private WifiP2pManager manager;
    private boolean retryChannel = false;

    private final IntentFilter intentFilter = new IntentFilter();
    private Channel channel;
    private BroadcastReceiver receiver = null;

	private AudioModem arduinoModem = new AudioModem();
	private final boolean alwaysBeep = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifidirect_main);

        // add necessary intent values to be matched.
        
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
		
        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
    	
    	// start the audio sender service
        new Thread(new Runnable() {
            @Override
            public void run() {
//            	String test_commands[] = {
//            			"L-0R-0A-0N-K-",
//            			"L+7R-7",
//            			"L+0R+0",
//            			"K+",
//            			"K-",
//            			"L-7R+7",
////            			"A+1",
////            			"N+",
////            			"K+",
////            			"N-",
////            			"L-0R-0A-0N-K-",
////            			"L-0R-0A-0N-K-",
////            			"L-0R-0A-0N-K-",
////            			"L-0R-0A-0N-K-",
////            			"L-0R-0A-0N-K-",
////            			"L-0R-0A-0N-K-",
////            			"L-0R-0A-0N-K-",
////            			"L-0R-0A-0N-K-",
////            			"L-0R-0A-0N-K-",
////            			"L-0R-0A-0N-K-",
////            			"L-0R-0A-0N-K-",
////            			"L-0R-0A-0N-K-",
////            			"L-0R-0A-0N-K-",
////            			"L-0R-0A-0N-K-",
////            			"L-0R-0A-0N-K-",
////            			"L-0R-0A-0N-K-",
////            			"L-0R-0A-0N-K-",
////            			"L-0R-0A-0N-K-",
////            			"L-0R-0A-0N-K-",
////            			"L-0R-0A-0N-K-",
////            			"L-0R-0A-0N-K-",
////            			"L-0R-0A-0N-K-",
////            			"L-0R-0A-0N-K-",
////            			"L-0R-0A-0N-K-",
//            			"L-0R-0A-0N-K-"
//            	};
//            	int cmd=0, loop=0;
        		char lastSentCommand[] = {0,0};
            	while (true) {
            		if (Globals.isPlayTest()) {
            			arduinoModem.sendData("Go Higgs!".toCharArray());
            		}
            		char audioCommand[] = Globals.getAudioCommand();
            		if ((!alwaysBeep) && (audioCommand[0] == lastSentCommand[0]) && (audioCommand[1] == lastSentCommand[1])) {
            			// no state change, so don't send audio
            			try {
							Thread.sleep(20);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
            		} else {
            			// send latest state
	                	//Log.d("AudioSender", "Sending audio command 0x" + 
	                	//		String.format("%02x", (byte) audioCommand[0]) + " 0x" +
	                	//		String.format("%02x", (byte) audioCommand[1]));
	        			arduinoModem.sendData(audioCommand);
	        			// cache last sent command
	        			lastSentCommand[0] = audioCommand[0];
	        			lastSentCommand[1] = audioCommand[1];
            			try {
							Thread.sleep(60);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
            		}
        			
//        			// TEST CODE:
//        			++loop;
//        			cmd = (loop / 10) % test_commands.length;
//                    Globals.updateState(test_commands[cmd]);
//                    //cmd = (cmd + 1) % test_commands.length;
            	}
            }
        }).start();
    }

    /** register the BroadcastReceiver with the intent values to be matched */
    @Override
    public void onResume() {
        super.onResume();
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
        registerReceiver(receiver, intentFilter);
        Log.d(TAG, "!!! registerReceiver");
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
        Log.d(TAG, "!!! unregisterReceiver");
    }

    /**
     * Remove all peers and clear all fields. This is called on
     * BroadcastReceiver receiving a state change event.
     */
    public void resetData() {
        DeviceListFragment fragmentList = (DeviceListFragment) getFragmentManager()
                .findFragmentById(R.id.frag_list);
        DeviceDetailFragment fragmentDetails = (DeviceDetailFragment) getFragmentManager()
                .findFragmentById(R.id.frag_detail);
        if (fragmentList != null) {
            fragmentList.clearPeers();
        }
        if (fragmentDetails != null) {
            fragmentDetails.resetViews();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_items, menu);
        return true;
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            
        	case R.id.start_server:
                if (manager != null && channel != null) {
                	manager.createGroup(channel, new WifiP2pManager.ActionListener() {

                        @Override
                        public void onSuccess() {
                            Toast.makeText(WiFiDirectActivity.this, "Group Initiated",
                                    Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(int reasonCode) {
                            Toast.makeText(WiFiDirectActivity.this, "Group Failed : " + reasonCode,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Log.e(TAG, "channel or manager is null");
                }
                return true;
        	case R.id.display_group_info:
        		
            	Log.d("DEVicedetailfragment", "!!! START SERVICE !!!");
            	Toast.makeText(getApplicationContext(), "services start !", Toast.LENGTH_SHORT).show();
            	
            	// start the network service
            	Intent netIntent = new Intent(this, NetworkService.class); 
            	// 8988 is the driver port
            	netIntent.putExtra("port", 8988); 
            	startService(netIntent);
            	 
            	netIntent.putExtra("port", 8989);
            	startService(netIntent);
            	
        		 manager.requestGroupInfo(channel, new WifiP2pManager.GroupInfoListener() {
						
						@Override
						public void onGroupInfoAvailable(WifiP2pGroup group) {
							 DeviceDetailFragment fragmentDetails = (DeviceDetailFragment) getFragmentManager()
						                .findFragmentById(R.id.frag_detail);
							fragmentDetails.setGroupInfo(group.getNetworkName(), group.getPassphrase());
						}
					});
        		return true;
        	case R.id.start_camera:
        		Intent intent = new Intent(getApplicationContext(), ObjTrackActivity.class);
                startActivity(intent);
        		return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void showDetails(WifiP2pDevice device) {
        DeviceDetailFragment fragment = (DeviceDetailFragment) getFragmentManager()
                .findFragmentById(R.id.frag_detail);
        fragment.showDetails(device);

    }

    @Override
    public void connect(WifiP2pConfig config) {
        manager.connect(channel, config, new ActionListener() {

            @Override
            public void onSuccess() {
                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(WiFiDirectActivity.this, "Connect failed. Retry.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    @Override
    public void disconnect() {
        final DeviceDetailFragment fragment = (DeviceDetailFragment) getFragmentManager()
                .findFragmentById(R.id.frag_detail);
        fragment.resetViews();
        manager.removeGroup(channel, new ActionListener() {

            @Override
            public void onFailure(int reasonCode) {
                Log.d(TAG, "Disconnect failed. Reason :" + reasonCode);

            }

            @Override
            public void onSuccess() {
                fragment.getView().setVisibility(View.GONE);
                Log.d(TAG, "!!! DISCONNECT !!!");
                Intent intent = new Intent(getApplicationContext(), NetworkService.class);  
           	 	stopService(intent);
            }

        });
    }

    @Override
    public void onChannelDisconnected() {
        // we will try once more
        if (manager != null && !retryChannel) {
            Toast.makeText(this, "Channel lost. Trying again", Toast.LENGTH_LONG).show();
            resetData();
            retryChannel = true;
            manager.initialize(this, getMainLooper(), this);
        } else {
            Toast.makeText(this,
                    "Severe! Channel is probably lost premanently. Try Disable/Re-Enable P2P.",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void cancelDisconnect() {

        /*
         * A cancel abort request by user. Disconnect i.e. removeGroup if
         * already connected. Else, request WifiP2pManager to abort the ongoing
         * request
         */
        if (manager != null) {
            final DeviceListFragment fragment = (DeviceListFragment) getFragmentManager()
                    .findFragmentById(R.id.frag_list);
            if (fragment.getDevice() == null
                    || fragment.getDevice().status == WifiP2pDevice.CONNECTED) {
                disconnect();
            } else if (fragment.getDevice().status == WifiP2pDevice.AVAILABLE
                    || fragment.getDevice().status == WifiP2pDevice.INVITED) {

                manager.cancelConnect(channel, new ActionListener() {

                    @Override
                    public void onSuccess() {
                        Toast.makeText(WiFiDirectActivity.this, "Aborting connection",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int reasonCode) {
                        Toast.makeText(WiFiDirectActivity.this,
                                "Connect abort request failed. Reason Code: " + reasonCode,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

    }
}
