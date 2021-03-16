package io.fantastix.messengersms.viewmodel;

import android.content.Context;
import android.os.Handler;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import io.fantastix.messengersms.Utils;
import io.fantastix.messengersms.model.Message;

public class SmsViewModel extends ViewModel {
    private String TAG = SmsViewModel.class.getSimpleName();

    private MutableLiveData<List<Message>> messages;
    public LiveData<List<Message>> getMessages() {
        if (messages == null) {
            messages = new MutableLiveData<>();
//            loadUsers();
        }
        return messages;
    }

//    public void addAll(List<Message> msg) {
//        messages.setValue(msg);
//    }

    public void loadUsers(Context context) {
//        Handler myHandler = new Handler();
//        myHandler.postDelayed(() -> {
//            List<Message> shoppingListSample = new ArrayList<>();
//            Message message = new Message();
//            message.setMessage("Bread");
//            List<Message> messages = Utils.retrieveSms(context);

//            messages.add(message);
//            shoppingListSample.add("Bananas");
//            shoppingListSample.add("Peanut Butter");
//            shoppingListSample.add("Eggs");
//            shoppingListSample.add("Chicken breasts");

//            long seed = System.nanoTime();
//            Collections.shuffle(shoppingListSample, new Random(seed));

//            messages.setValue(shoppingListSample);

//        }, 5000);

    }
}
