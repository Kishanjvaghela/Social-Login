package com.social.fb;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.social.fb.downloader.ImageDownloaderTask;
import com.social.fb.downloader.ImageException;
import com.social.fb.utils.Utils;
import java.util.Arrays;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by CS02 on 11/8/2016.
 */

public class FaceBookHelper {
  public static final String TAG = "FaceBookHelper";
  CallbackManager callbackManager;
  ShareDialog shareDialog;
  Activity activity;
  FacebookLoginListener listener;

  public FaceBookHelper(Activity activity) {
    this.activity = activity;
    callbackManager = CallbackManager.Factory.create();
  }

  public void setListener(FacebookLoginListener listner) {
    this.listener = listner;
  }

  public void login() {
    LoginManager.getInstance()
        .registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
          @Override
          public void onSuccess(LoginResult loginResult) {
            Log.d(TAG, loginResult.toString());
            getData(loginResult.getAccessToken().getUserId());
            // App code
          }

          @Override
          public void onCancel() {
            // App code
            onLoginError(null);
          }

          @Override
          public void onError(FacebookException exception) {
            // App code
            Log.d(TAG, exception.toString());
            onLoginError(exception.toString());
          }
        });
    LoginManager.getInstance()
        .logInWithReadPermissions(activity,
            Arrays.asList("email", "public_profile", "user_birthday", "user_about_me"));
  }

  private void getData(final String userid) {
    new GraphRequest(AccessToken.getCurrentAccessToken(), "/" + userid, null, HttpMethod.GET,
        new GraphRequest.Callback() {
          public void onCompleted(GraphResponse response) {
            /* handle the result */
            Log.d(TAG, response.toString());
            JSONObject object = response.getJSONObject();
            if (object != null) {
              try {
                String name = (String) object.get("name");
                String dob = null;
                if (object.has("birthday")) {
                  dob = (String) object.get("birthday"); // MM/DD/YYYY
                }
                String aboutMe = object.has("bio") ? (String) object.get("bio") : "";
                String email = object.has("email") ? (String) object.get("email") : "";
                downloadImage(userid, name, dob, aboutMe, email);
              } catch (JSONException e) {
                e.printStackTrace();
                onLoginError();
              }
            } else {
              onLoginError();
            }
          }
        }).executeAsync();
  }

  private void downloadImage(final String userId, final String name, final String dob,
      final String aboutMe, final String email) {
    String imageUrl = "https://graph.facebook.com/" + userId + "/picture?type=large";
    new ImageDownloaderTask(imageUrl, activity.getCacheDir().getAbsolutePath(), userId) {
      @Override
      protected void onPostExecute(Exception e) {
        super.onPostExecute(e);
        if (e == null) {
          if (listener != null) {
            listener.onLogin(userId, name, dob, aboutMe, getFinalImagePath(), email);
          }
        } else {
          if (e instanceof ImageException) {
            onLoginError(e.getMessage());
          } else {
            onLoginError();
          }
        }
      }
    }.execute();
  }

  private void onLoginError() {
    onLoginError("Please try again");
  }

  private void onLoginError(String error) {
    if (listener != null) {
      listener.onError(error);
    }
  }

  public void shareOnFacebook(String title, String desciption, String url) {
    shareDialog = new ShareDialog(activity);
    shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
      @Override
      public void onSuccess(Sharer.Result result) {

      }

      @Override
      public void onCancel() {

      }

      @Override
      public void onError(FacebookException e) {

      }
    });

    if (ShareDialog.canShow(ShareLinkContent.class)) {
      ShareLinkContent.Builder linkContent = new ShareLinkContent.Builder();
      linkContent.setContentTitle(title);
      if (!TextUtils.isEmpty(url)) {
        linkContent.setContentDescription(desciption);
        linkContent.setContentUrl(Uri.parse(url));
        shareDialog.show(linkContent.build());
      } else {
        Utils.makeToast(activity, R.string.no_url_found);
      }
    } else {
      Utils.makeToast(activity, R.string.please_try_again);
    }
  }

  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    callbackManager.onActivityResult(requestCode, resultCode, data);
  }
}