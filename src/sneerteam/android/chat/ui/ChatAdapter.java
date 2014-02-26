package sneerteam.android.chat.ui;

import java.util.List;

import sneerteam.android.chat.Message;
import sneerteam.android.chat.R;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ChatAdapter extends ArrayAdapter<Message>{

    Context context; 
    int layoutUserResourceId;    
    int listContactResourceId;
    List<Message> data = null;
    
    public ChatAdapter(Context context, int layoutUserResourceId, int listContactResourceId, List<Message> data) {
        super(context, layoutUserResourceId, data);
        this.layoutUserResourceId = layoutUserResourceId;
        this.listContactResourceId = listContactResourceId;
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
            if (position%2 == 0)
            	row = inflater.inflate(layoutUserResourceId, parent, false);
            else
            	row = inflater.inflate(listContactResourceId, parent, false);
            
            holder = new ContactHolder();
            holder.messageText = (TextView)row.findViewById(R.id.messageText);
            
            row.setTag(holder);
        }
        else
        {
            holder = (ContactHolder)row.getTag();
        }
        
        Message message = data.get(position);
        holder.messageText.setText(message.toString());
        
        return row;
    }
    
    static class ContactHolder
    {
        TextView messageText;
    }
}