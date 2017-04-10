package oleg.home.ua.flybuble;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

/**
 * Game controller.
 */

public class GameController extends GraphicController {
  private final static int LEFT_BTN_ID = 1;
  private final static int RIGHT_BTN_ID = 2;
  private final static int BUBLE_STEP = 50;
  private final static long REPEAT_TIMEOUT = 100;
  private final static long REPEAT_PERIOD = 100;
  private final static int MOTION_STEP = 5;
  private final static int MOTION_PAUSE = 20;
  private final static int OBSTRUCTION_LENGTH = 150;
  private final static int MAX_OBSTRUCTION_COUNT = 10;


  private GameButton leftBtn;
  private GameButton rightBtn;
  private Rect fieldRect;
  private Rect screenRect;
  private Buble buble;
  private Settings settings;
  private Random random;
  private long lastTimer = 0;
  protected ArrayList<Bitmap> obstructionBitmapList;


  GameController(Context c) {
    super(c);

    random = new Random();

    settings = Settings.getInstance(c);

    fieldRect = new Rect();
    screenRect = rect;

    leftBtn = new GameButton(LEFT_BTN_ID, R.mipmap.left_btn_up, R.mipmap.left_btn_down);
    rightBtn = new GameButton(RIGHT_BTN_ID, R.mipmap.right_btn_up, R.mipmap.right_btn_down);

    buble = new Buble();
    obstructionBitmapList = new ArrayList<>();

    obstructionBitmapList.add(BitmapFactory.decodeResource(context.getResources(), R.mipmap.pen1));
    obstructionBitmapList.add(BitmapFactory.decodeResource(context.getResources(), R.mipmap.pencil1));
    obstructionBitmapList.add(BitmapFactory.decodeResource(context.getResources(), R.mipmap.pin1));
    obstructionBitmapList.add(BitmapFactory.decodeResource(context.getResources(), R.mipmap.injector1));
  }

  @Override
  protected void geometrySetup() {
    final int d = Math.min(rect.width(), rect.height()) / 4;
    final int b = 50;
    int x, y;
    
    leftBtn.resize(d, d);
    rightBtn.resize(d, d);

    switch (settings.keysPosition) {
      case Settings.LEFT_KEY_POSITION:
        x = b;
        y = rect.height() / 2 - d - b;
        leftBtn.moveTo(x, y);
        y = rect.height() / 2 + b;
        rightBtn.moveTo(x, y);
        fieldRect.set(2 * b + d, 0, rect.right, rect.bottom);
        break;

      case Settings.RIGHT_KEY_POSITION:
        x = rect.width() - d - b;
        y = rect.height() / 2 - d - b;
        leftBtn.moveTo(x, y);
        y = rect.height() / 2 + b;
        rightBtn.moveTo(x, y);
        fieldRect.set(0, 0, rect.right - 2 * b - d, rect.bottom);
        break;

      case Settings.DUAL_KEY_POSITION:
        x = b;
        y = (rect.height() - d) / 2;
        leftBtn.moveTo(x, y);
        x = rect.width() - d - b;
        rightBtn.moveTo(x, y);
        fieldRect.set(2 * b + d, 0, rect.right - 2 * b - d, rect.bottom);
        break;
    }

    buble.resize(d, d);
    buble.moveTo(rect.width() / 2 - d, rect.bottom - d - b);
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

    int oldX = buble.getRect().left;
    int x = oldX;
    if(x > fieldRect.left)
    {
      x -= BUBLE_STEP;
      if(x < fieldRect.left)
        x = fieldRect.left;

      buble.moveTo(x, buble.getRect().top);
      ArrayList<Obstruction> lst = obstructionList();
      for(Obstruction o : lst) {
        while(buble.isIntersection(o)) {
          o.move(-10, 0);
        }
      }

      setSurfaceModified(true);
    }
    
  }
  
