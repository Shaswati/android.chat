package sneer.android.chat.ui;

import java.util.*;

import sneer.android.chat.*;
import sneer.chat.*;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.*;
import android.view.*;
import android.widget.*;

public class ChatAdapter extends ArrayAdapter<Message>{

    int layoutUserResourceId;    
    int listContactResourceId;
    List<Message> data = null;
	private String sender;
	private LayoutInflater inflater;
    
    public ChatAdapter(Context context, LayoutInflater inflater, int layoutUserResourceId, int listContactResourceId, List<Message> data) {
        super(context, layoutUserResourceId, data);
		this.inflater = inflater;
        this.layoutUserResourceId = layoutUserResourceId;
        this.listContactResourceId = listContactResourceId;
        this.data = data;
    }

    @SuppressLint("NewApi")
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        
        Message message = data.get(position);
        
        if (isMine(message))
        	row = inflater.inflate(layoutUserResourceId, parent, false);
        else
        	row = inflater.inflate(listContactResourceId, parent, false);
        
        RelativeLayout speechBubble = (RelativeLayout)row.findViewById(R.id.speechBubble);
        TextView messageContent = (TextView)row.findViewById(R.id.messageContent);
        TextView messageSender = (TextView)row.findViewById(R.id.messageSender);
        TextView messageTime = (TextView)row.findViewById(R.id.messageTime);
        
        messageContent.setText(message.content());
        messageSender.setText(message.sender());
        if (!isMine(message)) {
        	messageSender.setTextColor(darkColorDeterminedBy(message.sender()));
        	
        	View speechBubbleArrowLeft = row.findViewById(R.id.speechBubbleArrowLeft);
        	speechBubbleArrowLeft.setBackground(new TriangleLeftDrawable(darkColorDeterminedBy(message.sender())));
        	
        	LayerDrawable bubbleLayer = (LayerDrawable) speechBubble.getBackground();
        	GradientDrawable bubbleBackground = (GradientDrawable) bubbleLayer.findDrawableByLayerId(R.id.bubbleBackground);
        	bubbleBackground.setColor(lightColorDeterminedBy(message.sender()));
        	
        	GradientDrawable bubbleShadow = (GradientDrawable) bubbleLayer.findDrawableByLayerId(R.id.bubbleShadow);
        	bubbleShadow.setColor(darkColorDeterminedBy(message.sender()));
        } else {
        	View speechBubbleArrowRight = row.findViewById(R.id.speechBubbleArrowRight);
        	speechBubbleArrowRight.setBackground(new TriangleRightDrawable(Color.parseColor("#D34F39")));
        }
        
        messageTime.setText(message.time());
        
        return row;
    }

	private boolean isMine(Message message) {
		return sender != null && sender.equals(message.sender());
	}
    
	public boolean hasSender() {
		return sender != null;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}
	
	private static int darkColorDeterminedBy(String string) {
		return colorDeterminedBy(string, 50);
	}
	
	private static int lightColorDeterminedBy(String string) {
		return colorDeterminedBy(string, 170);
	}
	
	private static int colorDeterminedBy(String string, int strength) {
		Random random = new Random(string.hashCode() * 713);
		int r = strength + random.nextInt(86);
		int g = strength + random.nextInt(86);
		int b = strength + random.nextInt(86);
		return Color.argb(255, r, g, b);
	}
}