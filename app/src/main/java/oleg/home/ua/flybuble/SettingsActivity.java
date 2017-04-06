package oleg.home.ua.flybuble;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class SettingsActivity extends AppCompatActivity {

  Spinner keysPositionSpinner;
  Settings settings;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings);

    keysPositionSpinner = (Spinner) findViewById(R.id.spinner_key_position);

    onSetup();
  }

  protected void onSetup() {
    settings = Settings.getInstance(this);


    ArrayList<String> strings;
    int selection = 0;

    strings = new ArrayList<>();
    strings.add(getString(R.string.left_position));
    strings.add(getString(R.string.right_position));
    strings.add(getString(R.string.dual_position));
    selection = settings.keysPosition;

    ArrayAdapter<String> adapter;

    adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, strings);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    keysPositionSpinner.setAdapter(adapter);
    keysPositionSpinner.setSelection(selection);
    keysPositionSpinner.setPrompt(getString(R.string.keys_position));

  }

  @Override
  protected void onResume()
  {
    Log.d("Activity", "onResume()");
    super.onResume();
    onSetup();
  }

  @Override
  protected void onPause()
  {
    Log.d("BaseActivity", "onPause()");
    super.onPause();
    onSave();
  }

  protected void onSave() {
    settings.keysPosition = keysPositionSpinner.getSelectedItemPosition();

    settings.save(this);
  }

}
