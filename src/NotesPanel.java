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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class NotesPanel extends JPanel implements MouseListener
{
	private static final long serialVersionUID = -1735923156425027329L;
	Font appFont;
	Preferences appPrefs;
	private Vector<Note> notes; // first clef notes
	private Vector<Note> notes2; // second clef notes

    private int clefMask = 1;
    private Vector<Integer> clefs = new Vector<Integer>();    
	private int rowsDistance = 90; // distance in pixel between staff rows
	private int noteDistance = 72; // distance in pixel between 1/4 notes
	private int firstNoteXPos = 50;

	private int staffWidth;
	// kinda dirty variables used by setNotesPosition
	int tmpY = 0;
	int tmpX = 0;

	private boolean inlineMode = false;
	private int singleNoteIndex = -1; // force the painting of a single note (first clef)
	private int singleNote2Index = -1; // force the painting of a single note (second clef)
	private int selectedClef = 1; 

	private JLabel learningText;

	// edit mode, activated from the exercise panel
	boolean editMode = false;
	boolean editModeRhythm = false;
	int editNoteIndex = -1;
	int editNoteSelX = -1, editNoteSelY = -1, editNoteSelW = -1, editNoteSelH = -1;
	NoteGenerator editNG;

	private double globalScale = 1.0;

	public NotesPanel(Font f, Preferences p, Vector<Note> n, Vector<Note> n2, boolean inline)
	{
		appFont = f;
		appPrefs = p;
		notes = n;
		notes2 = n2;
		inlineMode = inline;
		globalScale = 1.0;
		
		learningText = new JLabel("", null, JLabel.CENTER);
		learningText.setForeground(Color.decode("0x869EBA"));
		learningText.setVisible(false);
		
		setLayout(null);
		
		add(learningText);
		//setDoubleBuffered(false);
    	//setBackground(Color.blue);
		addMouseListener(this);
	}

    public void setRowsDistance(int dist)
    {
    	rowsDistance = dist;
    }
    
    public int getRowsDistance()
    {
    	return rowsDistance;
    }

    public void setClefs(int type)
    {
    	clefMask = type;
    	clefs.clear();

    	if ((clefMask & appPrefs.TREBLE_CLEF) > 0) clefs.add(appPrefs.TREBLE_CLEF);
    	if ((clefMask & appPrefs.BASS_CLEF) > 0) clefs.add(appPrefs.BASS_CLEF);
    	if ((clefMask & appPrefs.ALTO_CLEF) > 0) clefs.add(appPrefs.ALTO_CLEF);
    	if ((clefMask & appPrefs.TENOR_CLEF) > 0) clefs.add(appPrefs.TENOR_CLEF);

		Font ltf = new Font("Arial", Font.BOLD, 30);
		learningText.setPreferredSize( new Dimension(200, 50));
		learningText.setBounds((getWidth() / 2) - 150, getHeight() - 60, 300, 50);
		learningText.setText("");
		learningText.setFont(ltf);
		
    	repaint();
    }
    
    public int getClef(int idx)
    {
    	if (idx < 0 || idx >= clefs.size())
    		return 0;
    	return clefs.get(idx);
    }
    
    public void setFirstNoteXPosition(int xpos)
    {
    	firstNoteXPos = xpos;
    }
    
    public void setStaffWidth(int w)
    {
    	//System.out.println("[NP] staff width: " + w);
    	staffWidth = w;
    }
    
    public void setScale(double factor)
    {
    	globalScale = factor;
    }
    
    public void setEditMode(boolean active, boolean isRhythm)
    {
    	editMode = active;
    	editModeRhythm = isRhythm;
    }
    
    public void setEditNoteIndex(int idx)
    {
    	editNoteIndex = idx;
    }
    
    public int getEditNoteIndex()
    {
    	return editNoteIndex;
    }

    public void setEditNoteGenerator(NoteGenerator ng)
    {
    	editNG = ng;
    }

    public void setNotesSequence(Vector<Note> n, Vector<Note> n2)
    {
    	int minLev = 9;
    	int maxLev = 17;
    	int row1H = 0;
    	int row2H = 0;
    	if (notes != null) notes.clear();
    	if (notes2 != null) notes2.clear();
    	if (n != null)
    	{
    		notes = n;
    		row1H = 90;
    		for (int i = 0; i < n.size(); i++)
    		{
    			int lev = n.get(i).getLevel();
    			if (lev < minLev) minLev = lev;
    			if (lev > maxLev) maxLev = lev;
    		}
    		System.out.println("Clef 1: minLev: " + minLev + ", maxLev: " + maxLev);
    		if (maxLev > 17) row1H += (maxLev - 17) * 5;
    		if (minLev < 9) row1H += (9 - minLev) * 5;
    	}
    	minLev = 9;
    	maxLev = 17;
    	if (n2 != null && clefs.size() == 2)
    	{
    		notes2 = n2;
    		row2H = 90;
  		    for (int i = 0; i < n2.size(); i++)
    		{
  		    	int lev = n2.get(i).getLevel();
    			if (lev < minLev) minLev = lev;
    			if (lev > maxLev) maxLev = lev;
    		}
    		System.out.println("Clef 2: minLev: " + minLev + ", maxLev: " + maxLev);
    		if (maxLev > 17) row2H += (maxLev - 17) * 5;
    		if (minLev < 9) row2H += (9 - minLev) * 5;
    	}
    	rowsDistance = row1H + row2H;
    	System.out.println("[setNotesSequence] rowsDistance set to: " + rowsDistance);
    }

    public void setNotesPositions()
    {
    	tmpX = firstNoteXPos;
    	tmpY = 0;

    	if (notes == null)
    		return;

    	for (int i = 0; i < notes.size(); i++)
    	{
    		setSingleNotePosition(notes.get(i), true);
    		//System.out.println("[Note(1): #" + i + "] type: " + notes.get(i).type + ", xpos: " + notes.get(i).xpos + ", ypos: " + notes.get(i).ypos);
    	}
    	
    	if (notes2 == null)
    		return;

    	tmpX = firstNoteXPos;
    	tmpY = 0;

    	for (int i = 0; i < notes2.size(); i++)
    	{
    		setSingleNotePosition(notes2.get(i), true);
    		//System.out.println("[Note(2): #" + i + "] type: " + notes.get(i).type + ", xpos: " + notes.get(i).xpos + ", ypos: " + notes.get(i).ypos);
    	}
    }

    public void setSingleNotePosition(Note note, boolean setXpos)
    {
   		int type = note.getType();
   		int ypos = (note.getLevel() * 5) + 11;
   		int yOffset = 0;

		if (tmpX >= staffWidth)
		{
			tmpX = firstNoteXPos;
			tmpY += rowsDistance;
		}

		if (note.isSecondRow() == true)
			yOffset += (rowsDistance / 2);

		note.setAddLinesNumber(0);

		if (note.getLevel() < 7)
		{
			note.setAddLinesNumber(4 - (note.getLevel() / 2));
			note.setAddLinesYpos(ypos + tmpY - 6 + ((note.getLevel()%2) * 5));
			if (note.isSecondRow() == true)
				note.setAddLinesYpos(note.getAddLinesYpos()
						+ (rowsDistance/2));
		}
		else if  (note.getLevel() > 17)
		{
			note.setAddLinesNumber((note.getLevel() / 2) - 8);
			note.setAddLinesYpos(ypos + tmpY - 6 - ((note.getLevel() - 18) * 5));
			if (note.isSecondRow() == true)
				note.setAddLinesYpos(note.getAddLinesYpos()
						+ (rowsDistance/2));
		}

		if (type == 0) // whole note
			ypos++;
		else if (type == 2 || type == 7) // quarter or dotted quarter note
		{
			if (note.getLevel() < 12)
				ypos += 41;
		}
		else if (type == 3) // eighth note
		{
			if (note.getLevel() < 12) 
				ypos += 30;
		}
		else if (type == 4) // triplets
		{
			if (note.getTripletValue() < 0)
				ypos += 41;
		}    		
		else if (type == 5) // silence
		{
			if (note.getDuration() == 4)
				ypos -= 16;
			else if (note.getDuration() == 2)
				ypos -= 12;
			else if (note.getDuration() == 1)
				ypos += 13;
			else if (note.getDuration() == 0.5)
				ypos += 13;
		}

		note.setYpos(ypos + tmpY + yOffset);
		if (inlineMode == false && setXpos == true) // the inline game controls X position itself
		{
			note.setXpos(tmpX);
			tmpX += (note.getDuration() * noteDistance);
		}
    }

    public void setLearningTips(String tip, boolean enable)
    {
    	if (enable == true)
    		learningText.setText(tip);

   		learningText.setVisible(enable); 	
    }

    public void highlightNote(int index, int clef, boolean enable)
    {
    	if (clef == 1)
    	{
    		if (notes.size() == 0) return;
    		singleNoteIndex = index;
    		notes.get(index).setHighlight(enable);
    		repaint();
    		singleNoteIndex = -1;
    	}
    	else if (clef == 2)
    	{
    		if (notes2.size() == 0) return;
    		singleNote2Index = index;
    		notes2.get(index).setHighlight(enable);
    		repaint();
    		singleNote2Index = -1;
    	}
    }

    public void mouseClicked(MouseEvent e) 
	{
		//System.out.println("Mouse clicked (# of clicks: " + e.getClickCount() + ")");
    	int mouseX = e.getX();
    	int mouseY = e.getY();
		System.out.println("[Edit mode] clicked X pos: " + mouseX + ", Y pos: " + mouseY);
		//System.out.println("editNoteSelX: " + editNoteSelX + ",editNoteSelY: " + editNoteSelY + ", editNoteSelW: " + editNoteSelW + ", editNoteSelH: "+ editNoteSelH);

		if (editMode == false || editModeRhythm == true)
			return;

		if (clefs.size() > 1)
		{
			int selY = editNoteSelY;
			int selH = editNoteSelH;

			if (notes.size() == 0 && notes2.size() == 0)
			{
				selY = (selectedClef - 1) * (rowsDistance / 2);
				selH = rowsDistance / 2;
				int newClef = selectedClef;
				if (selectedClef == 1 && mouseY >= selY + selH && mouseY < selY + (selH * 2))
					newClef = 2;
				else if (selectedClef == 2 && mouseY < selY && mouseY >= selY - selH)
					newClef = 1;
				if (newClef != selectedClef)
				{
					this.firePropertyChange("newSelectedClef", selectedClef, newClef);
					selectedClef = newClef;
					repaint();
				}
				return;
			}
		}


		// if mouse clicked inside the current selection, then manage a pitch change
		if (editNoteIndex != -1 && mouseX >= editNoteSelX && mouseX < editNoteSelX + editNoteSelW && 
			mouseY >= editNoteSelY && mouseY < editNoteSelY + editNoteSelH)
		{
			Note tmpNote = null;
			if (selectedClef == 1)
				tmpNote = notes.get(editNoteIndex);
			else if (selectedClef == 2)
				tmpNote = notes2.get(editNoteIndex);
			int origLevel = tmpNote.getLevel();
			int newLevel = (mouseY - editNoteSelY - 4) / 5;
			if (newLevel != origLevel)
			{
				tmpNote.setLevel((mouseY - editNoteSelY - 4) / 5);
				tmpX = tmpNote.getXpos(); // must 'rewind' xpos to avoid wrong check for second line
				if (selectedClef == 1)
					tmpY = editNoteSelY;
				else if (selectedClef == 2)
					tmpY = editNoteSelY - (rowsDistance/2);
				setSingleNotePosition(tmpNote, false); // do not touch X position !
				tmpNote.setPitch(editNG.getPitchFromClefAndLevel(clefs.get(selectedClef - 1), tmpNote.getLevel())); // retrieve the base pitch of this level and clef
				tmpNote.setPitch(editNG.getAlteredFromBase(tmpNote.getPitch())); // retrieve a new pitch if it is altered
				if (tmpNote.getAltType() != 0)
					this.firePropertyChange("levelWasAltered", origLevel, newLevel);
				tmpNote.setAltType(0);

				System.out.println("[Edit mode] note level: " + tmpNote.getLevel() + ", pitch = " + tmpNote.getPitch());
				this.firePropertyChange("levelChanged", origLevel, newLevel);
				repaint();
			}
		}
		else
		{
			System.out.println("[Edit mode] look for a note to select...");
			// look for a note to select
			int lookupY = 0, tmpClef = 1, selH = rowsDistance;
			Vector<Note>tmpNotes = notes;
			if (clefs.size() > 1) selH = rowsDistance / 2;

			for (int c = 0; c < clefs.size(); c++)
			{
			  int lookupX = firstNoteXPos;
			  for (int i = 0; i < tmpNotes.size(); i++)
			  {
				Note tmpNote = tmpNotes.get(i);

				//System.out.println("Clef: " + tmpClef + " - #" + i + ": nX: " + (lookupX - 5) + ", nY: " + lookupY + ", nX1: " + (int)(lookupX + (tmpNote.duration * noteDistance)) + ", nY1: " + (tmpY + selH));
				if (mouseX >= lookupX - 5 && mouseX < (int)(lookupX + (tmpNote.getDuration() * noteDistance)) && 
					mouseY >= lookupY && mouseY < lookupY + selH)
				{
					if (tmpClef != selectedClef)
					{
						this.firePropertyChange("newSelectedClef", selectedClef, tmpClef);
						selectedClef = tmpClef;
					}
					System.out.println("[Edit mode] selected note #" + i + ", pitch = " + tmpNote.getPitch());
					this.firePropertyChange("selectionChanged", editNoteIndex, i);
					setEditNoteIndex(i);
					repaint();
					return;
				}
				lookupX += (tmpNote.getDuration() * noteDistance);
				if (lookupX >= staffWidth)
				{
					lookupX = firstNoteXPos;
					lookupY += rowsDistance;
				}
			  }
			  // haven't found anything ? Look if we clicked on an empty clef
			  if (clefs.size() > 1)
			  {
				int newClef = selectedClef;
				if (tmpClef == 1 && notes2 != null && notes2.size() == 0)
					newClef = 2;
				else if (tmpClef == 2 && notes != null && notes.size() == 0)
					newClef = 1;
				if (newClef != selectedClef)
				{
					this.firePropertyChange("newSelectedClef", selectedClef, newClef);
					selectedClef = newClef;
					setEditNoteIndex(-1);
					repaint();
					return;
				}
			  }
			  lookupY = rowsDistance / 2;
			  tmpClef++;
			  tmpNotes = notes2;
			}
		}
	}

	public void mousePressed(MouseEvent e) 
	{
		//System.out.println("Mouse pressed; # of clicks: " + e.getClickCount());
	}

    public void mouseReleased(MouseEvent e) 
    {
    	//System.out.println("Mouse released; # of clicks: " + e.getClickCount());
    }

    public void mouseEntered(MouseEvent e) 
    {
    	//System.out.println("Mouse entered");
    }

    public void mouseExited(MouseEvent e) 
    {
    	//System.out.println("Mouse exited");
    }

    private void drawNote(Graphics g, int index, int clef) 
    {
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    	String symbol = "";
		Note note = null;
		if (clef == 1) note = notes.get(index);
		else if (clef == 2) note = notes2.get(index);
		int type = note.getType();

		if (editMode == true && clef == selectedClef && index == editNoteIndex)
		{
			// gotta find the original note Y base position :'(
			Vector<Note>tmpNotes = notes;
			if (clef == 2)
				tmpNotes = notes2;

			int lookupX = firstNoteXPos;
			int lookupY = (clef - 1) * (rowsDistance / 2);
			for (int i = 0; i < editNoteIndex; i++)
			{
				lookupX += (tmpNotes.get(i).getDuration() * noteDistance);
				if (lookupX >= staffWidth)
				{
					lookupX = firstNoteXPos;
					lookupY += rowsDistance;
				}
			}
			g.setColor(new Color(0xA2, 0xDD, 0xFF, 0x7F));
			editNoteSelX = note.getXpos() - 5;
			editNoteSelY = lookupY;
			editNoteSelW = (int)(note.getDuration() * noteDistance);
			editNoteSelH = 130;
			g.fillRoundRect(editNoteSelX, editNoteSelY, editNoteSelW, editNoteSelH, 10, 10);
			if (clefs.size() > 1)
			{
				// highlight the current clef
				g.setColor(new Color(0x00, 0xFF, 0x00, 0x1F));
				g.fillRoundRect(0, editNoteSelY + 5, firstNoteXPos - 5, editNoteSelH - 10, 10, 10);
			}
		}

		if (note.isHighlight() == true)
	    	g.setColor(Color.blue);
		else 
			g.setColor(Color.black);

		// draw additional lines if needed
		if (note.getAddLinesNumber() > 0)
		{
			int addLineWidth = (note.getType() == 0)?23:16;
			for (int j = 0; j < note.getAddLinesNumber(); j++)
				g.drawLine(note.getXpos() - 5, note.getAddLinesYpos() + (j * 10), note.getXpos() + addLineWidth, note.getAddLinesYpos() + (j * 10));
		}

    	g.setFont(appFont.deriveFont(57f));
		if (type == 0) symbol = "w"; // whole note
		else if (type == 1 || type == 6)
		{
			symbol = "h"; // half note
			if (type == 6) // dotted half
				g.fillOval(note.getXpos() + 15, note.getYpos() - 8, 5, 5);
		}
		else if (type == 2 || type == 7)
		{
			if (note.getLevel() >= 12) symbol = "q"; // quarter note upward
			else symbol = "" + (char)0xF6; // quarter note downward
			if (type == 7) // dotted quarter
			{
				if (note.getLevel() >= 12)
					g.fillOval(note.getXpos() + 15, note.getYpos() - 8, 5, 5);
				else
					g.fillOval(note.getXpos() + 15, note.getYpos() - 49, 5, 5);
			}
		}
		else if (type == 3)
		{
			if (note.getLevel() >= 12) symbol = "" + (char)0xC8; // eighth note upward 
			else symbol = "" + (char)0xCA; // eighth note downward
		}
		else if (type == 4)
		{
			if (note.getTripletValue() > 0)
				symbol = "q"; // triplet note upward
			else symbol = "" + (char)0xF6; // triplet note downward
		}
		else if (type == 5) // silence
		{
			if (note.getDuration() == 4)
				g.fillRect(note.getXpos() + (int)(noteDistance * 1.55), note.getYpos(), 14, 6);
			else if (note.getDuration() == 2)
				g.fillRect(note.getXpos(), note.getYpos(), 14, 6);
			else if (note.getDuration() == 1)
				symbol = "Q";
			else if (note.getDuration() == 0.5)
			{
				g.setFont(appFont.deriveFont(50f));
				symbol = "E";
			}
		}

		g.drawString(symbol, note.getXpos(), note.getYpos());

		// draw alteration symbol if required
		if (note.getAltType() != 0)
		{
			int altYOff = 0;
			g.setFont(appFont.deriveFont(50f));
			if (note.getLevel() < 12)
			{
				if (note.getType() == 2 || note.getType() == 7) 
					altYOff = -41;
				else if (note.getType() == 3)
					altYOff = -30;
			}
			if (note.getAltType() == -2)
			{
				g.drawString("b", note.getXpos() - 19, note.getYpos() + altYOff);
				g.drawString("b", note.getXpos() - 12, note.getYpos() + altYOff);
			}
			else if (note.getAltType() == -1)
				g.drawString("b", note.getXpos() - 12, note.getYpos() + altYOff);
			else if (note.getAltType() == 1)
				g.drawString("B", note.getXpos() - 12, note.getYpos() + altYOff);
			else if (note.getAltType() == 2)
				g.drawString("" + (char)0xBD, note.getXpos() - 14, note.getYpos() + altYOff);
		}

		// draw triplets special graphics
    	if (note.getTripletValue() != 0)
    	{
    		int tsub = 0; 
    		if (note.getTripletValue() < 0) // notes downward
    		{
    			if (note.getTripletValue() <= -1000) tsub = 1000;
    			int tripletBarYPos = note.getYpos() + (((Math.abs(note.getTripletValue()) - tsub) - note.getLevel()) * 5);
    			g.drawLine(note.getXpos(), note.getYpos() - 15, note.getXpos(), tripletBarYPos - 15);
    			if (note.getTripletValue() > -1000)
    			{
    				g.setFont(new Font("Arial", Font.BOLD, 15));
            		g.drawString("3", note.getXpos() + 22, tripletBarYPos + 3);
    				g.fillRect(note.getXpos(), tripletBarYPos - 15, 49, 5);
    			}
    		}
    		else // notes upward
    		{
    			if (note.getTripletValue() >= 1000) tsub = 1000;
    			int tripletBarYPos = note.getYpos() - ((note.getLevel() - (note.getTripletValue() - tsub)) * 5);
    			g.drawLine(note.getXpos() + 11, note.getYpos() - 40, note.getXpos() + 11, tripletBarYPos - 40);
    			if (note.getTripletValue() < 1000)
    			{
    				g.setFont(new Font("Arial", Font.BOLD, 15));
            		g.drawString("3", note.getXpos() + 32, tripletBarYPos - 42);
    				g.fillRect(note.getXpos() + 11, tripletBarYPos - 40, 49, 5);
    			}
    		}	
    	}
    }

	protected void paintComponent(Graphics g) 
 	{
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		if (globalScale != 1.0)
			((Graphics2D) g).scale(globalScale, globalScale);

    	if (singleNoteIndex == -1 && singleNote2Index == -1)
    	{
        	g.setColor(Color.black);
        	if (notes != null)
        	{
        		for (int i = 0; i < notes.size(); i++)
        			drawNote(g, i, 1);
        	}
        	if (notes2 != null)
        	{
        		for (int i = 0; i < notes2.size(); i++)
        			drawNote(g, i, 2);
        	}
    		if (editMode == true && editNoteIndex == -1 && clefs.size() > 1)
    		{
    			g.setColor(new Color(0x00, 0xFF, 0x00, 0x1F));
    			if (selectedClef == 1)
    				g.fillRoundRect(0, 5, firstNoteXPos - 5, 120, 10, 10);
    			else if (selectedClef == 2)
    				g.fillRoundRect(0, rowsDistance/2, firstNoteXPos - 5, 120, 10, 10);
    		}
    	}
    	else
    	{
    		if (singleNote2Index == -1)
    			drawNote(g, singleNoteIndex, 1);
    		else if (singleNoteIndex != -1 && singleNoteIndex < notes2.size())
    			drawNote(g, singleNoteIndex, 2);
    	}
 	}
}
