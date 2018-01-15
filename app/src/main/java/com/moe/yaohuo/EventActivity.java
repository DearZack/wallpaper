package com.moe.yaohuo;
import android.os.Bundle;
import android.view.WindowManager;
import android.support.v7.widget.Toolbar;
import com.moe.utils.PreferenceUtils;
import android.view.MotionEvent;
import android.view.View;
import android.animation.ObjectAnimator;
import android.animation.Animator;
import android.graphics.Color;
import android.animation.ValueAnimator;
import android.animation.AnimatorSet;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.view.ViewGroup;
import android.support.design.widget.CoordinatorLayout;
import android.support.graphics.drawable.TypedArrayUtils;
import android.content.res.TypedArray;
import android.widget.TextView;
import java.lang.reflect.Field;
import android.util.TypedValue;
import com.moe.internal.ImageGetter;
import android.text.Html;

public class EventActivity extends BaseActivity
{
	private TextView title;
	private ImageGetter ig;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_list_view);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
		Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
		try
		{
			Field title=android.support.v7.widget.Toolbar.class.getDeclaredField("mTitleTextView");
			title.setAccessible(true);
			this.title = (TextView) title.get(toolbar);
		}
		catch (NoSuchFieldException e)
		{}
		catch (IllegalAccessException e)
		{}
		catch (SecurityException e)
		{}
		catch (IllegalArgumentException e)
		{}
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
	/*@Override
	public boolean dispatchTouchEvent(MotionEvent event)
	{
		if(isAnime)return true;
		switch(event.getAction()){
			case event.ACTION_DOWN:
				oldx=event.getRawX();
				if(oldx<30){
				oldy=event.getRawY();
				mode=0;
				time=System.currentTimeMillis();
				//super.dispatchTouchEvent(event);
				return true;
				}else{
					oldy=-1;
				break;
				}
			case event.ACTION_MOVE:
				if(oldy==-1)break;
				switch(mode){
					case -1://横向滚动
						float lx=X+event.getRawX()-oldx;
						view.setX(lx<0?0:lx);
						background.setBackgroundColor(Color.argb((int)((1-lx/background.getWidth())*255/2),0,0,0));
						return true;
					case 0:
						if(Math.abs(oldy-event.getRawY())<5&&event.getRawX()>oldx)
						{mode=-1;
							X=view.getX();
						}else{
							mode=-1;
							}
						dispatchTouchEvent(event);
						break;
					case 1:
						break;
				}
				break;
			case event.ACTION_UP:
				if(oldy==-1)break;
				if(mode==-1){
					if(System.currentTimeMillis()-time<200||view.getX()>view.getWidth()*0.25)
				animeFinish();
				else
				animeResume();
				}
				break;
		}
		
		return super.dispatchTouchEvent(event);
	}*/
/**
	private void animeResume()
	{
		float[] data=new float[]{view.getX(),0};
		Animator anim=ObjectAnimator.ofFloat(view,"X",data);
		anim.setDuration((int)(data[0]/view.getWidth()*500));
		anim.addListener(new Animator.AnimatorListener(){

				@Override
				public void onAnimationStart(Animator p1)
				{
					isAnime=true;
				}

				@Override
				public void onAnimationEnd(Animator p1)
				{
					isAnime=false;
				}

				@Override
				public void onAnimationCancel(Animator p1)
				{
					// TODO: Implement this method
				}

				@Override
				public void onAnimationRepeat(Animator p1)
				{
					// TODO: Implement this method
				}
			});
		anim.start();
	}
	private void animeFinish(){
		float[] data=new float[]{view.getX(),view.getWidth()};
		AnimatorSet anim=new AnimatorSet();
		Animator trans=ObjectAnimator.ofFloat(view,"X",data);
		ValueAnimator alpha=ObjectAnimator.ofInt(new int[]{(int)((1-view.getX()/view.getWidth())*255/2),0});
		alpha.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){

				@Override
				public void onAnimationUpdate(ValueAnimator p1)
				{
					background.setBackgroundColor(Color.argb(p1.getAnimatedValue(),0,0,0));
				}
			});
		anim.addListener(new Animator.AnimatorListener(){

				@Override
				public void onAnimationStart(Animator p1)
				{
					isAnime=true;
				}

				@Override
				public void onAnimationEnd(Animator p1)
				{
					finish();
				}

				@Override
				public void onAnimationCancel(Animator p1)
				{
					// TODO: Implement this method
				}

				@Override
				public void onAnimationRepeat(Animator p1)
				{
					// TODO: Implement this method
				}
			});
			anim.playTogether(new Animator[]{trans,alpha});
			anim.setDuration((int)((data[1]-data[0])/data[1]*500));
			anim.start();
	}*/
	@Override
	public void finish()
	{
		// TODO: Implement this method
		super.finish();
		overridePendingTransition(0,0);
	}

	@Override
	public void setTitle(CharSequence title)
	{
		if(this.title!=null)
			this.title.setText(Html.fromHtml(title.toString(),ig==null?ig=new ImageGetter(this.title,true):ig,null));
			else
			getSupportActionBar().setTitle(title);
			
	}
	
}
