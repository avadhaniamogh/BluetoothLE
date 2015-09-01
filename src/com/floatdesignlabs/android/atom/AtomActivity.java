package com.floatdesignlabs.android.atom;

import com.floatdesignlabs.android.atom.bluetooth.BluetoothFragment;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;


public class AtomActivity extends FragmentActivity {

	private static Context mContext;
	private static BluetoothAdapter mBluetoothAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.atom);
		mContext = getApplicationContext();
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		Button btnLoadMapCurrentLoc = (Button) findViewById(R.id.btn_load_map_current_loc);
		Button btnMapRouting = (Button) findViewById(R.id.btn_load_map_routing);
		Button btnBluetoothStart = (Button) findViewById(R.id.btn_load_bluetooth);

		OnClickListener currentLocListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentManager fragmentManager = getFragmentManager();
				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
				MapFragment mapFragment = new MapFragment();
				fragmentTransaction.add(R.id.map_fragment_container, mapFragment, "MAPS");
				fragmentTransaction.commit();
			}
		};
		btnLoadMapCurrentLoc.setOnClickListener(currentLocListener);

		OnClickListener routingListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FragmentManager fragmentManager = getFragmentManager();
				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
				MapRouting mapRouting = new MapRouting();
				fragmentTransaction.add(R.id.map_fragment_container, mapRouting, "MAPSROUTING");
				fragmentTransaction.commit();
			}

		};
		btnMapRouting.setOnClickListener(routingListener);

		OnClickListener bluetoothListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LinearLayout mainLinearLayout = (LinearLayout) findViewById(R.id.main_linear_layout);
				mainLinearLayout.setVisibility(View.INVISIBLE);
				FragmentManager fragmentManager = getFragmentManager();
				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
				BluetoothFragment bluetoothFragment = new BluetoothFragment();
				fragmentTransaction.add(R.id.bluetooth_fragment_container, bluetoothFragment, "BLUETOOTH");
				fragmentTransaction.addToBackStack("BLUETOOTH");
				fragmentTransaction.commit();
			}
		};
		btnBluetoothStart.setOnClickListener(bluetoothListener);
	}

	private BroadcastReceiver mBluetoothStateChangedReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();

			if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
				final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
						BluetoothAdapter.ERROR);
				updateBluetoothFragment(state);
			}
		}
	};

	private BroadcastReceiver mBluetoothDiscoveryReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			//Finding devices                 
			if (BluetoothDevice.ACTION_FOUND.equals(action)) 
			{
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				updateBluetoothFragmentAvailable(device, action);
			}
		}

		private void updateBluetoothFragmentAvailable(BluetoothDevice device, String action) {
			// TODO Auto-generated method stub
			FragmentManager fragmentManager = getFragmentManager();
			BluetoothFragment bluetoothFragment = (BluetoothFragment) fragmentManager.findFragmentByTag("BLUETOOTH");
			bluetoothFragment.displayAvailableDevices(device);
		}
	};

	private void updateBluetoothFragment(int state) {
		FragmentManager fragmentManager = getFragmentManager();
		BluetoothFragment bluetoothFragment = (BluetoothFragment) fragmentManager.findFragmentByTag("BLUETOOTH");
		if(state == BluetoothAdapter.STATE_ON) {
			bluetoothFragment.displayPairedDevices();
		} else if (state == BluetoothAdapter.STATE_OFF) {
			bluetoothFragment.clearPairedDevices();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.atom, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//		if(mReceiver != null) {
		mContext.unregisterReceiver(mBluetoothStateChangedReceiver);
		//			mReceiver = null;
		//		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		IntentFilter bluetoothStateChangedFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
		bluetoothStateChangedFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		mContext.registerReceiver(mBluetoothStateChangedReceiver, bluetoothStateChangedFilter);
		IntentFilter bluetoothDiscoveryFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND); 
		mContext.registerReceiver(mBluetoothDiscoveryReceiver, bluetoothDiscoveryFilter);
	}

	@Override
	public void onBackPressed() {
		if (getFragmentManager().getBackStackEntryCount() > 0 ){
			getFragmentManager().popBackStack();
			LinearLayout mainLinearLayout = (LinearLayout) findViewById(R.id.main_linear_layout);
			mainLinearLayout.setVisibility(View.VISIBLE);
		} else {
			super.onBackPressed();
		}
	}

	public BluetoothAdapter getBluetoothAdapter() {
		return mBluetoothAdapter;
	}
}
