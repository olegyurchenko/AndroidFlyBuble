package oleg.home.ua.flybuble;

import android.os.Bundle;

public class MainActivity extends GraphicActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  GraphicController newGraphicController() {
    return new MainController(this);
  }

}
