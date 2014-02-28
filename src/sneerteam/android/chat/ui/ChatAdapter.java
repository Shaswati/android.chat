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
	private String sender;
    
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
        
        Message message = data.get(position);
        
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        if (sender != null && sender.equals(message.sender()))
        	row = inflater.inflate(layoutUserResourceId, parent, false);
        else
        	row = inflater.inflate(listContactResourceId, parent, false);
        
        TextView messageText = (TextView)row.findViewById(R.id.messageText);
        
        messageText.setText(message.toString());
        
        return row;
    }
    
	public boolean hasSender() {
		return sender != null;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}
}