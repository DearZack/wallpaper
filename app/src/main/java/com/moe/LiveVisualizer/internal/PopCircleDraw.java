package com.moe.LiveVisualizer.internal;
import com.moe.LiveVisualizer.LiveWallpaper;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.TypedValue;
import android.graphics.Bitmap;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff;
import android.graphics.LinearGradient;
import android.view.SurfaceHolder;

public class PopCircleDraw extends ImageDraw
{
	private Paint paint;
	//private float[] points;
	private AnimeThread anime;
	public PopCircleDraw(ImageDraw draw,LiveWallpaper.MoeEngine engine){
		super(draw,engine);
		paint=new Paint();
		paint.setStrokeWidth(3);
		paint.setStyle(Paint.Style.STROKE);
	}

	@Override
	public void onDraw(Canvas canvas, int color_mode)
	{
		/*if(anime==null){
			anime=new AnimeThread();
			anime.start();
			}
			*/
			animeDraw(canvas,color_mode);
		
	}
	private void animeDraw(Canvas canvas,int color_mode){
		switch(getEngine().getColorList().size()){
			case 0:
				paint.setColor(0xff39c5bb);
				drawPop(canvas,color_mode,false);
				break;
			case 1:
				paint.setColor(getEngine().getColorList().get(0));
				drawPop(canvas,color_mode,false);
				break;
			default:
				if(color_mode==0){
					final Bitmap src=Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
					final Canvas tmpCanvas=new Canvas(src);
					drawPop(tmpCanvas,color_mode,false);	
					if ( getEngine().getShader() == null )
						getEngine().setShader(new LinearGradient(0, 0, canvas.getWidth(), 0, getEngine().getColorList().toArray(), null, LinearGradient.TileMode.CLAMP));
					paint.setShader(getEngine().getShader());
					paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
					paint.setStyle(Paint.Style.FILL);
					tmpCanvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);
					paint.setStyle(Paint.Style.STROKE);
					//canvas.drawBitmap(shader, 0, 0, paint);
					paint.setShader(null);
					paint.setXfermode(null);
					canvas.drawBitmap(src, 0, 0, paint);
					src.recycle();
				}else
					drawPop(canvas,color_mode,true);
				break;
		}
	}
	private void drawPop(Canvas canvas,int color_mode,boolean useMode){
		byte[] buffer=getBuffer();
		int borderWidth=getEngine().getSharedPreferences().getInt("borderWidth", 30);
		float spaceWidth=getEngine().getSharedPreferences().getInt("spaceWidth", 20);
		int borderHeight=(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, getEngine().getSharedPreferences().getInt("borderHeight", 30), getEngine().getContext().getResources().getDisplayMetrics());
		int size=0;
		try
		{
			size = (int)((canvas.getWidth() - spaceWidth) / (borderWidth + spaceWidth));
		}
		catch (Exception e)
		{}
		if ( size == 0 )return;
		size = size > buffer.length ?buffer.length: size;
		//if(points==null||points.length!=size)
		//	points=new float[size];
		spaceWidth = (canvas.getWidth()-size*borderWidth) / ((float)size-1);
		float x=borderWidth/2.0f;//起始像素
		float y=canvas.getHeight() - getEngine().getSharedPreferences().getInt("height", 10) / 100.0f * canvas.getHeight();
		int step=buffer.length / size;
		int colorStep=0;
		int mode=Integer.parseInt(getEngine().getSharedPreferences().getString("color_mode", "0"));
		if ( mode == 3 )
			paint.setColor(getEngine().getColor());
			canvas.drawLine(0,y,canvas.getWidth(),y,paint);
		for ( int i=0;i < size;i ++ )
		{
			if(useMode){
				if ( mode == 1 )
				{
					paint.setColor(getEngine().getColorList().get(colorStep));
					colorStep++;
					if ( colorStep >= getEngine().getColorList().size() )colorStep = 0;
				}
				else if ( mode == 2 )
				{
					paint.setColor(getEngine().getColorList().getRandom());
				}
			}
			//if(points[i]==0)points[i]=y-borderWidth/2.0f;
			float currentP=y+(Math.abs(buffer[i*step])-128)*borderHeight/128.0f-borderWidth/2.0f;
			//float offset=currentP-points[i];
			//float offsetY=offset>0?(Math.abs(offset)>5?5:offset):(Math.abs(offset)<-3?-3:offset);
			//points[i]+=offsetY;
			canvas.drawCircle(x,currentP,borderWidth/2.0f,paint);
			x+=(spaceWidth+borderWidth);
		}
	}
	
	class AnimeThread extends Thread
	{

		@Override
		public void run()
		{
			while(true){
				SurfaceHolder sh=getEngine().getSurfaceHolder();
				if(sh!=null){
					Canvas canvas=sh.lockCanvas();
					if(canvas!=null){
				animeDraw(canvas,Integer.parseInt(getEngine().getSharedPreferences().getString("color_mode","0")));
				try{
					sh.unlockCanvasAndPost(canvas);
				}catch(Exception e){}
				}
				}
			try
			{
				sleep(33);
				if(!getEngine().getSharedPreferences().getString("visualizer_mode","0").equals("3")){
					anime=null;
					interrupt();
					break;
					}
			}
			catch (InterruptedException e)
			{}
			}
		}
		
	}
}