package com.example.PPP;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.jinbo.struct.Chess;
import com.example.jinbo.struct.Room;
import com.example.jinbo.struct.Square;
import com.example.yifeihappy.planechess.R;

import java.util.List;

public class Drawview extends View {

	private Room room;        //没什么用，只是为了获取room中的数据。

	private Cell cell;

	public int starts;   //棋子刚被点击时的下标

	public int ends;     //棋子走完步数时的下标

	static int max_cell = 200;

	public Cell c[]=new Cell[max_cell];

	boolean Init = true;

	public UpdateThread thread;   //动画线程

	public void setRoom(Room room)
	{
		this.room = room;
	}

	public Cell getCell()
	{
		return this.cell;
	}

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

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 如果是按下操作
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			setCell(event.getX(), event.getY());
		}
		return super.onTouchEvent(event);
	}

	/*
	* 判断并设置被选中的Cell或者cell的下标
	* */
	public void setCell(float x,float y)
	{
		Log.i("touch", "doxy: " + x + " " + y);
		int position = -1;      //这里还没设置好参数
		for(int i = 0; i < max_cell; i++)
		{
			if(x>c[i].x0 && x<c[i].x1 && y>c[i].y0 && y<c[i].y1)
			{
				position = i;
			}
		}

		Square square = room.game.getBoard().getSquare(position);
		Chess chess = square.getLastChess();
		starts = chess.getPoint();
		room.setchosechess(chess);    //点击的那个chess位置

		//我不知道这个阻塞操作会不会有问题，如果有问题
		//就要让逻辑层直接传那个骰子数过来。
		while(true)
		{
			if(room.ismove == true) {
				ends = chess.getPoint();    //要走到的那个位置
				break;
			}
		}

		thread = new UpdateThread(this);
		thread.start();
	}

	public void init(Canvas canvas)
	{
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

	public void fly(Canvas canvas)
	{
		Paint paint=new Paint();
		paint.setAntiAlias(true);
		paint.setColor(getResources().getColor(R.color.red));
		paint.setStyle(Paint.Style.FILL);
		paint.setStrokeWidth(3);

		init(canvas);    //重绘棋盘
		//要遍历一遍所有的棋子重绘。

		List<Chess> list = room.game.getAllchess();
		for(int i=0;i<list.size();i++)
		{
			if(list.get(i).getPoint() == starts)continue;
			/*
			* 画棋子,但现在还没有棋子的形状
			* */
			//设置颜色list.get(i).getcolor
			//cell[i].x0,y0,x1,y1
			//paint.setColor(getResources().getColor());
			//canvas.drawArc();
		}

		//可能starts还要初始化那里判断或搞一些
		if(!flyend())
		{
			//canvas.drawArc();
			starts++;     //走完这一步，移到下一格

		}

	}

	public boolean flyend()   //判断是否已经走完这一步
	{
		if(starts != ends)return true;
		else return false;
	}
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		if(Init)
		{
			init(canvas);
			Init = false;
		}
		else fly(canvas);

	}

}