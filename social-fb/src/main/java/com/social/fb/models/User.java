package com.social.fb.models;

/**
 * Created by CS02 on 11/8/2016.
 */

public class User {
  private String id;
  private String name;
  private String dob;
  private String email;
  private String bio;
  private String profileImage;
  private String profileImageLocal;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDob() {
    return dob;
  }

  public void setDob(String dob) {
    this.dob = dob;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getBio() {
    return bio;
  }

  public void setBio(String bio) {
    this.bio = bio;
  }

  public String getProfileImage() {
    return profileImage;
  }

  public void setProfileImage(String profileImage) {
    this.profileImage = profileImage;
  }

  public String getProfileImageLocal() {
    return profileImageLocal;
  }

  public void setProfileImageLocal(String profileImageLocal) {
    this.profileImageLocal = profileImageLocal;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Id : ").append(id).append("\n");
    builder.append("Name : ").append(name).append("\n");
    builder.append("Dob : ").append(dob).append("\n");
    builder.append("Email : ").append(email).append("\n");
    builder.append("Bio : ").append(bio).append("\n");
    builder.append("ProfileImage : ").append(profileImage).append("\n");
    builder.append("ProfileImageLocal : ").append(profileImageLocal);
    return builder.toString();
  }
}
