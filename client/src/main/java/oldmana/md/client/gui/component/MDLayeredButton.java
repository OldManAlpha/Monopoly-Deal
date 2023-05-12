package oldmana.md.client.gui.component;

import oldmana.md.common.playerui.ButtonColorScheme;

import java.awt.event.MouseAdapter;
import java.util.LinkedList;
import java.util.List;

public class MDLayeredButton extends MDButton
{
	private List<ButtonLayer> layers = new LinkedList<ButtonLayer>();
	
	public MDLayeredButton(String text)
	{
		super(text);
	}
	
	public void addLayer(String text, ButtonColorScheme color, MouseAdapter listener)
	{
		ButtonLayer layer = new ButtonLayer(text, color, listener);
		layers.add(0, layer);
		layer.applyLayer();
	}
	
	public void popLayer()
	{
		layers.remove(0);
		if (layers.size() > 0)
		{
			layers.get(0).applyLayer();
			
		}
		else
		{
			setText("");
			setColor(ButtonColorScheme.NORMAL);
			removeListener();
		}
	}
	
	public class ButtonLayer
	{
		private String text;
		private ButtonColorScheme color;
		private MouseAdapter listener;
		
		public ButtonLayer(String text, ButtonColorScheme color, MouseAdapter listener)
		{
			this.text = text;
			this.color = color;
			this.listener = listener;
		}
		
		public void applyLayer()
		{
			setText(text);
			setColor(color);
			setListener(listener);
		}
	}
}
