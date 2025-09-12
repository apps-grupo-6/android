package com.android.excuses404.models;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private int userId;
    private String jwtToken;
    private String firstName;
    private String lastName;
    private String telephoneNumber;
    private String email;

    public User(int userId, String jwtToken) {
        this.userId = userId;
        this.jwtToken = jwtToken;
    }
    public int getUserId() { return userId; }
    public String getJwtToken() { return jwtToken; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getTelephoneNumber() { return telephoneNumber; }
    public String getEmail() { return email; }

    // Parcelable implementation
    protected User(Parcel in) {
        userId = in.readInt();
        jwtToken = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        telephoneNumber = in.readString();
        email = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(userId);
        dest.writeString(jwtToken);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(telephoneNumber);
        dest.writeString(email);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    private void setFirstName(String firstName){ this.firstName = firstName; }
    private void setLastName(String lastName){ this.lastName = lastName; }
    private void setTelephoneNumber(String telephoneNumber){ this.telephoneNumber = telephoneNumber; }
    private void setEmail(String email){ this.email = email; }
}
