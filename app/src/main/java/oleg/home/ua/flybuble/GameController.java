package oleg.home.ua.flybuble;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Game controller.
 */

public class GameController extends GraphicController {
  private final static int LEFT_BTN_ID = 1;
  private final static int RIGHT_BTN_ID = 2;
  private final static int BUBLE_STEP = 50;
  private final static long REPEAT_TIMEOUT = 100;
  private final static long REPEAT_PERIOD = 100;

  private GameButton leftBtn1, leftBtn2, rightBtn1, rightBtn2;
  private Rect fieldRect;
  private Buble buble;
  
  GameController(Context c) {
    super(c);

    fieldRect = new Rect();
    
    leftBtn1 = new GameButton(LEFT_BTN_ID, R.mipmap.left_btn_up, R.mipmap.left_btn_down);
    leftBtn2 = new GameButton(LEFT_BTN_ID, R.mipmap.left_btn_up, R.mipmap.left_btn_down);
    rightBtn1 = new GameButton(RIGHT_BTN_ID, R.mipmap.right_btn_up, R.mipmap.right_btn_down);
    rightBtn2 = new GameButton(RIGHT_BTN_ID, R.mipmap.right_btn_up, R.mipmap.right_btn_down);

    buble = new Buble();
  }

  @Override
  protected void geometrySetup() {
    final int d = Math.min(rect.width(), rect.height()) / 4;
    final int b = 50;
    int x, y;
    
    leftBtn1.resize(d, d);
    leftBtn2.resize(d, d);
    rightBtn1.resize(d, d);
    rightBtn2.resize(d, d);

    x = b;
    y = rect.height() / 2 - d - b;
    
    leftBtn1.move(x, y);
  
    y = rect.height() / 2 + b;
    rightBtn1.move(x, y);

    x = rect.width() - d - b;
    y = rect.height() / 2 - d - b;
    leftBtn2.move(x, y);
  
    y = rect.height() / 2 + b;
    rightBtn2.move(x, y);

    
    fieldRect.set(2 * b + d, 0, rect.right - 2 * b - d, rect.bottom);
    
    buble.resize(d, d);
    buble.move(rect.width() / 2 - d, rect.bottom - d - b);
  }

  @Override
  protected void backgroundDraw(Canvas canvas) {
    canvas.drawColor(Color.WHITE);

    paint.setColor(Color.BLACK);
    paint.setStyle(Paint.Style.STROKE);
    canvas.drawRect(fieldRect, paint);
    
  }
  
  @Override
  protected void onClick(int id) {
    switch (id)
    {
      case LEFT_BTN_ID:
        toLeft();
        break;
      case RIGHT_BTN_ID:
        toRight();
        break;
    }
  }
  
  private void toLeft() {
    int x = buble.getRect().left;
    if(x > fieldRect.left)
    {
      x -= BUBLE_STEP;
      if(x < fieldRect.left)
        x = fieldRect.left;
      
      buble.move(x, buble.getRect().top);
      setSurfaceModified(true);
    }
    
  }
  
  private void toRight() {
    
    int x = buble.getRect().right;
    if(x < fieldRect.right)
    {
      x += BUBLE_STEP;
      if(x > fieldRect.right)
        x = fieldRect.right;
      buble.move(x - buble.getRect().width(), buble.getRect().top);
      setSurfaceModified(true);
    }
  }
  
  class GameButton extends Button {
    GameButton(int id, int up_image_id, int down_image_id) {
      super(id, up_image_id, down_image_id);
      setRepeatTimeout(REPEAT_TIMEOUT);
      setRepeatPeriod(REPEAT_PERIOD);
    }
  }
  
  class Buble extends GraphicObject implements Timerable {
    Bitmap srcBmp, bmp;
    final int deltaX = 10, deltaY = 10;
    final long TRANSFORMATION_TIMEOUT = 1000;
    long lastTimer = 0;
    int transformationIndex = 0;
    Rect bmpRect;

    Buble() {
      super(-1);
      srcBmp = BitmapFactory.decodeResource(context.getResources(), R.mipmap.buble);
      bmp = null;
      bmpRect = new Rect();
    }
  
    void onModify() {
      int w = rect.width();
      int h = rect.height();
      bmpRect.set(rect);
      switch(transformationIndex % 2)
      {
        case 0:
          w -= deltaX;
          bmpRect.left += deltaX / 2;
          break;
        case 1:
          h -= deltaY;
          bmpRect.top += deltaY / 2;
          break;
      }
      bmp = Bitmap.createScaledBitmap(srcBmp, w, h, true);
    }
  
    @Override
    void resize(int w, int h) {
      super.resize(w, h);
      transformationIndex = 0;
      onModify();
    }

    @Override
    void move(int x, int y) {
      super.move(x, y);
      //transformationIndex = 0;
      onModify();
    }
    
    @Override
    public void onTimer(long ms) {
      if(lastTimer + TRANSFORMATION_TIMEOUT < ms) {
        if (rect.width() > 0 && rect.height() > 0) {
          transformationIndex ++;
          onModify();
          setSurfaceModified(true);
        }
        lastTimer = ms;
      }
    }

    @Override
    public void onDraw(Canvas canvas) {
      if(bmp != null) {
        canvas.drawBitmap(bmp, bmpRect.left, bmpRect.top, paint);
      }
    }

  }


}
