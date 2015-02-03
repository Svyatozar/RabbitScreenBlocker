package ru.monochrome.research.rabbitscreenblocker.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import ru.monochrome.home_screen.R;

/**
 * Created by svyatozar on 16.06.14.
 * Требуется Root. Блокирует systemui для обеспечения kiosk mode
 */
@SuppressWarnings("rawtypes")
public class BlockContentWorker extends AsyncTask
{
    private static Context context;

    private static String SU;
    private static String _C;
    private static String PM_DISABLE;

    private static String LAUNCHER;
    private static String SYSTEMUI;

    public static void init (Context ctx)
    {
        context = ctx;

        SU = context.getString(R.string.SU);
        _C = context.getString(R.string.C);

        PM_DISABLE = context.getString(R.string.PM_DISABLE);

        LAUNCHER = context.getString(R.string.LAUNCHER);
        SYSTEMUI = context.getString(R.string.SYSTEMUI);
    }

	@Override
	@SuppressWarnings("static-access")
    protected Object doInBackground(Object[] objects)
    {
        // Блокируем стандартный лаунчер
        try
        {
            Process proc = Runtime.getRuntime().exec(new String[]{SU,_C, PM_DISABLE + LAUNCHER});
            proc.waitFor();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }

        // Выгружаем com.android.systemui
        try
        {
            //REQUIRES ROOT
            Build.VERSION_CODES vc = new Build.VERSION_CODES();
            Build.VERSION vr = new Build.VERSION();
            String ProcID = context.getString(R.string.HONEYCOMB_AND_OLDER_PROCESS_ID);

            //v.RELEASE  //4.0.3
            if(vr.SDK_INT >= vc.ICE_CREAM_SANDWICH)
            {
                ProcID = context.getString(R.string.ICS_AND_NEWER_PROCESS_ID); //ICS AND NEWER
            }

            Process proc = Runtime.getRuntime().exec(new String[]{SU,_C, context.getString(R.string.UNLOAD_PROCESS,ProcID,SYSTEMUI)});
            proc.waitFor();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }

        return null;
    }
}
