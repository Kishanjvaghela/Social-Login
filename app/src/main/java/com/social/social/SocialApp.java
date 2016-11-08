package com.social.social;

import android.app.Application;
import com.social.fb.FaceBookHelper;

/**
 * Created by CS02 on 11/8/2016.
 */

public class SocialApp extends Application {
  @Override
  public void onCreate() {
    super.onCreate();
    // TODO init with your id
    FaceBookHelper.init(this, Conts.FB_ID);
  }
}
