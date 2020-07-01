package me.przemovi;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JLabel;

public class Gauge extends JComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7698637995106435168L;

	public int getGaugeSize() {
		return gaugeSize;
	}

	public void setGaugeSize(int gaugeSize) {
		this.gaugeSize = gaugeSize;
		repaint();
	}

	public Color getColor1() {
		return color1;
	}

	public void setColor1(Color color1) {
		this.color1 = color1;
		repaint();
	}

	public Color getColor2() {
		return color2;
	}

	public void setColor2(Color color2) {
		this.color2 = color2;
		repaint();
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		if (value < 0) {
			value = 0;
		}
		this.value = value;
		repaint();
	}

	public int getMaximum() {
		return maximum;
	}

	public void setMaximum(int maximum) {
		this.maximum = maximum;
		repaint();
	}

	private int gaugeSize = 7;
	private Color color1 = Color.GREEN;
	private Color color2 = Color.RED;
	private int value;
	private int maximum = 100;

	public Gauge() {
		setFont(new JLabel().getFont().deriveFont(0, 20));
		setPreferredSize(new Dimension(250, 220));
		setForeground(new Color(60, 60, 60));
	}
	
	@Override
	public void paint(Graphics grphcs) {
		Graphics2D g2 = (Graphics2D) grphcs;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		buildBackground();
		g2.drawImage(background, 0, 0, null);
		
		int width = getWidth();
		int height = getHeight();
		
		float spaceBot = 0.15f; // space bot 15%
		height += (int) (height * spaceBot);
		int size = Math.min(width, height) - (gaugeSize + 5); // 5 is margin
		int centerX = width / 2;
		int centerY = height / 2;
		
		double angleSize = (size / 2) - gaugeSize;

		double angle = getAngleOfValues();
		double ang = 360 - (angle + 90 + angleStart);
		
		{
			int wie = 10;
			g2.setColor(color1);
			Path2D p = new Path2D.Double();
			Point end = getLocation(ang, angleSize);
			Point right = getLocation(ang - 90, wie);
			Point left = getLocation(ang + 90, wie);
			// wskazówka
			p.moveTo(centerX + left.x, centerY - left.y);
			p.lineTo(centerX + end.x, centerY - end.y);
			p.lineTo(centerX + right.x, centerY - right.y);
			g2.fill(p);
			// środek zew
			g2.fillOval(centerX - wie, centerY - wie, wie * 2, wie * 2);
			// środek wew
			g2.setColor(new Color(215, 215, 215));
			int wew = wie - 2;
			g2.fillOval(centerX - wew / 2, centerY - wew / 2, wew, wew);
		}
		{
			/*double start = 360 - (90 + angleStart) +5;
			double end = start-gaugeSizeStopnie -5;
			
			Path2D p = new Path2D.Double();
			
			Point mid = getLocation(360-45, 20);
			Point bot = getLocation(230, angleSize);
			Point ri = getLocation(0, angleSize);
			
			p.moveTo(centerX + mid.x, centerY - mid.y);//near middle
			
			p.lineTo(centerX + ri.x, centerY - mid.y);//to top right
			p.lineTo(centerX + ri.x, centerY - bot.y);//to bottom right
			p.lineTo(centerX + bot.x, centerY - bot.y);//to left bottom
			p.lineTo(centerX + mid.x, centerY - mid.y);//back to middle
			
			
			g2.setPaint(color1);
			g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
			g2.draw(p);*/
		}
		
		//drawText(g2, centerX, centerY, angleSize);
		
		super.paint(grphcs);
	}

	private static final int gaugeSizeStopnie = 225;
	private static final int angleStart = 45;
	
	private void buildBackground() {
		if(background != null) {
			return;
		}
		background = new BufferedImage(getWidth(),getHeight(),BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = background.createGraphics();
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		int width = getWidth();
		int height = getHeight();

		// przestrzeń pod zegarem
		float spaceBot = 0.15f; // space bot 15%
		height += (int) (height * spaceBot);
		int size = Math.min(width, height) - (gaugeSize + 5); // 5 is margin
		
		int x = (width - size) / 2;
		int y = (height - size) / 2;
		int centerX = width / 2;
		int centerY = height / 2;
		
		double angleSize = (size / 2) - gaugeSize;

		double offsetAngle = 1.3;
		// kręgi
		{
			//sektor 1 - podciśnienie - 45stopni
			{
				g2.setStroke(new BasicStroke(15, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
				double zoneStart = 0-offsetAngle;
				double zoneSize = 44+offsetAngle;
				double sta = 360 - (90 + angleStart + zoneStart);
				Point a = getLocation(sta, angleSize);
				Point b = getLocation(sta-zoneSize, angleSize);
				g2.setPaint(new GradientPaint(centerX+a.x, centerY-a.y, new Color(66, 179, 245), centerX+b.x, centerY-b.y, Color.BLUE));
				int fac = 8;
				g2.draw(new Arc2D.Double(x+fac, y+fac, size-fac*2, size-fac*2, sta, -zoneSize, Arc2D.OPEN));
			}
			//sektor 2 - ciśnienie robocze
			{
				double zoneStart = 45;
				double zoneSize = 134;
				double sta = 360 - (90 + angleStart + zoneStart);
				Point a = getLocation(sta, angleSize);
				Point b = getLocation(sta-zoneSize, angleSize);
				g2.setPaint(new GradientPaint(centerX+a.x, centerY-a.y, Color.BLUE, centerX+b.x, centerY-b.y, Color.ORANGE));
				int fac = 8;
				g2.draw(new Arc2D.Double(x+fac, y+fac, size-fac*2, size-fac*2, sta, -zoneSize, Arc2D.OPEN));
			}
			//sektor 3 - zbyt duże
			{
				double zoneStart = 180;
				double zoneSize = 45+offsetAngle;
				double sta = 360 - (90 + angleStart + zoneStart);
				Point a = getLocation(sta, angleSize);
				Point b = getLocation(sta-zoneSize+20, angleSize);//start red zone earleier
				g2.setPaint(new GradientPaint(centerX+a.x, centerY-a.y, Color.ORANGE, centerX+b.x, centerY-b.y, Color.RED));
				int fac = 8;
				g2.draw(new Arc2D.Double(x+fac, y+fac, size-fac*2, size-fac*2, sta, -zoneSize, Arc2D.OPEN));
			}
			//zewnętrzny krąg - margines
			{
				g2.setPaint(color1);
				g2.setStroke(new BasicStroke(2, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
				
				double zoneStart = 0-offsetAngle;
				double zoneSize = 225+offsetAngle*2;
				double sta = 360 - (90 + angleStart + zoneStart);
				int fac = 1;
				g2.draw(new Arc2D.Double(x+fac, y+fac, size-fac*2, size-fac*2, sta, -zoneSize, Arc2D.OPEN));
			}
			//wewnętrzny krąg - margines
			{
				g2.setPaint(color1);
				g2.setStroke(new BasicStroke(2, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
				
				double zoneStart = 0-offsetAngle;
				double zoneSize = 225+offsetAngle*2;
				double sta = 360 - (90 + angleStart + zoneStart);
				int fac = 15;
				g2.draw(new Arc2D.Double(x+fac, y+fac, size-fac*2, size-fac*2, sta, -zoneSize, Arc2D.OPEN));
			}
			{
				//zamknięcie po lewej
				double zoneStart = 0-offsetAngle;
				double sta = 360 - (90 + angleStart + zoneStart);
				
				Point a = getLocation(sta, angleSize+5);
				Point b = getLocation(sta, angleSize-7);
				g2.drawLine(centerX+a.x, centerY-a.y, centerX+b.x, centerY-b.y);
			}
			{
				//zamknięcie po prawej
				double start = 360 - (90 + angleStart);
				double end = start-gaugeSizeStopnie-offsetAngle;
				
				Point a = getLocation(end, angleSize+5);
				Point b = getLocation(end, angleSize-7);
				g2.drawLine(centerX+a.x, centerY-a.y, centerX+b.x, centerY-b.y);
			}
		}
		// rysowanie podziałek
		{
			Map<Integer,String> etykiety = new HashMap<Integer,String>();
			etykiety.put(0, "-0.5");//50kPa
			etykiety.put(45, "0");
			etykiety.put(90, "0.5");
			etykiety.put(135, "1");
			etykiety.put(180, "1.5");
			etykiety.put(225, "2");
			
			double start = 360 - (90 + angleStart);
			for(Integer i : etykiety.keySet()) {
				String text = etykiety.get(i);
				double angle = start-i;
				//Point p = getLocation(angle, angleSize);
				//g2.fillOval(centerX + p.x - 2, centerY - p.y - 2, 4, 4);
				int fac = 7;
				Point left = getLocation(angle-2, angleSize-fac);
				Point right = getLocation(angle+2, angleSize-fac);
				
				Point top = getLocation(angle, angleSize-fac-6);
				
				Path2D p = new Path2D.Double();
				p.moveTo(centerX + left.x, centerY - left.y);
				p.lineTo(centerX + top.x, centerY - top.y);
				p.lineTo(centerX + right.x, centerY - right.y);
				g2.fill(p);
				
				fac = 25;
				g2.setFont(g2.getFont().deriveFont(14f));
				FontMetrics ft = g2.getFontMetrics();
				Rectangle2D rect = ft.getStringBounds(text, g2);
				Point lbl = getLocation(angle, angleSize-fac);
				int usx = lbl.x+centerX;
				int usy = centerY-lbl.y;
				g2.drawString(text, (int)(usx-rect.getWidth()/2), (int)(usy+rect.getHeight()/2-ft.getDescent()));
				
				/*g2.drawString(text, (int) (x - r2.getWidth() / 2), (int) (y - r2.getHeight() /2 ));*/
			}
		}
		g2.dispose();
	}
	private BufferedImage background = null;

	private void drawText(Graphics2D g2, int x, int y, double size) {
		g2.setColor(getForeground());
		double max = maximum;
		double v = getValueFixed();
		double n = v / max * 100f;
		String text = String.valueOf((int) n) + "kPa";
		FontMetrics ft = g2.getFontMetrics();
		Rectangle2D r2 = ft.getStringBounds(text, g2);
		g2.drawString(text, (int) (x - r2.getWidth() / 2), (int) (y + size - r2.getHeight()));
	}

	private double getAngleOfValues() {
		double max = maximum;
		double v = getValueFixed();
		double n = v / max * 100f;
		double angle = n * (float) gaugeSizeStopnie / 100f;
		return angle;
	}

	private int getValueFixed() {
		return value > maximum ? maximum : value;
	}

	private Point getLocation(double angle, double size) {
		double x = Math.cos(Math.toRadians(angle)) * size;
		double y = Math.sin(Math.toRadians(angle)) * size;
		return new Point((int) x, (int) y);
	}
}
