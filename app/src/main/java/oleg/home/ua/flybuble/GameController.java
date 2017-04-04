package oleg.home.ua.flybuble;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

/**
 * Class implements game logic
 */


public class GameController implements DrawView.SurfaceCallbacs {

  boolean surfaceModified;
  Context context;
  Paint paint;
  int width, height;
  
  
  GameController(Context c) {
    context = c;
    surfaceModified = true;
    paint = new Paint();
    width = height = 0;
  }

  @Override
  public boolean isSurfaceModified() {
    return surfaceModified;
  }
  @Override
  public void setSurfaceModified(boolean m) {
    surfaceModified = m;
  }
  
  @Override
  public void surfaceSetSize(int w, int h) {
    width = w;
    height = h;
  }
  
  @Override
  public void surfaceDraw(Canvas canvas) {
    Rect rect = new Rect();
    String text = String.format("%d", System.currentTimeMillis());
/*
    rect.set(0, 0, width, height);
    paint.setColor(Color.WHITE);
    paint.setStyle(Paint.Style.FILL);
    canvas.drawRect(rect, paint);
*/

    paint.setTypeface(Typeface.DEFAULT);// your preference here
    paint.setTextSize(30);// have this the same as your text size
    paint.getTextBounds(text, 0, text.length(), rect);
  
    int x = (width - rect.width()) / 2;
    int y = (height - rect.height()) / 2 + rect.height();
  
    rect.set(x - 20, y - 40, x + rect.width() + 40, y + rect.height() + 40);
    paint.setColor(Color.WHITE);
    paint.setStyle(Paint.Style.FILL);
    canvas.drawRect(rect, paint);

    paint.setColor(Color.BLACK);
    canvas.drawText(text, x, y, paint);
  }

  @Override
  public void surfaceLock() {
  }
  
  @Override
  public void surfaceUnlock() {
  }

  void onResume() {
    setSurfaceModified(true);
  }

  void onPause() {
    setSurfaceModified(true);
  }

  void onTimer() {
    setSurfaceModified(true);
  }
  
  void onTouchDown(int id, float fx, float fy) {
    setSurfaceModified(true);
  }
  
  void onTouchUp(int id, float x, float y) {
    setSurfaceModified(true);
  }
  
  void onTouchMove(int id, float fx, float fy) {
    setSurfaceModified(true);
  }
  
}
