package io.fantastix.messengersms.model;

public interface MessageType {
    int MESSAGE = 1;
    int EMOJI = 2;
    int IMAGE = 3;

    int getMessageType();
}
