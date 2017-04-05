package oleg.home.ua.flybuble;

import android.content.Context;

/**
 * Implement game main page.
 */

public class MainController extends GraphicController {
  public final int PLAY_BTN_ID = 1;
  public final int SETTINGS_BTN_ID = 2;
  private Button playBtn, settingsBtn;
  MainController(Context c) {
    super(c);

    playBtn = new Button(PLAY_BTN_ID, R.mipmap.play_btn_up, R.mipmap.play_btn_down);
    settingsBtn = new Button(SETTINGS_BTN_ID, R.mipmap.settings_btn_up, R.mipmap.settings_btn_down);
  }

  @Override
  protected void geometrySetup() {

    int d = Math.min(rect.width() / 3, rect.height() / 3);

    int x = rect.width() / 3 - d / 2;
    int y = rect.height() / 3 - d / 2;

    playBtn.move(x, y);
    playBtn.resize(d, d);

    x = rect.width() / 3 - d / 2;
    y = rect.height() / 3 + d;

    settingsBtn.move(x, y);
    settingsBtn.resize(d, (d * 2) / 3);
  }

}
