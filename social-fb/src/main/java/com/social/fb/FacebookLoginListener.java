package com.social.fb;

/**
 * Created by CS02 on 11/8/2016.
 */

public interface FacebookLoginListener {
  void onLogin(String fbId, String name, String dob, String aboutMe, String imageLocalPath,
      String email);

  void onError(String error);
}