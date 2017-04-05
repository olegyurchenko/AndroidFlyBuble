package oleg.home.ua.flybuble;

import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;

public class GameActivity extends GraphicActivity {
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }
  
  @Override
  GraphicController newGraphicController() {
    return new GameController(this);
  }
  
}
