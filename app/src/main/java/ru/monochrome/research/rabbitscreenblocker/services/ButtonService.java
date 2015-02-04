package ru.monochrome.research.rabbitscreenblocker.services;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;

import ru.monochrome.research.rabbitscreenblocker.MainActivity;
import ru.monochrome.research.rabbitscreenblocker.R;
import ru.monochrome.research.rabbitscreenblocker.activities.PasswordActivity;

/**
 * Сервис выводит поверх всего кнопку назад, и троекнопие по требованию
 * @author konservator_007
 *
 */
public class ButtonService extends Service
{
    public static final int NOTIFICATION_ID = 77;

    LayoutInflater li;
    WindowManager wm; 	// Менеджер для вывода на экран элементов

    View special_button; // Кнопка назад
    View special_panel; // Троекнопочная панель

    WindowManager.LayoutParams params;

    static SoundPool sp;
    static int soundIdClick = -1;

    static boolean isLaunched = false; // Флаг показывающий, сработал ли onStartCommand в Service
    static boolean isPanelAdded = false; // Флаг показывающий была ли добавлена троекнопочная панель на эуран
    static boolean isLongBackPressed = false; // Флаг показывающий было ли долгое нажатие на кнопку назад

    static
    {
        sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        sp.setOnLoadCompleteListener(new OnLoadCompleteListener()
        {

            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status)
            {
                // TODO Auto-generated method stub

            }
        });
    }

    public IBinder onBind(Intent paramIntent)
    {
        return null;
    }

    public void onCreate()
    {
        li = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);

        // Создаем View кнопки назад и панели с кнопками
        special_panel = li.inflate(R.layout.special_panel, null);
        special_button = li.inflate(R.layout.special_button, null);

        // Находим кнопки на панели
        View menu_button = special_panel.findViewById(R.id.imageViewMenu);
        View hide_button = special_panel.findViewById(R.id.imageViewHide);
        View home_button = special_panel.findViewById(R.id.imageViewHome);

        // .. и кнопку назад
        View back_special_button = special_button.findViewById(R.id.imageViewBack);

        params = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.BOTTOM | Gravity.LEFT;

        OnTouchListener hide = new OnTouchListener()
        {
            // ОБРАБОТКА НАЖАТИЯ "CКРЫТЬ"
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN)
                {
                    playClick(ButtonService.this);

                    isPanelAdded = false;
                    wm.removeView(special_panel);
                }

                return false;
            }
        };

        // ОБРАБОТКА НАЖАТИЯ "ДОМОЙ"
        OnTouchListener home = new OnTouchListener()
        {

            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN)
                {
                    playClick(ButtonService.this);

                    try
                    {
                        Intent home = new Intent(ButtonService.this,MainActivity.class);
                        home.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        ButtonService.this.startActivity(home);

                        Log.i("LOG", "PRESSED_HOME");
                    }
                    catch (Exception e)
                    {
                        // TODO: handle exception
                    }

                    isPanelAdded = false;
                    wm.removeView(special_panel);

                }
                return false;
            }
        };

        OnTouchListener menu = new OnTouchListener()
        {
            // ОБРАБОТКА НАЖАТИЯ "МЕНЮ"
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN)
                {
                    playClick(ButtonService.this);

                    try
                    {
                        Log.i("LOG", "PRESSED");

                        Intent settings = new Intent(ButtonService.this,PasswordActivity.class);
                        settings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(settings);
                    }
                    catch (Exception e)
                    {

                    }

                    isPanelAdded = false;
                    wm.removeView(special_panel);

                }

                return false;
            }
        };

        back_special_button.setLongClickable(true);

        back_special_button.setOnLongClickListener(new OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                // По долгому нажатию выводим панель
                isLongBackPressed = true;

                if (!isPanelAdded)
                {
                    isPanelAdded = true;

                    params.gravity = Gravity.CENTER | Gravity.CENTER;

                    wm.addView(special_panel, params);

                    params.gravity = Gravity.BOTTOM | Gravity.LEFT;
                }

                return false;
            }
        });

        OnTouchListener back = new OnTouchListener()
        {
            // ОБРАБОТКА НАЖАТИЯ "НАЗАД"
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getActionMasked() == MotionEvent.ACTION_UP)
                {
                    playClick(ButtonService.this);

                    if (!isLongBackPressed)
                    {
                        try
                        {
                            Log.i("LOG", "PRESSED");

                            Thread run = new Thread(new Runnable()
                            {

                                @Override
                                public void run()
                                {
                                    try
                                    {
                                        Runtime.getRuntime().exec(new String[] {"su", "-c", "input keyevent 4"});
                                    }
                                    catch (Exception e)
                                    {

                                    }
                                }
                            });

                            run.start();
                        }
                        catch (Exception e)
                        {
                            // TODO: handle exception
                        }
                    }
                    else
                    {
                        isLongBackPressed = false;
                    }
                }

                return false;
            }
        };

        // Устанавливаем обработчики для кнопок
        home_button.setOnTouchListener(home);
        menu_button.setOnTouchListener(menu);
        hide_button.setOnTouchListener(hide);

        back_special_button.setOnTouchListener(back);
    }

    public static void playClick(Context context)
    {
        if (-1 == soundIdClick)
            soundIdClick = sp.load(context, R.raw.click, 1);
        sp.play(soundIdClick, 1, 1, 0, 0, 1);
    }

    public void onDestroy()
    {

        // Убираем кнопки и панели с экрана
        wm.removeView(special_button);

        if (isPanelAdded)
        {
            isPanelAdded = false;
            wm.removeView(special_panel);
        }

        isLaunched = false;
    }

    // onStartCommand сработает только один раз, как срабатывает - сразу добавляет кнопку "назад" на экран
    public int onStartCommand(Intent paramIntent, int paramInt1, int paramInt2)
    {
        if (!isLaunched)
        {
            isLaunched = true;

            wm.addView(special_button, params);
            soundIdClick = sp.load(this, R.raw.click, 1); // Загружаем быстренько звук для нажатия

            Notification noti = new NotificationCompat.Builder(getApplicationContext())
                    .setContentTitle(getString(R.string.blocker_in_work))
                    .setSmallIcon(R.drawable.ic_launcher)
                    .build();

            startForeground(NOTIFICATION_ID, noti);
        }

        return START_STICKY;
    }
}