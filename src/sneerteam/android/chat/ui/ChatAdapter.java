package sneerteam.android.chat.ui;

import java.util.List;
import java.util.Random;

import sneerteam.android.chat.Message;
import sneerteam.android.chat.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
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
        if (isMine(message))
        	row = inflater.inflate(layoutUserResourceId, parent, false);
        else
        	row = inflater.inflate(listContactResourceId, parent, false);
        
        TextView messageContent = (TextView)row.findViewById(R.id.messageContent);
        TextView messageSender = (TextView)row.findViewById(R.id.messageSender);
        TextView messageTime = (TextView)row.findViewById(R.id.messageTime);
        
        messageContent.setText(message.content());
        messageSender.setText(message.sender());
        if (!isMine(message)) messageSender.setTextColor(pastelColorDeterminedBy(message.sender()));
        messageTime.setText(message.time());
        
        return row;
    }

	private boolean isMine(Message message) {
		return sender != "" && sender.equals(message.sender());
	}
    
	public boolean hasSender() {
		return sender != "";
	}

	public void setSender(String sender) {
		this.sender = sender;
	}
	
	private static int pastelColorDeterminedBy(String string) {
		Random random = new Random(string.hashCode() * 713);
		int r = 50 + random.nextInt(106);
		int g = 50 + random.nextInt(106);
		int b = 50 + random.nextInt(106);
		return Color.argb(255, r, g, b);
	}
}