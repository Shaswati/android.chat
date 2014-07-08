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
        
        String sender = message.sender().nickname().toBlockingObservable().first();

        int resourceId = message.isOwn() ? layoutUserResourceId : listContactResourceId;
        row = inflater.inflate(resourceId, parent, false);
        
        RelativeLayout speechBubble = (RelativeLayout)row.findViewById(R.id.speechBubble);
        TextView messageContent = (TextView)row.findViewById(R.id.messageContent);
        TextView messageSender = (TextView)row.findViewById(R.id.messageSender);
        TextView messageTime = (TextView)row.findViewById(R.id.messageTime);
        
        messageContent.setText(message.content());
		messageSender.setText(sender);
        if (message.isOwn()) {
        	View speechBubbleArrowRight = row.findViewById(R.id.speechBubbleArrowRight);
        	speechBubbleArrowRight.setBackground(new TriangleRightDrawable(Color.parseColor("#D34F39")));
        } else {
        	messageSender.setTextColor(darkColorDeterminedBy(sender));
        	
        	View speechBubbleArrowLeft = row.findViewById(R.id.speechBubbleArrowLeft);
        	speechBubbleArrowLeft.setBackground(new TriangleLeftDrawable(darkColorDeterminedBy(sender)));
        	
        	LayerDrawable bubbleLayer = (LayerDrawable) speechBubble.getBackground();
        	GradientDrawable bubbleBackground = (GradientDrawable) bubbleLayer.findDrawableByLayerId(R.id.bubbleBackground);
        	bubbleBackground.setColor(lightColorDeterminedBy(sender));
        	
        	GradientDrawable bubbleShadow = (GradientDrawable) bubbleLayer.findDrawableByLayerId(R.id.bubbleShadow);
        	bubbleShadow.setColor(darkColorDeterminedBy(sender));
        }
        
        messageTime.setText(message.timeSent());
        
        return row;
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