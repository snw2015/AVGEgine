package snw.engine.component;

import java.awt.Image;

public class MovingTextBox extends TextBox
{
	private boolean isEnded = false;
	private int counter = 0;
	private int speed = 0;// 0~100

	public MovingTextBox(String name, Image background, int x, int y, int width,
			int height)
	{
		super(name, background, x, y, width, height);
		text.setRenderLength(0);
		// TODO Auto-generated constructor stub
	}

	public MovingTextBox(String name, Image background, int x, int y)
	{
		super(name, background, x, y);
		text.setRenderLength(0);
		// TODO Auto-generated constructor stub
	}

	public MovingTextBox(String name, Image background, String rawText, int x, int y)
	{
		super(name, background, rawText, x, y);
		text.setRenderLength(0);
		// TODO Auto-generated constructor stub
	}

	public MovingTextBox(String name, Image background, String rawText)
	{
		super(name, background, rawText);
		text.setRenderLength(0);
		// TODO Auto-generated constructor stub
	}

	public MovingTextBox(String name, Image background)
	{
		super(name, background);
		text.setRenderLength(0);
		// TODO Auto-generated constructor stub
	}

	public MovingTextBox(String name, Image background, String rawText, int x, int y,
			int width, int height)
	{
		super(name, background, rawText, x, y, width, height);
		text.setRenderLength(0);
		// TODO Auto-generated constructor stub
	}

	public MovingTextBox(String name, Image background, String rawText, int x, int y,
			int width, int height, int speed)
	{
		super(name, background, rawText, x, y, width, height);
		text.setRenderLength(0);
		this.speed = speed;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void update()
	{
		if (speed != 0 && !isEnded)
		{
			int maxmumFrame = 100 / speed;
			if (++counter > maxmumFrame)
			{
				counter = 0;
				if (!text.addRenderLength())
				{
					isEnded = true;
				}
			}
		}
	}

	public int getSpeed()
	{
		return speed;
	}

	public void setSpeed(int speed)
	{
		this.speed = speed;
	}
}