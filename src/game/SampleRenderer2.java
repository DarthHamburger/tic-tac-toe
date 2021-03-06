package game;

import java.awt.Color;

import core.graphics.RenderEvent;

/** Another sample Renderer implementation.
* @author Bryan Charles Bettis
*/
@SuppressWarnings("javadoc")
public class SampleRenderer2 implements core.graphics.Renderer
{
	private int x;
	private int y;
	private int width;
	private int height;
	private Color renderColor;
	
	public SampleRenderer2()
	{
		this(180, 0, 20, 270, Color.darkGray);
	}
	
	public SampleRenderer2(int x, int y, int width, int height, Color color)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.renderColor = color;
	}

	@Override
	public void render(RenderEvent e)
	{
		e.getContext().setColor(renderColor);
		e.getContext().fillRect(x, y, width, height);
	}

}
