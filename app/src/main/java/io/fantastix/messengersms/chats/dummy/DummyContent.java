package io.fantastix.messengersms.chats.dummy;

import io.fantastix.messengersms.model.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<Message> ITEMS = new ArrayList<Message>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<Long, Message> ITEM_MAP = new HashMap<Long, Message>();

    private static final int COUNT = 25;

//    static {
//        // Add some sample items.
//        for (int i = 1; i <= COUNT; i++) {
//            addItem(createDummyItem(i));
//        }
//    }

    private static void addItem(Message item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.getId(), item);
    }

//    private static Message createDummyItem(int position) {
//        return new Message(String.valueOf(position), "Message " + position, makeDetails(position));
//    }

//    private static String makeDetails(int position) {
//        StringBuilder builder = new StringBuilder();
//        builder.append("Details about Item: ").append(position);
////        for (int i = 0; i < position; i++) {
//            builder.append("\nMore details information here.");
////        }
//        return builder.toString();
//    }
}