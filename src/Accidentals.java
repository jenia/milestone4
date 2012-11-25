/***********************************************
This file is part of the ScoreDate project (http://www.mindmatter.it/scoredate/).

ScoreDate is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

ScoreDate is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with ScoreDate.  If not, see <http://www.gnu.org/licenses/>.

**********************************************/

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ResourceBundle;
import java.awt.Point;


/**
 * @author Massimo Callegari
 *
 */
public class Accidentals {
	
	Preferences appPrefs;
	private String type;
	private int amount;
	
	public Accidentals(String t, int count, Preferences p) 
	{
		appPrefs = p;
		type = t;
		amount = count;
	}

	public void setTypeAndCount(String t, int count)
	{
		type = t;
		amount = count;
		//System.out.println("[Accidentals - setTypeAndCount] type: " + type + ", count: " + count);
	}

	public int getNumber()
	{
		return amount;
	}
	
	public String getType()
	{
		return type;
	}
	
	public String getTonality(ResourceBundle bundle)
	{
		String tStr = "";

	    String DO = bundle.getString("_do");
	    String RE = bundle.getString("_re");
	    String MI = bundle.getString("_mi");
	    String FA = bundle.getString("_fa");
	    String SOL = bundle.getString("_sol");
	    String LA = bundle.getString("_la");
	    String SI = bundle.getString("_si");

	    if (amount == 0)
	    	tStr = DO + " Maj | " + LA + " min";
	    else if (amount == 1)
	    {
	      if (type.equals("#"))
	    	tStr = SOL + " Maj | " + MI + " min";
	      else
	    	tStr = FA + " Maj | " + RE + " min";
	    }
	    if (amount == 2)
	    {
	      if (type.equals("#"))
	    	tStr = RE + " Maj | " + SI + " min";
	      else
	    	tStr = SI + "b Maj | " + SOL + " min";
	    }
	    if (amount == 3)
	    {
	      if (type.equals("#"))
	    	tStr = LA + " Maj | " + FA + "# min";
	      else
	    	tStr = MI + "b Maj | " + DO + " min";
	    }
	    if (amount == 4)
	    {
	      if (type.equals("#"))
	    	tStr = MI + " Maj | " + DO + "# min";
	      else
	    	tStr = LA + "b Maj | " + FA + " min";
	    }
	    if (amount == 5)
	    {
	      if (type.equals("#"))
	    	tStr = SI + " Maj | " + SOL + "# min";
	      else
	    	tStr = RE + "b Maj | " + SI + "b min";
	    }
	    if (amount == 6)
	    {
	      if (type.equals("#")) 
	    	tStr = FA + "# Maj | " + RE + "# min";
	      else
	        tStr = SOL + "b Maj | " + MI + "b min";
	    }
	    if (amount == 7)
	    {
	      if (type.equals("#"))
	    	tStr = DO + "# Maj | " + LA + "# min";
	      else
	    	tStr = DO + "b Maj | " + LA + "b min";
	    }

	    return tStr;
	}

    private void drawAlteration(Graphics g, Font f,Point p, String altType) 
    {
    	((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.black);
		g.setFont(f.deriveFont(54f));
		if (altType == "B")
			g.drawString(altType, (int)(p.getX()+2), (int)(p.getY() + 21));
		else
			g.drawString(altType, (int)(p.getX()+2), (int)(p.getY() + 22));
	}

	public void paint(Graphics g, Font f, Point p, int clefMask) 
	{
	    String sharp = "B"; // # alteration
	    String flat = "b"; // b alteration
	    int clefOffset = 0;
	    
	    if (clefMask == appPrefs.BASS_CLEF)
	    	clefOffset = 10;
	    else if (clefMask == appPrefs.ALTO_CLEF)
	    	clefOffset = 5;
	    else if (clefMask == appPrefs.TENOR_CLEF)
	    	clefOffset = -5;

	    if (type.equals("#")) 
	    {
	      if (amount >= 1) // FA#
	        drawAlteration(g, f, new Point((int)p.getX(), (int)p.getY() - 15 + clefOffset), sharp);
	      if (amount >= 2) // DO#
   	  		drawAlteration(g, f, new Point((int)p.getX()+ 10, (int)p.getY()  + clefOffset), sharp);
	      if (amount >= 3) // SOL#
	      {
	    	if (clefMask == appPrefs.TENOR_CLEF)
	    		drawAlteration(g, f, new Point((int)p.getX() + 20, (int)p.getY() +  clefOffset + 15), sharp); // shift an octave down
	    	else	    	  
	        	drawAlteration(g, f, new Point((int)p.getX()+ 20, (int)p.getY() - 20 + clefOffset), sharp);
	      }
	      if (amount >= 4) // RE#
	        drawAlteration(g, f, new Point((int)p.getX()+ 30, (int)p.getY() - 5 + clefOffset), sharp);
	      if (amount >= 5) // LA#
	        drawAlteration(g, f, new Point((int)p.getX()+ 40, (int)p.getY() + 10 + clefOffset), sharp);
	      if (amount >= 6) // MI#
	      {
	    	if (clefMask == appPrefs.TENOR_CLEF)
	    		drawAlteration(g, f, new Point((int)p.getX()+ 50, (int)p.getY() + 25 + clefOffset), sharp);
	    	else
	    		drawAlteration(g, f, new Point((int)p.getX()+ 50, (int)(p.getY() - 10 + clefOffset)), sharp);
	      }
	      if (amount >= 7) // SI#
	        drawAlteration(g, f, new Point((int)(p.getX()+ 60), (int)(p.getY() + 5 + clefOffset)), sharp);
	    }

	    if (type.equals("b"))
	    {
	      if (amount >= 1) // SIb
	        drawAlteration(g, f, new Point((int)p.getX(), (int)(p.getY() + 5 + clefOffset)), flat);
	      if (amount >= 2) // MIb
	        drawAlteration(g, f, new Point((int)p.getX()+ 9, (int)(p.getY() - 10 + clefOffset)), flat);
	      if (amount >= 3) // LAb
	        drawAlteration(g, f, new Point((int)(p.getX()+ 18), (int)(p.getY() + 10 + clefOffset)), flat);
	      if (amount >= 4) // REb
	        drawAlteration(g, f, new Point((int)(p.getX()+ 27), (int)(p.getY() - 5 + clefOffset)), flat);
	      if (amount >= 5) // SOLb
	        drawAlteration(g, f, new Point((int)(p.getX()+ 36), (int)(p.getY() + 15 + clefOffset)), flat);
	      if (amount >= 6) // DOb
	        drawAlteration(g, f, new Point((int)(p.getX()+ 45), (int)(p.getY() + clefOffset)), flat);
	      if (amount >= 7) // FAb
	        drawAlteration(g, f, new Point((int)p.getX()+ 54, (int)(p.getY() + 20 + clefOffset)), flat);
	    }
	  }
}
