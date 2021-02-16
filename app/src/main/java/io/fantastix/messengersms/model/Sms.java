package io.fantastix.messengersms.model;

public class Sms {
    private String _id;
    private Contact _address;
    private Contact _contact = new Contact();
    private String _msg;
    private int _readState; //"0" for have not read sms and "1" for have read sms
    private long _time;
    private String _folderName;

    public String getId(){
        return _id;
    }
//    public String getAddress(){
//        return _address;
//    }
    public String getMsg(){
        return _msg;
    }
    public int getReadState(){
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
//    public void setAddress(String address){
//        _address = address;
//    }
    public void setMsg(String msg){
        _msg = msg;
    }
    public void setReadState(int readState){
        _readState = readState;
    }
    public void setTime(long time){
        _time = time;
    }
    public void setFolderName(String folderName){
        _folderName = folderName;
    }

    public Contact getContact() {
        return _contact;
    }

    public void setContact(Contact contact) {
        this._contact = contact;
    }
}
