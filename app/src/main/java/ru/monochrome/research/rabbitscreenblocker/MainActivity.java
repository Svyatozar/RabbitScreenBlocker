package ru.monochrome.research.rabbitscreenblocker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import ru.monochrome.home_screen.R;
import ru.monochrome.research.rabbitscreenblocker.services.ButtonService;
import ru.monochrome.research.rabbitscreenblocker.utils.DataBase;

public class MainActivity extends FragmentActivity implements LoaderCallbacks<Cursor>
{
    
    /**
     * �������� �� ��� ����������
     */
    static boolean isStarted = false;

    /**
     * ����������� �������
     */
	PackageManager pm;
	  /**
	   * ������ ���������� � ��
	   */
	  private DataBase db;
	  /**
	   * ������� ��� ��������� ������
	   */
	  private SimpleCursorAdapter scAdapter;
	  
	  /**
	   * ������ � ������������
	   */
	  ListView listView;
	 
	  /**
	   * ����� - ����� �������
	   */
	  private TextView batteryInfo;
	  /**
	   * ����� - �����
	   */
	  private TextView timeInfo;
	  /**
	   * �������� - �������
	   */
	  private ImageView imageBatteryState;
	  /**
	   * �������� - ������, ���� ��������� ����������
	   */
	  private ImageView isBatteryCharging;
	  /**
	   * �������� - ������
	   */
	  private ImageView signalState;
	  
	  /**
		* ����� ��� ������ ����� ������
		*/
	  private Time now;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
		
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
		db = new DataBase(getApplicationContext());
		db.open();

		pm = this.getPackageManager();
		now = new Time();
		
	    // ��������� ������� �������������
	    String[] from = new String[] { DataBase.COLUMN_APPNAME};
	    int[] to = new int[] { android.R.id.text1};
	    