  private void toRight() {

    int oldX = buble.getRect().left;
    int x = buble.getRect().right;
    if(x < fieldRect.right)
    {
      x += BUBLE_STEP;
      if(x > fieldRect.right)
        x = fieldRect.right;
      buble.moveTo(x - buble.getRect().width(), buble.getRect().top);

      ArrayList<Obstruction> lst = obstructionList();
      for(Obstruction o : lst) {
        while (buble.isIntersection(o)) {
          o.move(10, 0);
        }
      }
      setSurfaceModified(true);
    }
  }

  private ArrayList<Obstruction> obstructionList() {
    ArrayList<Obstruction> lst = new ArrayList<>();
    for (Object o : childList) {
      Obstruction obstc = null;

      try {
        obstc = (Obstruction) o;
      } catch (Exception ignored) {
      }

      if (obstc != null) {
        lst.add(obstc);
      }
    }

    return lst;
  }


  private void motion() {

    int objecCount = 0;
    ArrayList<Object> rmList = null;

    for(Object o : childList) {
      Obstruction obstc = null;

      try {
        obstc = (Obstruction) o;
      }
      catch (Exception ignored) {
      }

      if(obstc != null) {
        obstc.move(0, MOTION_STEP);
        if(buble.isIntersection(obstc)) {
          //!!!!! For debug
          obstc.move(
            0,
            - MOTION_STEP
          );
        }
        if(obstc.isVisible())
          objecCount ++;
        else {
          if(rmList == null)
            rmList = new ArrayList<>(1);
          rmList.add(obstc);
        }
      }

    }

    if(objecCount < MAX_OBSTRUCTION_COUNT) {
      if((random.nextInt(1000) % 20) == 0)
        new Obstruction(obstructionBitmapList.get(random.nextInt(1000) % obstructionBitmapList.size()));
    }

    if(rmList != null) {
      for (Object o : rmList)
        removeChild(o);
    }

    setSurfaceModified(true);
  }


  @Override
  void onTimer() {
    super.onTimer();
    long ms = System.currentTimeMillis();
    if(lastTimer + MOTION_PAUSE < ms) {
      if(lastTimer != 0) {
        surfaceLock();
        try {
          if(buble != null && !buble.isDead())
            motion();
        }

        finally {
          surfaceUnlock();
        }
      }
      lastTimer = ms;
    }
  }

  private class GameButton extends Button {
    GameButton(int id, int up_image_id, int down_image_id) {
      super(id, up_image_id, down_image_id);
      setRepeatTimeout(REPEAT_TIMEOUT);
      setRepeatPeriod(REPEAT_PERIOD);
    }
  }
  
  private class Buble extends GraphicObject implements Timerable {
    Bitmap srcBmp, bmp;
    final int deltaX = 10, deltaY = 10;
    final long TRANSFORMATION_TIMEOUT = 1000;
    long lastTimer = 0;
    int transformationIndex = 0;
    Rect bmpRect;
    boolean deadState;
    int deadStage;
    final long DEAD_TIMEOUT = 50;
    final int DEAD_STEP = 20;
    final int DEAD_COLOR = 0xDEADBE;

    Buble() {
      super(-1);
      srcBmp = BitmapFactory.decodeResource(context.getResources(), R.mipmap.buble);
      bmp = null;
      bmpRect = new Rect();
      deadState = false;
    }
  
    void onModify() {
      int w = rect.width();
      int h = rect.height();
      bmpRect.set(rect);
      if(w > deltaX && h > deltaY) {
        switch (transformationIndex % 2) {
          case 0:
            w -= deltaX;
            bmpRect.left += deltaX / 2;
            break;
          case 1:
            h -= deltaY;
            bmpRect.top += deltaY / 2;
            break;
        }
      }
      bmp = Bitmap.createScaledBitmap(srcBmp, w, h, true);
      bmpRect.set(bmpRect.left,
        bmpRect.top,
        bmpRect.left + bmp.getWidth(),
        bmpRect.top + bmp.getHeight()
      );
    }
  
    @Override
    void resize(int w, int h) {
      super.resize(w, h);
      transformationIndex = 0;
      onModify();
    }

    @Override
    void moveTo(int x, int y) {
      super.moveTo(x, y);
      //transformationIndex = 0;
      onModify();
    }
    
