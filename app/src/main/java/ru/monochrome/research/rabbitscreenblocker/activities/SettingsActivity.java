package ru.monochrome.research.rabbitscreenblocker.activities;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import ru.monochrome.home_screen.R;
import ru.monochrome.research.rabbitscreenblocker.MainActivity;
import ru.monochrome.research.rabbitscreenblocker.adapters.AppAdapter;
import ru.monochrome.research.rabbitscreenblocker.services.ButtonService;
import ru.monochrome.research.rabbitscreenblocker.utils.DataBase;

public class SettingsActivity extends Activity 
{

	/**
	 * �������� �������
	 */
	PackageManager pm;
	ArrayList<String> list_packages;
	DataBase base;
	
	String temp = "";
	
	AppAdapter adapter;
	String password;
	
	SharedPreferences sPref;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		sPref = getSharedPreferences("preferences", MODE_PRIVATE);

		pm = this.getPackageManager();
		base = new DataBase(getApplicationContext()); // ������� ������ �� � ������������
		base.open();
		
		adapter = new AppAdapter(getApplicationContext()); 

		Intent intent = new Intent(Intent.ACTION_MAIN, null); // ������ ��� ������ ���� ���������� � �������
		intent.addCategory(Intent.CATEGORY_LAUNCHER);

		ArrayList<ResolveInfo> list = (ArrayList<ResolveInfo>) pm.queryIntentActivities(intent, PackageManager.PERMISSION_GRANTED); // �������� ��� apps
		
		list_packages = base.getAllApps(); // �������� �� �� �� ����������, ��� ��� �������� ��� �������
		
		for (ResolveInfo rInfo : list) // � ��������� �������!
		{
			String current_app_name = rInfo.activityInfo.applicationInfo.loadLabel(pm).toString();
			
			if (!current_app_name.equals(temp)) // ����� �� ���� ��������
			{	
				boolean is_checked_found = false;
				
				for (String app_name : list_packages) // ���� ����� ��� �������� � �� - ������ �������
				{
					if (current_app_name.equalsIgnoreCase(app_name))
					{
						adapter.addApp(true, app_name,rInfo.activityInfo.packageName);
						is_checked_found = true;
						break;
					}
				}
				
				if (!is_checked_found) // ���� ��� - ������ ���������
				{
					adapter.addApp(false, current_app_name,rInfo.activityInfo.packageName);
				}
				
				temp = current_app_name;
			}
		}
		
		ListView listView = (ListView) findViewById(R.id.admin_list);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

