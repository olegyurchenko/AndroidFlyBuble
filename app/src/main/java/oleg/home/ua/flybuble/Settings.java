package oleg.home.ua.flybuble;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Gema settings
 */

public class Settings {
  private static Settings instance = null;
  public static Settings getInstance(Context context) {
    if(instance == null)
      instance = new Settings(context);
    return instance;
  }


  private final static String sectionName = "settings";
  public final static int
    LEFT_KEY_POSITION = 0,
    RIGHT_KEY_POSITION = 1,
    DUAL_KEY_POSITION = 2;

  public int tickTime = 100;
  public int keysPosition = DUAL_KEY_POSITION;

  private Settings(Context c) {
    load(c);
  }

  private void check() {
    switch (keysPosition) {
      case LEFT_KEY_POSITION:
      case RIGHT_KEY_POSITION:
      case DUAL_KEY_POSITION:
        break;
      default:
        keysPosition = DUAL_KEY_POSITION;

    }
  }

  public void load(Context context) {

    SharedPreferences preferences = context.getSharedPreferences(sectionName, Context.MODE_PRIVATE);
    tickTime = preferences.getInt("tickTime", tickTime);
    keysPosition = preferences.getInt("keysPosition", keysPosition);
    check();
  }

  public void save(Context context) {
    SharedPreferences preferences = context.getSharedPreferences(sectionName, Context.MODE_PRIVATE);
    SharedPreferences.Editor ed = preferences.edit();

    check();
    ed.putInt("tickTime", tickTime);
    ed.putInt("keysPosition", keysPosition);

    ed.apply();
  }

}