	    // ������� ������� � ����������� ������
	    scAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null, from, to, 0);
		
		listView = (ListView) findViewById(R.id.list);
		listView.setAdapter(scAdapter);
		
	    // ������� ������ ��� ������ ������
	    getSupportLoaderManager().initLoader(0, null, this);
		
		listView.setOnItemClickListener(new OnItemClickListener() 
    	{
    	      public void onItemClick(AdapterView<?> parent, View view,int position, long id) 
    	      {
    	    	  try 
    	    	  {  
        	    	  Intent app = pm.getLaunchIntentForPackage(db.getPackage(id));
        	    	  MainActivity.this.startActivity(app);
    	    	  } 
    	    	  catch (Exception e) 
    	    	  {

    	    	  }

    	      }
    	});
		
		// ��������� ������, ��� ����� ���������� �������� ��� ������ �����
		Intent settings = new Intent(MainActivity.this,ButtonService.class);
	  	startService(settings);
	  	
	  	batteryInfo=(TextView)findViewById(R.id.percent);
        timeInfo=(TextView)findViewById(R.id.time);
        imageBatteryState=(ImageView)findViewById(R.id.battery);
        signalState=(ImageView)findViewById(R.id.signal);
        isBatteryCharging=(ImageView)findViewById(R.id.charging);
        
        isOnline();
        this.registerReceiver(this.mIRNetwork,	new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        this.registerReceiver(this.batteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        this.registerReceiver(this.timeInfoReceiver, new IntentFilter(Intent.ACTION_TIME_TICK)); 
        
        // ������������� ����� �����. ����� ����������� ������ ������
        timeInfoReceiver.onReceive(getApplicationContext(), null);
        
	}
	
	/**
	 * ��������� �������� �������� ���������� ��� ��� � ��������� ������
	 */
	private void isOnline()
    {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        
        if (netInfo != null && netInfo.isConnectedOrConnecting())
        	signalState.setImageResource(R.drawable.signal_high);
        else
        	signalState.setImageResource(R.drawable.sihnal_low);

    }
	
	/**
	 * ����������� ������ ������ � ��������� �����.
	 */
	private BroadcastReceiver timeInfoReceiver = new BroadcastReceiver() 
	{
		public void onReceive(Context context, Intent intent)
		{
	    	now.setToNow();
	    	
	    	String date = now.format3339(true);
	    	
	    	timeInfo.setText(date + "    " + now.hour + ":" + now.minute);
		}
	};
	
	/**
	 * ������� ���������� � �������
	 */
	private BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver() 
	{
		@Override
		public void onReceive(Context context, Intent intent) 
		{
			
			int  level= intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);	
 
			batteryInfo.setText(level + "%");
			
			if (level < 15)
				imageBatteryState.setImageResource(R.drawable.stat_sys_battery_0);
			else if (level < 28)
				imageBatteryState.setImageResource(R.drawable.stat_sys_battery_15);
			else if (level < 43)
				imageBatteryState.setImageResource(R.drawable.stat_sys_battery_28);
			else if (level < 57)
				imageBatteryState.setImageResource(R.drawable.stat_sys_battery_43);
			else if (level < 71)
				imageBatteryState.setImageResource(R.drawable.stat_sys_battery_57);
			else if (level < 85)
				imageBatteryState.setImageResource(R.drawable.stat_sys_battery_71);
			else if (level < 100)
				imageBatteryState.setImageResource(R.drawable.stat_sys_battery_85);
			else
				imageBatteryState.setImageResource(R.drawable.stat_sys_battery_100);//dataAttached,dataEnabled
			
			int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
			boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
			                     status == BatteryManager.BATTERY_STATUS_FULL;
			
			if (isCharging)
				isBatteryCharging.setVisibility(View.VISIBLE);
			else
				isBatteryCharging.setVisibility(View.INVISIBLE);
		}
	};
	
	/**
	 * �������� ���������� � �������
	 */
	private BroadcastReceiver mIRNetwork = new BroadcastReceiver() 
	{
	    @Override
	    public void onReceive(final Context context, final Intent intent) 
	    {

	        Log.i("LOG","mIRNetwork: Network State Received: "+intent.getAction());
	        Bundle extras = intent.getExtras();
	        if (extras!=null)
	        {
	        	boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
	            if (noConnectivity) 
	            	signalState.setImageResource(R.drawable.sihnal_low);
	            else 
	            	signalState.setImageResource(R.drawable.signal_high);
	        }
	    }
	    
	};
	
	@Override
	protected void onStart() 
	{
	    super.onStart();
	    getSupportLoaderManager().getLoader(0).forceLoad(); // ��������� ������ �� �� � ������ ��� ������ ���������
	}
	
	@Override
	protected void onDestroy() 
	{
	    super.onDestroy();
	    
	    // ��������� receiver'�
	    this.unregisterReceiver(batteryInfoReceiver);
	    this.unregisterReceiver(mIRNetwork);
	    this.unregisterReceiver(timeInfoReceiver);
	}
	
	@Override
	public void onBackPressed()
	{
		
	}
	
	  @Override
	  public Loader<Cursor> onCreateLoader(int id, Bundle bndl) 
	  {
	    return new MyCursorLoader(this, db);
	  }

	  @Override
	  public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) 
	  {
	    scAdapter.swapCursor(cursor);
	  }

	  @Override
	  public void onLoaderReset(Loader<Cursor> loader) 
	  {
		  //�������� ���...
	  }
	  
	  /**
	   * ��������� ������ �� ��
	   * @author svyatozar
	   *
	   */
	  static class MyCursorLoader extends CursorLoader 
	  {

	    DataBase db;
	    
	    public MyCursorLoader(Context context, DataBase db) 
	    {
	      super(context);
	      this.db = db;
	    }
	    
	    @Override
	    public Cursor loadInBackground() 
	    {
	      Cursor cursor = db.getAllData();
	      
	      return cursor;
	    }
	    
	  }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{

		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