		listView.setAdapter(adapter);
	}
	
	/**
	 * ������� �� ������ "�������� ������"
	 * @param v
	 */
	public void onClick(View v)
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("������ ��������������");
		alert.setMessage("������� � ������� ��");
		// ������� ���� �����
		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton("��", new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int whichButton) // ���� �� - ����� ������ � Preferences
			{
			  String value = input.getText().toString();

			  Editor ed = sPref.edit();
			  ed.putString("PASSWORD", value);
			  ed.commit();
			  
			  Toast.makeText(SettingsActivity.this, "������ ��������.", Toast.LENGTH_SHORT).show();
			}
		});

		alert.setNegativeButton("������", new DialogInterface.OnClickListener() // ���� �� �� - �������, ������ ������ ���������
		{
		  public void onClick(DialogInterface dialog, int whichButton) 
		  {
			 
		  }
		});

		alert.show();
	}
	
	/**
	 * ������� �� ������ "������� ����"
	 * @param v
	 */
	public void onClick_table(View v)
	{
		Intent user_screen = new Intent(this,MainActivity.class);
	  	startActivity(user_screen); // ��������� ���, ����������
	}
	
	/**
	 * "������ system bar"
	 * @param v
	 */
	public void onHide(View v)
	{	
		Thread hide = new Thread(new Runnable() 
		{		
			@Override
			public void run() 
			{
				Intent buttons = new Intent(SettingsActivity.this,ButtonService.class);
			  	startService(buttons); // ��������� ������ � ��������

			    // ��������� ����������� ������� ����� no Way
				try
				{
				    //REQUIRES ROOT
				    Process proc = Runtime.getRuntime().exec(
				        new String[]{"su","-c",
				        		"pm disable com.android.launcher"});
				    proc.waitFor();
				} catch(Exception ex) {
				    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
				}

				// ��������� com.android.systemui ����� ������
				try{
				    //REQUIRES ROOT
				    Build.VERSION_CODES vc = new Build.VERSION_CODES();
				    Build.VERSION vr = new Build.VERSION();
				    String ProcID = "79"; //HONEYCOMB AND OLDER

				    //v.RELEASE  //4.0.3
				    if(vr.SDK_INT >= vc.ICE_CREAM_SANDWICH){
				        ProcID = "42"; //ICS AND NEWER
				    }

				    //REQUIRES ROOT
				    Process proc = Runtime.getRuntime().exec(
				        new String[]{"su","-c",
				            "service call activity "+ ProcID +" s16 com.android.systemui"});
				    proc.waitFor();
				} catch(Exception ex) {
				    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
				}
				
				// ��������� com.android.systemui, ����� �� ����������� system bar � ������ ������ �������� ������� ��� �������� � ������
				try
				{
				    //REQUIRES ROOT
				    Process proc = Runtime.getRuntime().exec(
				        new String[]{"su","-c",
				            "pm disable com.android.systemui"});
				    proc.waitFor();
				} catch(Exception ex) {
				    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
				}
				
				// ��������� ���������
				try
				{
				    //REQUIRES ROOT
				    Process proc = Runtime.getRuntime().exec(
				        new String[]{"su","-c",
				            "pm disable com.android.settings"});
				    proc.waitFor();
				} catch(Exception ex) {
				    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
				}
				
				// ��������� ���� ����
				try
				{
				    //REQUIRES ROOT
				    Process proc = Runtime.getRuntime().exec(
				        new String[]{"su","-c",
				            "pm disable com.android.vending"});
				    proc.waitFor();
				} catch(Exception ex) {
				    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
				}
			}
		});
		
		hide.start(); // ��������� ��� ������� � ��������� ������, ����� UI �� �����
		

	}
	
	/**
	 * ������ system bar
	 * @param v
	 */
	public void onUnhide(View v)
	{
		Thread unHide = new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				Intent settings = new Intent(SettingsActivity.this,ButtonService.class);
				stopService(settings); // ������� ������ � ������
	
				// ������������ �������
				try
				{
				    //REQUIRES ROOT
				    Process proc = Runtime.getRuntime().exec(
				        new String[]{"su","-c",
				            "pm enable com.android.launcher"});
				    proc.waitFor();
				} catch(Exception ex) {
				    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
				}
				
				// ������������ systemui - ������ ������ ������ ��������� ����������
				try
				{
				    //REQUIRES ROOT
				    Process proc = Runtime.getRuntime().exec(
				        new String[]{"su","-c",
				            "pm enable com.android.systemui"});
				    proc.waitFor();
				} catch(Exception ex) {
				    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
				}
				
				// ������������ ���������
				try
				{
				    //REQUIRES ROOT
				    Process proc = Runtime.getRuntime().exec(
				        new String[]{"su","-c",
				            "pm enable com.android.settings"});
				    proc.waitFor();
				} catch(Exception ex) {
				    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
				}
				
				// ������������ ���� ����
				try
				{
				    //REQUIRES ROOT
				    Process proc = Runtime.getRuntime().exec(
				        new String[]{"su","-c",
				            "pm enable com.android.vending"});
				    proc.waitFor();
				} catch(Exception ex) {
				    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
				}
				
				// �������� systemui, ����� �������� ������
				try {
				    Runtime.getRuntime().exec(
				        new String[]{"am","startservice","-n",
				            "com.android.systemui/.SystemUIService"});
				} catch (IOException e) {
				    // TODO Auto-generated catch block
				    e.printStackTrace();
				}
			}
		});
		
		unHide.start();
	}
	
	// �������� �� ������� �� ������ "�����"
	public void onClick_exit(View v)
	{
		Intent intent = new Intent(Intent.ACTION_MAIN, null);
		intent.addCategory(Intent.CATEGORY_HOME);

		ArrayList<ResolveInfo> list = (ArrayList<ResolveInfo>) pm.queryIntentActivities(intent, PackageManager.PERMISSION_GRANTED); // �������� ��� ��������
		
		for (ResolveInfo rInfo : list) 
		{
			String package_name = rInfo.activityInfo.applicationInfo.packageName;
			
			if (!package_name.equals("ru.monochrome.home_screen")) // � ��������� ����� �� ��� ��� �� home_screen
			{
				Intent intentToResolve = new Intent(Intent.ACTION_MAIN);
				intentToResolve.addCategory(Intent.CATEGORY_HOME);
				intentToResolve.setPackage(package_name);
				ResolveInfo ri = getPackageManager().resolveActivity(intentToResolve, 0);
				if (ri != null) 
				{
				    Intent app = new Intent(intentToResolve);
				    app.setClassName(ri.activityInfo.applicationInfo.packageName, ri.activityInfo.name);
				    app.setAction(Intent.ACTION_MAIN);
				    app.addCategory(Intent.CATEGORY_HOME);
				    startActivity(app);
				}
		  	  	
		  	  	break;
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
