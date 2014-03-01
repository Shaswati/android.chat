package sneerteam.android.chat.ui;

import java.util.List;
import java.util.Random;

import sneerteam.android.chat.Message;
import sneerteam.android.chat.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
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
        
        RelativeLayout speechBubble = (RelativeLayout)row.findViewById(R.id.speechBubble);
//        View speechBubbleArrowLeft = (View)row.findViewById(R.id.speechBubbleArrowLeft);
        TextView messageContent = (TextView)row.findViewById(R.id.messageContent);
        TextView messageSender = (TextView)row.findViewById(R.id.messageSender);
        TextView messageTime = (TextView)row.findViewById(R.id.messageTime);
        
        messageContent.setText(message.content());
        messageSender.setText(message.sender());
        if (!isMine(message)) {
        	messageSender.setTextColor(darkColorDeterminedBy(message.sender()));
        	
//        	LayerDrawable bubbleArrowLayer = (LayerDrawable) speechBubbleArrowLeft.getBackground();
//        	RotateDrawable bubbleArrowRotate = (RotateDrawable) bubbleArrowLayer.findDrawableByLayerId(R.id.bubbleArrow);
//        	GradientDrawable x = (GradientDrawable) getContext().getResources().getDrawable(R.id.bubbleArrow);
//        	x.setColor(darkColorDeterminedBy(message.sender()));
        	
        	LayerDrawable bubbleLayer = (LayerDrawable) speechBubble.getBackground();
        	GradientDrawable bubbleBackground = (GradientDrawable) bubbleLayer.findDrawableByLayerId(R.id.bubbleBackground);
        	bubbleBackground.setColor(lightColorDeterminedBy(message.sender()));
        	
        	GradientDrawable bubbleShadow = (GradientDrawable) bubbleLayer.findDrawableByLayerId(R.id.bubbleShadow);
        	bubbleShadow.setColor(darkColorDeterminedBy(message.sender()));
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