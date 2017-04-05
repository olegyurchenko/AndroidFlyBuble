package oleg.home.ua.flybuble;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import java.util.Timer;
import java.util.TimerTask;

public class GraphicActivity extends AppCompatActivity  implements View.OnTouchListener {

  GraphicController graphicController;
  DrawView drawView;
  Timer myTimer;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(getLayoutId());

    drawView = new DrawView(this);

    /* Insert main view*/
    LinearLayout layout = (LinearLayout) findViewById(R.id.draw_layout);
    if (layout != null) {
      layout.addView(drawView);
    }

    graphicController = (GraphicController) getLastCustomNonConfigurationInstance();

    if(graphicController == null) {
      graphicController = newGraphicController();
    }

    drawView.setSurfaceCallbacs(graphicController);
    drawView.setOnTouchListener(this);

  }

  int getLayoutId() {
    return R.layout.activity_graphic;
  }

  GraphicController newGraphicController() {
    return new GraphicController(this);
  }

  @Override
  protected void onResume()
  {
    Log.d("Activity", "onResume()");

    super.onResume();
    graphicController.onResume();

    myTimer = new Timer(); // Создаем таймер
    myTimer.schedule(new TimerTask() { // Определяем задачу
      @Override
      public void run() {
        graphicController.onTimer();
      }

    }, 100, 10); // интервал - 100 миллисекунд, 0 миллисекунд до первого запуска.

    Log.d("Activity", "onResume() end");
  }

  @Override
  protected void onPause()
  {
    Log.d("BaseActivity", "onPause()");
    super.onPause();
    graphicController.onPause();
    myTimer.cancel();
    Log.d("BaseActivity", "onPause() end");
  }

  @Override
  public Object onRetainCustomNonConfigurationInstance() {
    return graphicController;
  }

  @Override
  public boolean onTouch(View v, MotionEvent event) {
    int actionMask = event.getActionMasked();
    int pointerIndex = event.getActionIndex();
    int pointerId = event.getPointerId(pointerIndex);
    int pointerCount = event.getPointerCount();
    float x = event.getX(pointerIndex);
    float y = event.getY(pointerIndex);

    switch (actionMask) {
      case MotionEvent.ACTION_DOWN: // нажатие
      case MotionEvent.ACTION_POINTER_DOWN:
        graphicController.onTouchDown(pointerId, x, y);
        break;
      case MotionEvent.ACTION_MOVE: // движение
        for(int i = 0; i < pointerCount; i++)
        {
          x = event.getX(i);
          y = event.getY(i);
          pointerId = event.getPointerId(i);
          graphicController.onTouchMove(pointerId, x, y);
        }
        break;
      case MotionEvent.ACTION_UP: // отпускание
      case MotionEvent.ACTION_CANCEL:
      case MotionEvent.ACTION_POINTER_UP:
        graphicController.onTouchUp(pointerId, x, y);
        break;
    }
    return true;
  }

}
