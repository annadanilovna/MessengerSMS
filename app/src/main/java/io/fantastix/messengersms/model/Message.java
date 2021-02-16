package io.fantastix.messengersms.model;

import java.io.Serializable;
import java.util.Date;

public class Message implements MessageType, Serializable {
    private String _id;
    private String _address;
    private String _nickname;
    private String _message;
    private String _readState; //"0" for have not read sms and "1" for have read sms
    private long _time = -1;
    private String _folderName;

    private long threadId = -1;
    private long msgCount = -1;
    private boolean read = false;
    private Contact contact;
    private String snippet = null;
    private String recipient;

    public Message() {
        threadId = -1;
        _time = -1;
        msgCount = -1;
        read = false;
        contact = null;
        recipient = null;
    }

    public Message(long threadId, long time, long msgCount, boolean read, Contact contact, String snippet, String recipient) {
        this.threadId = threadId;
        this._time = time;
        this.msgCount = msgCount;
        this.read = read;
        this.contact = contact;
        this.snippet = snippet;
        this.recipient = recipient;
    }

    @Override
    public String toString() {
        return _message;
    }

    public String getId(){
        return _id;
    }
    public String getAddress(){
        return _address;
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
    public String getReadState(){
        return _readState;
    }
    public long getTime(){
        return _time;
    }
    public String getFolderName(){
        return _folderName;
    }
    public void setId(String id){
        _id = id;
    }
    public void setAddress(String address){
        _address = address;
    }
    public void setReadState(String readState){
        _readState = readState;
    }
    public void setTime(long time) {
        _time = time;
    }
    public void setFolderName(String folderName){
        _folderName = folderName;
    }

    public String getRecipient() {
        return recipient;
    }
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public Contact getContact() {
        return contact;
    }
    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public long getThreadId() {
        return threadId;
    }
    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public long getMsgCount() {
        return msgCount;
    }
    public void setMsgCount(long msgCount) {
        this.msgCount = msgCount;
    }

    public boolean isRead() {
        return read;
    }
    public void setRead(boolean read) {
        this.read = read;
    }

    public String getSnippet() {
        return snippet;
    }
    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    @Override
    public int getMessageType() {
        return MessageType.MESSAGE;
    }
}

