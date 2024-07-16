package oldmana.md.client.gui;

import oldmana.md.client.gui.component.MDComponent;

public interface AutoScrollable
{
	int getScrollNeededToView(MDComponent component);
	
	int getScrollPos();
	
	void setScrollPos(int pos);
}
