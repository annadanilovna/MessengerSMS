package io.fantastix.messengersms.contacts;

import java.util.ArrayList;

public class Contact {
    private String mId;
    private String mName;
    private String mPhoneNumber;
    private boolean mOnline;

    public static int lastContactId = 0;

    //    public Contact(String name, boolean online) {
//        this.mId = ++lastContactId;
//        this.mName = name;
//        this.mOnline = online;
//    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public boolean isOnline() {
        return mOnline;
    }
    public void isOnline(boolean mOnline) {
        this.mOnline = mOnline;
    }

    public String getId() {
        return mId;
    }
    public void setId(String mId) {
        this.mId = mId;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.mPhoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

//    public static ArrayList<Contact> createContactsList(int numContacts) {
//        ArrayList<Contact> contacts = new ArrayList<Contact>();
//
//        for (int i = 1; i <= numContacts; i++) {
//            contacts.add(new Contact("Person " + ++lastContactId, i <= numContacts/2));
//        }
//
//        return contacts;
//    }

    @Override
    public String toString() {
        return mName + ": " + mPhoneNumber;
    }
}
