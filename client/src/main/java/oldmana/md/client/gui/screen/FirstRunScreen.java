package oldmana.md.client.gui.screen;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.File;

import oldmana.md.client.MDSoundSystem;
import oldmana.md.client.gui.LayoutAdapter;
import oldmana.md.client.gui.component.MDButton;
import oldmana.md.client.gui.component.MDComponent;
import oldmana.md.client.gui.component.MDText;
import oldmana.md.client.gui.util.TextPainter.Alignment;

public class FirstRunScreen extends MDComponent
{
	private MDText welcome;
	private MDText message;
	
	private MDButton local;
	private MDButton portable;
	
	private MDText localDesc;
	private MDText portableDesc;
	
	public FirstRunScreen()
	{
		welcome = new MDText("Welcome to Monopoly Deal");
		welcome.setFontSize(36);
		welcome.setBold(true);
		welcome.setHorizontalAlignment(Alignment.CENTER);
		welcome.setVerticalAlignment(Alignment.CENTER);
		add(welcome);
		
		message = new MDText("Where would you like to store the game's settings on your computer?");
		message.setFontSize(24);
		add(message);
		
		local = new MDButton("Local");
		local.setFontSize(24);
		local.addClickListener(() ->
		{
			File folder = getClient().getLocalFolder();
			folder.mkdirs();
			getClient().setDataFolder(folder);
			getClient().getSettings().setLocation(folder);
			MDSoundSystem.loadCache();
			System.out.println("Created local files");
			getClient().getWindow().displayMenu();
		});
		add(local);
		
		portable = new MDButton("Portable");
		portable.setFontSize(24);
		portable.addClickListener(() ->
		{
			File folder = getClient().getJarFolder();
			getClient().setDataFolder(folder);
			getClient().getSettings().setLocation(folder);
			MDSoundSystem.loadCache();
			System.out.println("Created portable files");
			getClient().getWindow().displayMenu();
		});
		add(portable);
		
		localDesc = new MDText("Store the game settings in a fixed position on your computer: " + getClient().getLocalFolder().getAbsolutePath());
		localDesc.setFontSize(20);
		localDesc.setVerticalAlignment(Alignment.TOP);
		add(localDesc);
		
		portableDesc = new MDText("Store the game settings in the same directory as where you are running the game from: " + 
		getClient().getJarFolder().getAbsolutePath());
		portableDesc.setFontSize(20);
		portableDesc.setVerticalAlignment(Alignment.TOP);
		add(portableDesc);
		
		
		setLayout(new FirstRunLayout());
	}
	
	@Override
	public void paintComponent(Graphics gr)
	{
		super.paintComponent(gr);
		Graphics2D g = (Graphics2D) gr;
		g.setColor(Color.LIGHT_GRAY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.fillRoundRect(localDesc.getX() - scale(10), local.getY() - scale(10), localDesc.getWidth() + scale(20), getHeight() - local.getY(), 
				scale(24), scale(24));
		g.fillRoundRect(portableDesc.getX() - scale(10), portable.getY() - scale(10), portableDesc.getWidth() + scale(20), getHeight() - portable.getY(), 
				scale(24), scale(24));
	}
	
	public class FirstRunLayout extends LayoutAdapter
	{
		@Override
		public void layoutContainer(Container container)
		{
			welcome.setSize(getWidth(), scale(70));
			welcome.setLocation(0, scale(10));
			
			message.setSize(getWidth() * 0.7, scale(80));
			message.setLocation(getWidth() * 0.15, welcome.getMaxY() + scale(10));
			
			local.setSize(getWidth() / 3, scale(30));
			local.setLocationCentered(getWidth() * 0.25, message.getMaxY() + scale(30));
			
			portable.setSize(getWidth() / 3, scale(30));
			portable.setLocationCentered(getWidth() * 0.75, message.getMaxY() + scale(30));
			
			localDesc.setSize(getWidth() * 0.4, getHeight() - local.getMaxY() - scale(20));
			localDesc.setLocation((getWidth() * 0.25) - (localDesc.getWidth() / 2), local.getMaxY() + scale(10));
			
			portableDesc.setSize(getWidth() * 0.4, getHeight() - portable.getMaxY() - scale(20));
			portableDesc.setLocation((getWidth() * 0.75) - (portableDesc.getWidth() / 2), portable.getMaxY() + scale(10));
		}
		
		@Override
		public void invalidateLayout(Container container)
		{
			layoutContainer(container);
		}
	}
}
