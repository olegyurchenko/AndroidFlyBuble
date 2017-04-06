package oleg.home.ua.flybuble;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Class implements game logic
 */


public class GraphicController implements DrawView.SurfaceCallbacs {

  private boolean surfaceModified;
  protected  Context context;
  protected  Paint paint;
  protected  Rect rect;
  protected ReentrantLock lock;
  protected ArrayList<Object> childList;
  protected ClickListener clickListener = null;
  private boolean active;


  GraphicController(Context c) {
    context = c;
    surfaceModified = true;
    paint = new Paint();
    lock = new ReentrantLock();
    rect = new Rect();
    childList = new ArrayList<>();
    active = false;
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
    rect.set(0, 0, w, h);
    geometrySetup();
  }
  
  @Override
  public void surfaceDraw(Canvas canvas) {

    backgroundDraw(canvas);
    for(Object o : childList) {
      Drawable d = null;
      try {
        d = (Drawable) o;
      }
      catch (Exception ignored) {
        
      }
      if(o != null)
        d.onDraw(canvas);
    }
/*
    Rect rect = new Rect();
    String text = String.format("%d", System.currentTimeMillis());
    rect.set(0, 0, width, height);
    paint.setColor(Color.WHITE);
    paint.setStyle(Paint.Style.FILL);
    canvas.drawRect(rect, paint);

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
*/
  }

  protected void backgroundDraw(Canvas canvas) {
  }

  @Override
  public void surfaceLock() {
    lock.lock();
  }
  
  @Override
  public void surfaceUnlock() {
    lock.unlock();
  }

  void setActive(boolean a) {active = a;}
  boolean isActive() {return active;}
  
  void onResume() {
    setSurfaceModified(true);
    setActive(true);
  }


  void onPause() {
    setActive(false);
  }

  void onTimer() {
    long ms = System.currentTimeMillis();
    for(Object o : childList) {
      Timerable t = null;
      try {
        t = (Timerable) o;
      }
      catch (Exception ignored) {
        
      }

      if(t != null)
        t.onTimer(ms);
    }
  }
  
  void onTouchDown(int id, float fx, float fy) {
    for(Object o : childList) {
      Button b = null;
      try {
        b = (Button) o;
      }
      catch (Exception ignored) {
        
      }
      if(b != null) {
        if(!b.isDown() && b.contains(fx, fy)) {
          b.onTouchDown(id);
          setSurfaceModified(true);
        }
      }
    }


  }
  
  void onTouchUp(int id, float x, float y) {
    for(Object o : childList) {
      Button b = null;
      try {
        b = (Button) o;
      }
      catch (Exception ignored) {
        
      }
      if(b != null) {
        if(b.isDown() && b.getTouchId() == id) {
          b.onTouchUp();
          setSurfaceModified(true);
        }
      }
    }
  }
  
  void onTouchMove(int id, float fx, float fy) {

    for(Object o : childList) {
      Button b = null;
      try {
        b = (Button) o;
      }
      catch (Exception ignored) {
        
      }
      
      if(b != null) {
        if(b.isDown() && b.getTouchId() == id && !b.contains(fx, fy)) {
          b.onTouchUp();
          setSurfaceModified(true);
        }
        if(!b.isDown() && b.contains(fx, fy)) {
          b.onTouchDown(id);
          setSurfaceModified(true);
        }
      }
    }

  }
  
  protected void removeChild(Object o) {
    surfaceLock();
    try {
      childList.remove(o);
    }
    
    finally {
      surfaceUnlock();
    }
  }
  
  protected void geometrySetup() {
    //Pure virtual
  }

  public void setClickListener(ClickListener l) {
    clickListener = l;
  }

  protected void onClick(int id) {
    if(isActive() && clickListener != null)
      clickListener.onClick(id);
  }


  public interface Drawable {
    public void onDraw(Canvas canvas);
  }

  public interface Timerable {
    public void onTimer(long ms);
  }

  public interface ClickListener {
    public void onClick(int id);
  }


  class GraphicObject implements Drawable {
    private int id;
    Rect rect;

    GraphicObject(int id) {
      this.id = id;
      childList.add(this);
      rect = new Rect();
    }

    int getId() {return id;}
    Rect getRect() {return rect;}
    void move(int x, int y) {rect.offsetTo(x, y);}
    void resize(int w, int h) {rect.set(rect.left, rect.top, rect.left + w, rect.top + h);}
    boolean contains (int x, int y) {return  rect.contains(x, y);}
    boolean contains (float x, float y) {return  rect.contains((int)x, (int)y);}

    @Override
    public void onDraw(Canvas canvas) {

    }
  }

  enum  ButtonState {
    UP,
    DOWN
  }

  class Button extends GraphicObject implements Timerable {
    Bitmap bmp = null;
    Bitmap upBmp, downBmp;
    ButtonState state;
    long repeatTimeout = 3000;
    long repeatPeriod = 500;
    long lastTimer, timeout;
    int touchId;



    Button(int id, int up_image_id, int down_image_id) {
      super(id);
      state = ButtonState.UP;
      upBmp = BitmapFactory.decodeResource(context.getResources(), up_image_id);
      downBmp = BitmapFactory.decodeResource(context.getResources(), down_image_id);
      timeout = repeatTimeout;
    }

    ButtonState getState() {return state;}
    boolean isDown() {return state == ButtonState.DOWN;}

    void setState(ButtonState s) {
      if(state != s) {
        state = s;
        onModify();
        if(s == ButtonState.DOWN) {
          onClick(getId());
          lastTimer = 0;
          timeout = repeatTimeout;
        }
      }
    }
    
    void setRepeatTimeout(long t) {
      repeatTimeout = t;
    }
    
    void setRepeatPeriod(long t) {
      repeatPeriod = t;
    }

    void onModify() {
      Bitmap src = null;
      bmp = null;
      if(state == ButtonState.UP) {
        src = upBmp;
      }
      if(state == ButtonState.DOWN) {
        src = downBmp;
      }

      if(src != null)
        bmp = Bitmap.createScaledBitmap(src, rect.width(), rect.height(), true);

    }

    @Override
    void resize(int w, int h) {
      super.resize(w, h);
      onModify();
    }

    @Override
    public void onDraw(Canvas canvas) {
      if(bmp != null) {
        canvas.drawBitmap(bmp, rect.left, rect.top, paint);
      }

    }

    @Override
    public void onTimer(long ms) {
  
      if(lastTimer + timeout < ms) {
    
        if(state == ButtonState.DOWN) {
          timeout = repeatPeriod;
          onClick(getId());
        }
    
        lastTimer = ms;
      }

    }

    int getTouchId() {return touchId;}

    void onTouchDown() {
      setState(ButtonState.DOWN);
    }
    void onTouchDown(int id) {
      if(!isDown())
        touchId = id;
      setState(ButtonState.DOWN);
    }

    void onTouchUp() {
      setState(ButtonState.UP);
    }
    void onTouchUp(int id) {
      if(isDown() && id == touchId)
        setState(ButtonState.UP);
    }
  }


}
