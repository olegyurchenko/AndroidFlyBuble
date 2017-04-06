package oleg.home.ua.flybuble;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends GraphicActivity  implements GraphicController.ClickListener {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    graphicController.setClickListener(this);
  }
  
  @Override
  GraphicController newGraphicController() {
    return new MainController(this);
  }
  
  
  @Override
  public void onClick(int id) {
    
    switch (id)
    {
      case MainController.PLAY_BTN_ID:
        start();
        break;
      case MainController.SETTINGS_BTN_ID:
        settings();
        break;
    }
  }

  
  void start() {
    // Создаем объект Intent для вызова новой Activity
    Intent intent = new Intent(this, GameActivity.class);
    // запуск activity
    startActivity(intent);
  }
  
  void settings() {
    // Создаем объект Intent для вызова новой Activity
    Intent intent = new Intent(this, SettingsActivity.class);
    // запуск activity
    startActivity(intent);
  }

}
