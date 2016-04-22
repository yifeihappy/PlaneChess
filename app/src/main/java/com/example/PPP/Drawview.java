package com.example.PPP;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.example.yifeihappy.planechess.R;

public class Drawview extends View{

	public Drawview(Context context) {
		super(context);
		if(isInEditMode()) {return;}
		// TODO Auto-generated constructor stub
	}

	public Drawview(Context context, AttributeSet attrs) {
		super(context, attrs);
		if(isInEditMode()) {return;}
	}

	public Drawview(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		if(isInEditMode()) {return;}
	}

	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);

		Paint paint=new Paint();
		paint.setAntiAlias(true);
		paint.setColor(getResources().getColor(R.color.red));
		paint.setStyle(Paint.Style.FILL);
		paint.setStrokeWidth(3);
		canvas.drawRect(0, 0, 90, 90, paint);
		canvas.drawRect(80, 80, 110, 110, paint);
		paint.setColor(getResources().getColor(R.color.yellow));
		canvas.drawRect(350, 0, 450, 100, paint);
		paint.setColor(getResources().getColor(R.color.green));
		canvas.drawRect(0, 350, 100, 450, paint);
		paint.setColor(getResources().getColor(R.color.blue));
		canvas.drawRect(350, 350, 450, 450, paint);
		for(int i=0;i<200;i++)
		{
            switch (i%4) {
            case 0:
            	paint.setColor(getResources().getColor(R.color.blue));
                break;
            case 1:
            	paint.setColor(getResources().getColor(R.color.green));
                break;
            case 2:
            	paint.setColor(getResources().getColor(R.color.red));
                break;
            case 3:
            	paint.setColor(getResources().getColor(R.color.yellow));
                break;
            default:
            	paint.setColor(Color.WHITE);
                break;
        }
            Cell c[]=new Cell[200];    
            //
            if(i>=0 && i<=3)
            	c[i]=new Cell(120,420-i*30,150, 450-i*30, paint,canvas);
            //
            if(i>=4 && i<=7)
            	c[i]=new Cell(90-(i-4)*30,300,120-(i-4)*30, 330, paint,canvas);
            //
            if(i>=8 && i<=12)
            	c[i]=new Cell(0,270-(i-8)*30,30, 300-(i-8)*30, paint,canvas);
            //
            if(i>=13 && i<=16)
            	c[i]=new Cell(0+(i-13)*30,120,30+(i-13)*30, 150, paint,canvas);
            //
            if(i>=17 && i<=20)
            	c[i]=new Cell(120,90-(i-17)*30,150,120-(i-17)*30, paint,canvas);
    		//
	    	if(i>=21 && i<=25)
	    		c[i]=new Cell(150+(i-21)*30,0,180+(i-21)*30, 30, paint,canvas);
	    	//
	    	if(i>=26 && i<=29)
            	c[i]=new Cell(300,0+(i-26)*30,330,30+(i-26)*30, paint,canvas);
	    	//
	    	if(i>=30 && i<=33)
	    		c[i]=new Cell(330+(i-30)*30,120,360+(i-30)*30,150, paint,canvas);
	    	//
	    	if(i>=34 && i<=38)
            	c[i]=new Cell(420,150+(i-34)*30,450,180+(i-34)*30, paint,canvas);
	    	//
            if(i>=39 && i<=42)
            	c[i]=new Cell(420-(i-39)*30,300,450-(i-39)*30, 330, paint,canvas);
	    	//
	    	if(i>=43 && i<=46)
            	c[i]=new Cell(300,330+(i-43)*30,330,360+(i-43)*30, paint,canvas);
	    	//
            if(i>=47 && i<=51)
            	c[i]=new Cell(270-(i-47)*30,420,300-(i-47)*30, 450, paint,canvas);
            
            if(i>=100 && i<=101)
            	c[i]=new Cell(23+(i-100)*45,23, paint,canvas);
            if(i>=102 && i<=103)
            	c[i]=new Cell(23+(i-102)*45,68, paint,canvas);

            
		}
		
		
	}
	

}