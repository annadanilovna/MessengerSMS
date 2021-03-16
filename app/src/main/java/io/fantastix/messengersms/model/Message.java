package io.fantastix.messengersms.model;

import android.content.Context;

import java.io.Serializable;
import java.util.Date;

public class Message implements MessageType, Serializable {
    private long _id;
    private long _thread_id;
    private String _address;
    private String _nickname;
    private String _recipient;
    private Contact _contact = new Contact();
    private String _message;
    private String _snippet = null;
    private long _time = -1;
    private String _folderName;
    private long _count = -1;
    private int _readState; //"0" for have not read sms and "1" for have read sms
    private boolean _read = false;
    private boolean _seen = false;
    public static final Object MESSAGE_TYPE_SMS = 1;
    private int _type;
    private Context context;

    public Message() {
        _thread_id = -1;
        _time = -1;
        _count = -1;
        _read = false;
        _contact = null;
        _recipient = null;
    }

    public Message(long threadId, long time, long msgCount, boolean read, Contact contact, String snippet, String recipient) {
        this._thread_id = threadId;
        this._time = time;
        this._count = msgCount;
        this._read = read;
        this._contact = contact;
        this._snippet = snippet;
        this._recipient = recipient;
    }
    public Message(Context context, String address, String contactId_string, String body, long timestamp, long threadId, int count, long messageId, Object messageTypeSms) {
        this.context = context;
        this._address = address;
        this._contact.setId(Long.parseLong(contactId_string));
        this._message = body;
        this._time = timestamp;
        this._thread_id = threadId;
        this._count = count;
        this._id = messageId;
    }


    @Override
    public String toString() {
        return _message;
    }

    public long getId(){
        return _id;
    }
    public void setId(long id){ _id = id; }
    public String getAddress(){
        return _address;
    }
    public void setAddress(String address){
        _address = address;
    }
    public String getNickname() {
        return _nickname;
    }
    public void setNickname(String nickname) {
        this._nickname = nickname;
    }
    public String getMessage() {
        return _message;
    }
    public void setMessage(String message) {
        this._message = message;
    }
    public long getTime(){
        return _time;
    }
    public void setTime(long time) {
        _time = time;
    }
    public String getFolderName(){
        return _folderName;
    }
    public void setFolderName(String folderName){
        _folderName = folderName;
    }

    public String getRecipient() {
        return _recipient;
    }
    public void setRecipient(String recipient) {
        this._recipient = recipient;
    }

    public Contact getContact() {
        return _contact;
    }
    public void setContact(Contact contact) {
        this._contact = contact;
    }

    public long getThreadId() {
        return _thread_id;
    }
    public void setThreadId(long threadId) {
        this._thread_id = threadId;
    }

    public long getMsgCount() {
        return _count;
    }
    public void setMsgCount(long msgCount) {
        this._count = msgCount;
    }

    public int getReadState(){
        return _readState;
    }
    public void setReadState(int readState){
        _readState = readState;
    }
    public void setRead(boolean read) { this._read = read; }
    public boolean isRead() { return _readState == 1; }

    public boolean isSeen() { return _seen; }
    public void setSeenState(boolean seen) { this._seen = seen; }

    public String getSnippet() {
        return _snippet;
    }
    public void setSnippet(String snippet) {
        this._snippet = snippet;
    }
    public void setType(int type) {
        this._type = type;
    }
    public int getType() {
        return this._type;
    }

    @Override
    public int getMessageType() {
        return MessageType.MESSAGE;
    }
}
