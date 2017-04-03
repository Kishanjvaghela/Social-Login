package com.social.fb;

import com.social.fb.models.User;

/**
 * Created by CS02 on 11/8/2016.
 */

public interface FacebookLoginListener {
  void onLogin(User user, String token);

  void onError(String error);
}