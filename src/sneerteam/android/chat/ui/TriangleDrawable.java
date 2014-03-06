package sneerteam.android.chat.ui;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;

public class TriangleDrawable extends Drawable {

	private Paint paint;

	public TriangleDrawable(int color) {
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(color);
		paint.setStyle(Paint.Style.STROKE);
	}

	@Override
	public void draw(Canvas canvas) {
		int height = getBounds().height();
		int width = getBounds().width();

		Path path = new Path();
		paint.setStyle(Paint.Style.FILL);
		
		path.moveTo(0, 0);
		path.lineTo(width, 0);
		path.lineTo(width, height);
		path.lineTo(0, 0);
		
		path.close();
		canvas.drawPath(path, paint);
  }

  @Override
  public void setAlpha(int alpha) {

  }

  @Override
  public void setColorFilter(ColorFilter cf) {

  }

  @Override
  public int getOpacity() {
    return 255;
  }

} 