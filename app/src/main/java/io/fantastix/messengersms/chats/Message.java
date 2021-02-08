package io.fantastix.messengersms.chats;

public class Message {
    private String _id;
    private String _address;
    private String _nickname;
    private String _message;
    private String _readState; //"0" for have not read sms and "1" for have read sms
    private String _time;
    private String _folderName;

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
    public String getTime(){
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
    public void setMsg(String msg){
        _message = msg;
    }
    public void setReadState(String readState){
        _readState = readState;
    }
    public void setTime(String time){
        _time = time;
    }
    public void setFolderName(String folderName){
        _folderName = folderName;
    }

}
