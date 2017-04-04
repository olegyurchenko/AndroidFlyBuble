package oleg.home.ua.flybuble;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

/**
 * Class to implement game view
 */

public class DrawView extends SurfaceView implements Callback {
  private DrawThread drawThread;
  private SurfaceCallbacs surfaceCallbacs = null;

  public DrawView(Context context) {
    super(context);
    getHolder().addCallback(this);
  }

  @Override
  public void surfaceChanged(SurfaceHolder holder, int format, int width,
                             int height) {

  }

  @Override
  public void surfaceCreated(SurfaceHolder holder) {
    drawThread = new DrawThread(getHolder());
    drawThread.setRunning(true);
    drawThread.start();
  }

  @Override
  public void surfaceDestroyed(SurfaceHolder holder) {
    boolean retry = true;
    drawThread.setRunning(false);
    while (retry) {
      try {
        drawThread.join();
        retry = false;
      }
      catch (InterruptedException ignored) {
      }
    }
  }

  public void setSurfaceCallbacs(SurfaceCallbacs sc) {
    surfaceCallbacs = sc;
  }
  
  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    if(surfaceCallbacs != null)
      surfaceCallbacs.surfaceSetSize(w, h);
  }
  
  
  private class DrawThread extends Thread {

    private boolean running = false;
    private SurfaceHolder surfaceHolder;

    DrawThread(SurfaceHolder surfaceHolder) {
      this.surfaceHolder = surfaceHolder;
    }

    void setRunning(boolean running) {
      this.running = running;
    }

    @Override
    public void run() {
      Canvas canvas;
      SurfaceCallbacs sc;
      while (running) {
        canvas = null;
        sc = surfaceCallbacs;
          
        try {
          if(sc == null
            || !sc.isSurfaceModified()
            ) {
            sleep(10);
            continue;
          }

          canvas = surfaceHolder.lockCanvas(null);

          if(canvas != null) {
            sc.surfaceLock();
            sc.surfaceDraw(canvas);
            sc.surfaceUnlock();
            sc.setSurfaceModified(false);
          }
          
        }
        catch (Exception ignored) {
          
        }
        finally {
          if (canvas != null) {
            surfaceHolder.unlockCanvasAndPost(canvas);
          }
        }
      }
    }
  }

  public interface SurfaceCallbacs {
  
    public boolean isSurfaceModified();
    public void setSurfaceModified(boolean m);
    public void surfaceDraw(Canvas canvas);
    public void surfaceLock();
    public void surfaceUnlock();
    public void surfaceSetSize(int w, int h);
    
  }

}

