package io.fantastix.messengersms.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.fantastix.messengersms.R;
import io.fantastix.messengersms.model.Contact;
import kotlin.reflect.KParameter;

public class ContactsListViewAdapter extends BaseAdapter implements Filterable {
    private final List<Contact> contactsList;
    private List<Contact> contactsFilteredList;
    private final Context mContext;
    private LayoutInflater inflater;

    public ContactsListViewAdapter(Context context, List<Contact> contacts) {
        this.mContext = context;
        this.contactsList = contacts;
        contactsFilteredList = contacts;
    }

    @Override
    public int getCount() {
        return contactsList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.activity_contacts_item, parent, false);
        Contact m = contactsList.get(position);
        TextView name = v.findViewById(R.id.contact_name);
        TextView number = v.findViewById(R.id.contact_number);
        ImageView photo = v.findViewById(R.id.contact_image);

        name.setText(m.getName());
        number.setText(m.getPhoneNumber());
//        photo.setImageURI(Uri.parse(m.getPhotoUri()));

        return v;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();

                if (charString.isEmpty()) {
                    contactsFilteredList.addAll(contactsList);
                }
                else {
                    List<Contact> filteredList = new ArrayList<>();

                    for (Contact contact : contactsList) {
                        if (contact.getName().toLowerCase().contains(charString.toLowerCase()) ||
                                contact.getPhoneNumber().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(contact);
                        }
                    }
                    contactsFilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = contactsFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                contactsFilteredList = (ArrayList<Contact>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
