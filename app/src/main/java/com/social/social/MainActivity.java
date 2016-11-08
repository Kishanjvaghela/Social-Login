package com.social.social;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.social.fb.FaceBookHelper;
import com.social.fb.FacebookLoginListener;
import com.social.fb.models.User;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements FacebookLoginListener {

  private FaceBookHelper faceBookHelper;
  private TextView statusTextView, resultTextView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    faceBookHelper = new FaceBookHelper(this);
    faceBookHelper.setListener(this);
    statusTextView = (TextView) findViewById(R.id.status);
    resultTextView = (TextView) findViewById(R.id.resultText);
    Button button = (Button) findViewById(R.id.login);
    button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        loginWithFb();
      }
    });
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    faceBookHelper.onActivityResult(requestCode, resultCode, data);
  }

  private void loginWithFb() {
    faceBookHelper.login(Arrays.asList("email", "public_profile", "user_birthday", "user_about_me"),
        true);
  }

  @Override
  public void onLogin(User user) {
    statusTextView.setText(R.string.success);
    resultTextView.setText(user.toString());
  }

  @Override
  public void onError(String error) {
    statusTextView.setText(R.string.error);
    if (error != null) resultTextView.setText(error);
  }
}
