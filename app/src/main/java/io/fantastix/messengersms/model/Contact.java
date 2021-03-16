package io.fantastix.messengersms.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Contact implements Serializable {
    private long mId;
    private String mName;
    private String mPhoneNumber;
    private boolean mOnline;
    public String photoUri = "";
    public List<String> allPhoneNumber = new ArrayList<>();

    public Contact() {
    }

    public Contact(long id, String name, String number, String photoUri, List<String> allPhoneNumber) {
        this.mId = id;
        this.mName = name;
        this.mPhoneNumber = number;
        this.photoUri = photoUri;
        this.allPhoneNumber = allPhoneNumber;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public boolean isOnline() {
        return mOnline;
    }
    public void isOnline(boolean mOnline) {
        this.mOnline = mOnline;
    }

    public long getId() {
        return mId;
    }

    public void setId(long mId) {
        this.mId = mId;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.mPhoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    @Override
    public String toString() {
        return mName + ": " + mPhoneNumber;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public List<String> getAllPhoneNumbers() {
        return allPhoneNumber;
    }

    public void setAllPhoneNumber(List<String> allPhoneNumber) {
        this.allPhoneNumber = allPhoneNumber;
    }
}
