package ru.monochrome.research.rabbitscreenblocker.receivers;

import java.io.File;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

public class ApkCleaner extends BroadcastReceiver
{
	String folder = null;
	
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		Thread cleaner = new Thread(new Runnable() 
		{
			
			@Override
			public void run() 
			{
				Log.i("LOG", "RECEIVER IS WORK");
				
				String sdState;
				
				int counter = 0;
				
				while(true) // ����������, ����� �������������� sd �����
				{
					sdState = Environment.getExternalStorageState(); //�������� ��������� SD ����� (���������� ��� ��� ���) - ������������ true � false ��������������
					
					if (sdState.equals(Environment.MEDIA_MOUNTED))
					{
						folder = Environment.getExternalStorageDirectory().toString();
						break;
					}
					else
					{
						try 
						{
							Thread.sleep(500);
							counter = counter + 500;
						} 
						catch (InterruptedException e) 
						{
							e.printStackTrace();
						}
					}
					
					// ���� ����� ������� ������ 20 ��� - ��������� �����
					if (counter > 50000)
						break;
				};
				
				folder = folder + "/#data";
				
				File file = new File(folder); //������� �������� ����������
				
				 if(file.exists()) 
				 {
					Log.i("LOG", "FOLDER IS EXISTS!");
					
			        File[] files = file.listFiles();
			        
			        for(int i=0; i<files.length; i++) 
			        {
			                files[i].delete();
			        }
				}
			}
		});
		
		cleaner.start();

	}

}
