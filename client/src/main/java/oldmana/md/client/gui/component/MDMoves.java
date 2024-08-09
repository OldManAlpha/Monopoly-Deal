package oldmana.md.client.gui.component;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

import oldmana.md.client.Scheduler;
import oldmana.md.client.gui.util.GraphicsUtils;
import oldmana.md.client.gui.util.TextPainter;
import oldmana.md.client.gui.util.TextPainter.Alignment;
import oldmana.md.client.state.ActionState;
import oldmana.md.client.state.ActionStateDraw;

public class MDMoves extends MDComponent
{
	private int visibleMaxMoves = getMaxMoves();
	
	private double visibleMoves;
	
	private boolean rush;
	
	public MDMoves()
	{
		getClient().getScheduler().scheduleFrameboundTask(task ->
		{
			int moves = getMoves();
			int maxMoves = getMaxMoves();
			
			double fps = Scheduler.getFPS();
			
			if (visibleMoves > moves + 1 || visibleMoves < moves - 1)
			{
				rush = true;
			}
			
			if (visibleMoves > moves)
			{
				visibleMoves -= rush ? 6 / fps : 3 / fps;
				if (visibleMoves < moves)
				{
					visibleMoves = moves;
				}
				updateGraphics();
			}
			else if (visibleMoves < moves)
			{
				visibleMoves += rush ? 6 / fps : 3 / fps;
				if (visibleMoves > moves)
				{
					visibleMoves = moves;
				}
				updateGraphics();
			}
			else
			{
				rush = false;
			}
			if (visibleMoves > visibleMaxMoves)
			{
				visibleMaxMoves++;
				updateGraphics();
			}
			if (visibleMaxMoves > maxMoves && visibleMoves <= maxMoves)
			{
				visibleMaxMoves--;
				updateGraphics();
			}
		});
	}
	
	public int getMaxMoves()
	{
		return getClient().getRules().getMaxMoves();
	}
	
	private int getMoves()
	{
		return shouldHideMoves() ? 0 : getClient().getGameState().getMoves();
	}
	
	public void updateVisibleMaxMoves()
	{
		visibleMaxMoves = Math.max(getMaxMoves(), (int) Math.ceil(visibleMoves));
	}
	
	private boolean shouldHideMoves()
	{
		ActionState state = getClient().getGameState().getActionState();
		return getClient().getGameState().getPlayerTurn() == null || state == null || state instanceof ActionStateDraw;
	}
	
	@Override
	public void doPaint(Graphics gr)
	{
		LinearGradientPaint grayPaint = new LinearGradientPaint(0, 0, 0, getHeight() * 0.5F,
				new float[] {0, 1}, new Color[] {Color.GRAY, new Color(145, 145, 145)});
		LinearGradientPaint greenPaint =
				new LinearGradientPaint(0, 0, 0, getHeight() * 0.5F, new float[] {0, 1},
						new Color[] {GraphicsUtils.getLighterColor(GraphicsUtils.GREEN, 1), GraphicsUtils.GREEN});
		
		int moves = getMoves();
		
		int width = scale(180);
		double turnWidth = (double) width / visibleMaxMoves;
		double visibleWidth = (visibleMoves / visibleMaxMoves) * getWidth();
		Graphics2D g = (Graphics2D) gr.create();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.GRAY);
		g.clip(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), scale(20), scale(50)));
		
		
		g.setPaint(grayPaint);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		g.setPaint(greenPaint);
		g.fillRect(0, 0, (int) (visibleWidth), getHeight());
		
		if (visibleMoves != moves)
		{
			double targetDiff = (Math.abs(moves - visibleMoves) / visibleMaxMoves) * getWidth();
			int shineWidth = (int) Math.min(scale(10), Math.max(1, targetDiff));
			int startX = (int) (visibleWidth - shineWidth);
			LinearGradientPaint shinePaint = new LinearGradientPaint(startX, 0, startX + shineWidth, 0,
					new float[] {0, 1}, new Color[] {new Color(255, 255, 255, 10), new Color(255, 255, 255, 200)});
			//g.setColor(new Color(255, 255, 255, 80));
			g.setPaint(shinePaint);
			g.fillRect(startX, 1, shineWidth, getHeight() - 2);
		}
		
		g.setColor(Color.DARK_GRAY);
		for (int i = 0 ; i < visibleMaxMoves - 1 ; i++)
		{
			int linePos = (int) Math.ceil(turnWidth * (i + 1));
			g.drawLine(linePos, 0, linePos, getHeight());
		}
		
		
		if (!shouldHideMoves())
		{
			g.setColor(Color.BLACK);
			TextPainter tp = new TextPainter("Moves: " + moves, GraphicsUtils.getBoldMDFont(Font.PLAIN, scale(24)), getVisibleRect());
			tp.setVerticalAlignment(Alignment.CENTER);
			tp.setHorizontalAlignment(Alignment.CENTER);
			tp.paint(g);
		}
		
		g = (Graphics2D) gr;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.DARK_GRAY);
		g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, scale(20), scale(50));
	}
}
