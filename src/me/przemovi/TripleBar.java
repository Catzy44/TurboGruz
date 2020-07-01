package me.przemovi;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JLabel;

public class TripleBar extends JComponent{
	private static final long serialVersionUID = -2341657292139397697L;
	private int value = 80;

	public TripleBar() {
		setFont(new JLabel().getFont().deriveFont(0, 14));
		setPreferredSize(new Dimension(250, 220));
		setForeground(new Color(60, 60, 60));
	}
	
	private static final int padding = 20;
	private static final int margin = 8;
	
	private static final int barHeight = 20;
	
	private int skalaMin = 40;
	private int skalaMax = 120;
	private static final int skalaHeight = 15;
	
	private static final int descLinesInn = 3;
	
	private Color fontColor = Color.LIGHT_GRAY;
	private Color linesColor = Color.LIGHT_GRAY;
	private Color barBgColor = Color.GRAY;
	private Color barInnColor = Color.RED;
	private Color barTextColor = Color.BLACK;
	private Font font = new JLabel().getFont();
	
	private String desc = "WATER TEMP";
	private String valName = "C";
	
	private int getValueFixed() {
		return (value > skalaMax) ? skalaMax : (value < skalaMin ? skalaMin : value);
	}
	
	public void setDescription(String desc) {
		this.desc = desc;
	}
	
	public void setMin(int min) {
		this.skalaMin = min;
	}
	
	public void setMax(int max) {
		this.skalaMax = max;
	}
	
	public void setValName(String val) {
		this.valName = val;
	}
	
	public void setValue(int value) {
		this.value = value;
		super.repaint();
	}
	public void setValue(float value) {
		this.value = (int)value;
		super.repaint();
	}
	
	static public final float map(float value, float start1, float stop1, float start2, float stop2) {
		float outgoing = start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1));
		String badness = null;
		if (outgoing != outgoing) {
			badness = "NaN (not a number)";

		} else if (outgoing == Float.NEGATIVE_INFINITY || outgoing == Float.POSITIVE_INFINITY) {
			badness = "infinity";
		}
		if (badness != null) {
			final String msg = String.format("map(%s, %s, %s, %s, %s) called, which returns %s", nf(value), nf(start1), nf(stop1), nf(start2), nf(stop2), badness);
			System.err.println(msg);
		}
		return outgoing;
	}
	
	private static String nf(float s) {
		return s+"";
	}
	
	@Override
	public void paint(Graphics grphcs) {
		Graphics2D g2 = (Graphics2D) grphcs;
		buildBg(g2);
		
		int width = getWidth();
		FontMetrics ft = g2.getFontMetrics();
		
		// PASEK POSTĘPU
		g2.setPaint(barInnColor);
		
		//val->skalaMax ---> 0->barWidth
		int barWidth = width-padding*2;
		int barFilled = (int)map(getValueFixed(),skalaMin,skalaMax,0,barWidth);

		Path2D p = new Path2D.Double();
		p.moveTo(padding, barLocH);

		p.lineTo(width - padding - (barWidth-barFilled), barLocH);
		p.lineTo(width - padding - (barWidth-barFilled), barLocH + barHeight);
		p.lineTo(padding, barLocH + barHeight);
		p.lineTo(padding, barLocH);
		g2.fill(p);
		
		g2.setPaint(barTextColor);
		String s = value+" "+valName;
		Rectangle2D r1 = ft.getStringBounds(s, g2);
		int fontHeight = (int) r1.getHeight() - ft.getDescent();
		g2.drawString(s, padding+margin, barLocH+barHeight/2+fontHeight/2);

		super.paint(grphcs);
	}
	
	private int barLocH = -1;
	private BufferedImage background = null;
	private void buildBg(Graphics2D main) {
		if(background != null) {
			main.drawImage(background, 0, 0, null);
			return;
		}
		background = new BufferedImage(getWidth(),getHeight(),BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = background.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		int width = getWidth();

		int offsetH = 0;

		g2.setPaint(fontColor);
		g2.setFont(font);

		FontMetrics ft = g2.getFontMetrics();

		// LEWA ETYKIETA
		Rectangle2D r1 = ft.getStringBounds(skalaMin + "", g2);
		int fontHeight = (int) r1.getHeight() - ft.getDescent();
		g2.drawString(skalaMin + "", (int) (padding - r1.getWidth() / 2), fontHeight + offsetH);
		// PRAWA
		Rectangle2D r2 = ft.getStringBounds(skalaMax + "", g2);
		g2.drawString(skalaMax + "", (int) (width - padding - r2.getWidth() / 2), fontHeight + offsetH);
		// ŚRODKOWA
		int skalaMid = (skalaMin + skalaMax) / 2;
		Rectangle2D r3 = ft.getStringBounds(skalaMid + "", g2);
		g2.drawString(skalaMid + "", (int) (width / 2 - r3.getWidth() / 2), fontHeight + offsetH);

		offsetH += fontHeight + margin;

		// PIONOWE LINIE
		g2.setPaint(linesColor);

		g2.drawLine(padding, offsetH, padding, offsetH + skalaHeight);
		g2.drawLine(width - padding, offsetH, width - padding, offsetH + skalaHeight);
		g2.drawLine(width / 2, offsetH, width / 2, offsetH + skalaHeight);
		// POZIOMA LINIA
		g2.drawLine(padding, offsetH + skalaHeight, width - padding, offsetH + skalaHeight);

		offsetH += skalaHeight + margin;
		barLocH = offsetH;

		// PASEK POSTĘPU
		g2.setPaint(barBgColor);

		int barOffsetY = (int) (offsetH);
		int barOffsetX = (int) (padding);

		Path2D p = new Path2D.Double();
		p.moveTo(barOffsetX, barOffsetY);

		p.lineTo(width - barOffsetX, barOffsetY);
		p.lineTo(width - barOffsetX, barOffsetY + barHeight);
		p.lineTo(barOffsetX, barOffsetY + barHeight);
		p.lineTo(barOffsetX, barOffsetY);
		g2.fill(p);

		offsetH += barHeight + padding;

		// OPIS WSKAŹNIKA
		g2.setPaint(fontColor);
		g2.setFont(font);

		Rectangle2D des = ft.getStringBounds(desc, g2);
		g2.drawString(desc, (int) (width / 2 - des.getWidth() / 2), (int) (offsetH + (des.getHeight() - ft.getDescent()) / 2));

		// POZIOME LINIE
		g2.setPaint(linesColor);
		g2.drawLine(descLinesInn, offsetH, (int) (width / 2 - des.getWidth() / 2 - margin), offsetH);
		g2.drawLine((int) (width / 2 + des.getWidth() / 2 + margin), offsetH, width - descLinesInn, offsetH);
		// PIONOWE LINIE
		g2.drawLine(descLinesInn, offsetH - barLocH, descLinesInn, offsetH);
		g2.drawLine(width - descLinesInn, offsetH - barLocH, width - descLinesInn, offsetH);
		
		g2.dispose();
		main.drawImage(background, 0, 0, null);
	}
	
	private void drawStringCenteredOn(int x, int y,String s) {
		Graphics2D g2 = (Graphics2D) this.getGraphics();
		Rectangle2D rect = metrics(s);
		//g2.drawString(s, (int)(x-rect.getWidth()/2), (int)(y+rect.getHeight()/2-ft.getDescent()));
	}
	
	private Rectangle2D metrics(String s) {
		Graphics2D g2 = (Graphics2D) this.getGraphics();
		
		FontMetrics ft = g2.getFontMetrics();
		return ft.getStringBounds(s, g2);
	}
}
