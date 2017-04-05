package oleg.home.ua.flybuble;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;

/**
 * Game controller.
 */

public class GameController extends GraphicController {
  GameController(Context c) {
    super(c);
  }

  @Override
  protected void backgroundDraw(Canvas canvas) {
    canvas.drawColor(Color.WHITE);
  }
}
