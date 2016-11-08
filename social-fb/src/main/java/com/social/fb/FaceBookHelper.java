package com.social.fb;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
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
import com.social.fb.models.User;
import com.social.fb.utils.Utils;
import java.util.Collection;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by CS02 on 11/8/2016.
 */

public class FaceBookHelper {
  private static final String TAG = "FaceBookHelper";
  private CallbackManager callbackManager;
  private ShareDialog shareDialog;
  private Activity activity;
  private FacebookLoginListener listener;

  public FaceBookHelper(Activity activity) {
    this.activity = activity;
    callbackManager = CallbackManager.Factory.create();
  }

  public static void init(Context context, String fbId) {
    FacebookSdk.sdkInitialize(context);
    FacebookSdk.setApplicationId(fbId);
  }

  public void setListener(FacebookLoginListener listener) {
    this.listener = listener;
  }

  public void login(Collection<String> permissions) {
    login(permissions, false);
  }

  public void login(Collection<String> permissions, final boolean isImageDownload) {
    if (FacebookSdk.getApplicationId() == null) {
      throw new RuntimeException("Facebook app id not available.");
    }
    LoginManager.getInstance()
        .registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
          @Override
          public void onSuccess(LoginResult loginResult) {
            Log.d(TAG, loginResult.toString());
            getData(loginResult.getAccessToken().getUserId(), isImageDownload);
          }

          @Override
          public void onCancel() {
            onLoginError(null);
          }

          @Override
          public void onError(FacebookException exception) {
            Log.d(TAG, exception.toString());
            onLoginError(exception.toString());
          }
        });
    LoginManager.getInstance().logInWithReadPermissions(activity, permissions);
  }

  public void logout() {
    LoginManager.getInstance().logOut();
  }

  private void getData(final String userId, final boolean isImageDownload) {
    new GraphRequest(AccessToken.getCurrentAccessToken(), "/" + userId, null, HttpMethod.GET,
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
                User user = new User();
                user.setId(userId);
                user.setName(name);
                user.setDob(dob);
                user.setBio(aboutMe);
                user.setEmail(email);
                if (isImageDownload) {
                  downloadImage(userId, user);
                } else {
                  user.setProfileImage(generateImagePath(userId));
                  onLoginSuccess(user);
                }
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

  private void downloadImage(final String userId, final User user) {
    final String imageUrl = generateImagePath(userId);
    new ImageDownloaderTask(imageUrl, activity.getCacheDir().getAbsolutePath(), userId) {
      @Override
      protected void onPostExecute(Exception e) {
        super.onPostExecute(e);
        if (e == null) {
          user.setProfileImage(imageUrl);
          user.setProfileImageLocal(getFinalImagePath());
          onLoginSuccess(user);
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

  private String generateImagePath(String userId) {
    return "https://graph.facebook.com/" + userId + "/picture?type=large";
  }

  private void onLoginSuccess(User user) {
    if (listener != null) {
      listener.onLogin(user);
    }
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