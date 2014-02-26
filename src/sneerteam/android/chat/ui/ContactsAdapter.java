package sneerteam.android.chat.ui;

import java.util.List;

import sneerteam.android.chat.Contact;
import sneerteam.android.chat.R;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ContactsAdapter extends ArrayAdapter<Contact>{

    Context context; 
    int layoutResourceId;    
    List<Contact> data = null;
    
    public ContactsAdapter(Context context, int layoutResourceId, List<Contact> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ContactHolder holder = null;
        
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new ContactHolder();
            holder.contactName = (TextView)row.findViewById(R.id.contactName);
            
            row.setTag(holder);
        }
        else
        {
            holder = (ContactHolder)row.getTag();
        }
        
        Contact contact = data.get(position);
        holder.contactName.setText(contact.getName());
        
        return row;
    }
    
    static class ContactHolder
    {
        TextView contactName;
    }
}