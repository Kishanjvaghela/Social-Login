package com.social.google;

import com.social.google.models.User;

/**
 * Created by CS02 on 11/9/2016.
 */

public interface GoogleLoginListener {
  void onLogin(User user);

  void onError(String error);
}
