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
      while (running) {
        canvas = null;
        try {
          canvas = surfaceHolder.lockCanvas(null);
          if (canvas == null)
            continue;
          canvas.drawColor(Color.GREEN);
        } finally {
          if (canvas != null) {
            surfaceHolder.unlockCanvasAndPost(canvas);
          }
        }
      }
    }
  }

}

