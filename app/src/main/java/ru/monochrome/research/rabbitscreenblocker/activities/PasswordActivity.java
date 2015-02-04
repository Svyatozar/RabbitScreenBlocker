package ru.monochrome.research.rabbitscreenblocker.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import ru.monochrome.research.rabbitscreenblocker.R;
import ru.monochrome.research.rabbitscreenblocker.services.ButtonService;

// Окошко с вводом пароля
public class PasswordActivity extends Activity
{
    TextView password_field;

    SharedPreferences sPref;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.void_layout);

        password_field = (TextView)findViewById(R.id.editText1);
        sPref = getSharedPreferences("preferences", MODE_PRIVATE);
    }

    public void onOkClick(View v)
    {

        ButtonService.playClick(getApplicationContext()); // Играем звук

        Intent settings = new Intent(this,SettingsActivity.class);

        password = sPref.getString("PASSWORD", "");
        String value = password_field.getText().toString();

        if (value.equals(password))
        {
            startActivity(settings);
        }
        else
        {
            Toast.makeText(this, "Неверный пароль.", Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    public void onCancelClick(View v)
    {
        ButtonService.playClick(getApplicationContext());

        finish();
    }
}