    @Override
    public void onTimer(long ms) {
      if(deadState) {
        if(lastTimer + DEAD_TIMEOUT < ms) {
          if(deadStage > 0) {
            int new_width = rect.width();
            int new_height = rect.height();

            new_width -= DEAD_STEP;
            new_height -= DEAD_STEP;

            if(new_width <= 0 || new_height <= 0) {
              deadStage = 0;
            }
            else {
              rect.set(rect.left, rect.top, rect.left + new_width, rect.top + new_height);
              transformationIndex ++;
              onModify();
              setSurfaceModified(true);
            }

          }
          lastTimer = ms;
        }
      }
      else {
        if(lastTimer + TRANSFORMATION_TIMEOUT < ms) {
          if (rect.width() > 0 && rect.height() > 0) {
            transformationIndex ++;
            onModify();
            setSurfaceModified(true);
          }
          lastTimer = ms;
        }
      }
    }

    @Override
    public void onDraw(Canvas canvas) {
      if(bmp != null) {
        canvas.drawBitmap(bmp, bmpRect.left, bmpRect.top, paint);
      }
    }

    void dead() {
      deadState = true;
      deadStage = rect.width() / DEAD_STEP;
    }

    boolean isDead() {
      return deadState;
    }

    boolean isIntersection(Obstruction o) {
      if (bmpRect != null
        && bmp != null
        && o.getBmp() != null
        && Rect.intersects(bmpRect, o.getRect())) {

        Rect inter = new Rect(bmpRect);


        if (inter.intersect(o.getRect())) {
          int width = inter.width();
          int height = inter.height();

          int bublePixels[] = new int[width * height];
          bmp.getPixels(bublePixels,
            0,
            width,
            inter.left - bmpRect.left,
            inter.top - bmpRect.top,
            width,
            height);

          int obstructionPixels[] = new int[width * height];
          o.getBmp().getPixels(obstructionPixels,
            0,
            width,
            inter.left - o.getRect().left,
            inter.top - o.getRect().top,
            width,
            height);

          boolean intersect = false;

          for (int i = 0; i < width * height; i++) {
            if (Color.alpha(bublePixels[i]) != 0
              && Color.alpha(obstructionPixels[i]) != 0) {

              intersect = true;
              if((obstructionPixels[i] & 0xffffff) == DEAD_COLOR)
                dead();
            }
          }
          return intersect;
        }
      }

      return false;
    }
  }

  private class Obstruction extends GraphicObject {
    Bitmap bmp;

    Obstruction(Bitmap srcBmp) {
      super(-1);

      float aspectRatio = (float) srcBmp.getWidth() / (float) srcBmp.getHeight();

      int w, h;
      if(aspectRatio > 1.0) {
        w = OBSTRUCTION_LENGTH;
        h = (int)((float) w / aspectRatio);
      }
      else {
        h = OBSTRUCTION_LENGTH;
        w = (int)((float) h / aspectRatio);
      }

      srcBmp = Bitmap.createScaledBitmap(srcBmp, w, h, true);


      float angle = random.nextFloat() * 360.0f;
      Matrix matrix = new Matrix();
      matrix.postRotate(angle);
      bmp = Bitmap.createBitmap(srcBmp, 0, 0, srcBmp.getWidth(), srcBmp.getHeight(), matrix, true);

      resize(bmp.getWidth(), bmp.getHeight());
      moveTo(random.nextInt(
        fieldRect.left + rect.width() + fieldRect.width()) - rect.width(),
        -1 * rect.height());
    }

    boolean isVisible() {
      return rect.top < fieldRect.bottom;
    }


    @Override
    public void onDraw(Canvas canvas) {
      if (bmp != null) {
        canvas.clipRect(fieldRect, Region.Op.REPLACE);

        canvas.drawBitmap(bmp, rect.left, rect.top, paint);
        //restore full canvas clip for any subsequent operations
        canvas.clipRect(screenRect, Region.Op.REPLACE);
      }
    }
  
    public Bitmap getBmp() {return bmp;}
  }

}
