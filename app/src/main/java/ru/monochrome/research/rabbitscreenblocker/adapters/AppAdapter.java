package ru.monochrome.research.rabbitscreenblocker.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import ru.monochrome.home_screen.R;
import ru.monochrome.research.rabbitscreenblocker.model.AppItem;
import ru.monochrome.research.rabbitscreenblocker.utils.DataBase;

/**
 * ������� ��� ������� ����������
 * @author svyatozar
 *
 */
public class AppAdapter extends BaseAdapter
{
	private LayoutInflater inflater;

	private ArrayList<AppItem> items;
	private DataBase base;
	
	public AppAdapter(Context context)
	{
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		items = new ArrayList<AppItem>();
		base = new DataBase(context);
	}
	
	/**
	 * ���-�� ���������
	 */
	@Override
	public int getCount() 
	{
		return items.size();
	}

	/**
	 * �������� ������� ������
	 */
	@Override
	public Object getItem(int position)
	{
		return items.get(position);
	}

	/**
	 * �������� ID �������� ������
	 */
	@Override
	public long getItemId(int id) 
	{
		return id;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		// ���������� ���������, �� �� ������������ view
	    View view = convertView;
	    
	    AppItem item = getAppItem(position);
	    
	    // ���� ������� �� ������
	    if (view == null) 
	    {
    		view = inflater.inflate(R.layout.item, parent, false);
	    } 
	    
	    TextView tw = (TextView)view.findViewById(R.id.text1);
	    CheckBox ch = (CheckBox)view.findViewById(R.id.checkBox1);
	    
	    tw.setText(item.app_name);
	    ch.setChecked(item.is_checked);
	    ch.setTag(position);
	    
	    ch.setOnClickListener(new OnClickListener() 
	    {
			
			@Override
			public void onClick(View v) 
			{
				CheckBox ch = (CheckBox)v;
				
				base.open();
				int index = (Integer)v.getTag();
				AppItem item = items.get(index);
				
				item.setChecked(ch.isChecked());
				
				
				if (ch.isChecked())
					base.addRec(item.app_name,item.app_package);
				else
					base.delRec(item.app_package);	
				
				base.close();
			}
		});

	    return view;
	}
	
    // ������� ������� �� �������
	public AppItem getAppItem(int position) 
    {
  	    return ((AppItem) getItem(position));
    }
    
    /**
     * �������� ������ � ���������. ��� ���������� ��������� ������� notifyDataSetChanged()
     * @param type ���
     * @param message ����
     */
    public void addApp(boolean is_checked, String app_name, String app_package)
    {
    	AppItem item = new AppItem(is_checked,app_name,app_package);
    	items.add(item);
    }
    
    /**
     * ������� ������ �� ���������. ��� ���������� ��������� ������� notifyDataSetChanged()
     * @param id �������������
     */
    public void deleteNote(int id)
    {
    	items.remove(id);
    }

}
