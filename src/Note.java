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

public class Note 
{
	private int xpos;
	private int ypos;
	private int type; // 0 whole, 1 half, 2 quarter, 3 eighth, 4 triplet, 5 silence, 6 dotted half, 7 dotted quarter
	private double duration;
	private double timestamp;
	private int clef; // note clef
	private int altType; // alteration to be displayed. Can be: -2 = double flat, -1 = flat, 0 = none, 1 = sharp, 2 = natural
	private boolean secondRow; // indicates whether the note is on the first or second row
	private int level; // note level as handled in the ClefSelector
	private int addLinesNumber; // number of additional lines (if present)
	private int addLinesYpos; // Y position of the first additional line (if present)
	private int pitch; // MIDI note pitch
	private int tripletValue = 0;
	private boolean highlight; // used when playing a rhtyhm or score sequence

	

	public Note(int xPos, int nClef, int nLevel, int nPitch, int nType, boolean nSecondRow, int nAlt)
	{
		xpos = xPos;
		clef = nClef;
		altType = nAlt;
		level = nLevel;
		pitch = nPitch;
		type = nType;
		secondRow = nSecondRow;
		addLinesNumber = 0;
		addLinesYpos = 0;
		highlight = false;
		timestamp = 0;

		switch(type)
		{
			case 0: duration = 4; break;
			case 1: duration = 2; break;
			case 2: duration = 1; break;
			case 3: duration = 0.5; break;
			case 4: duration = 1.0 / 3.0; break;
			case 5: duration = 0; level = 12; pitch = 71; break;
			case 6: duration = 3; break;
			case 7: duration = 1.5; break;
		}
		
		//System.out.println("[Note] t: " + type + ", p: " + pitch + ", l: " + level + ", dur: " + duration + ", alt: " + altType);
		
		ypos = 0; // y positions are calculated by the NotesPanel
	}
	
	public double getDuration(int type)
	{
		double dur = 1;
		switch(type)
		{
			case 0: dur = 4; break;
			case 1: dur = 2; break;
			case 2: dur = 1; break;
			case 3: dur = 0.5; break;
			case 4: dur = 1.0 / 3.0; break;
			case 5: dur = 0; break;
			case 6: dur = 3; break;
			case 7: dur = 1.5; break;
		}
		return dur;
	}
	
	public int getXpos() {
		return xpos;
	}

	public void setXpos(int xpos) {
		this.xpos = xpos;
	}

	public int getYpos() {
		return ypos;
	}

	public void setYpos(int ypos) {
		this.ypos = ypos;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public double getDuration() {
		return duration;
	}

	public void setDuration(double duration) {
		this.duration = duration;
	}


	public double getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(double timestamp) {
		this.timestamp = timestamp;
	}

	public int getClef() {
		return clef;
	}

	public void setClef(int clef) {
		this.clef = clef;
	}

	public int getAltType() {
		return altType;
	}

	public void setAltType(int altType) {
		this.altType = altType;
	}

	public boolean isSecondRow() {
		return secondRow;
	}

	public void setSecondRow(boolean secondRow) {
		this.secondRow = secondRow;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getAddLinesNumber() {
		return addLinesNumber;
	}

	public void setAddLinesNumber(int addLinesNumber) {
		this.addLinesNumber = addLinesNumber;
	}

	public int getAddLinesYpos() {
		return addLinesYpos;
	}

	public void setAddLinesYpos(int addLinesYpos) {
		this.addLinesYpos = addLinesYpos;
	}

	public int getPitch() {
		return pitch;
	}

	public void setPitch(int pitch) {
		this.pitch = pitch;
	}

	public int getTripletValue() {
		return tripletValue;
	}

	
	public boolean isHighlight() {
		return highlight;
	}

	public void setHighlight(boolean highlight) {
		this.highlight = highlight;
	}
	
	public void setTripletValue(int val)
	{
		tripletValue = val;
	}
	
	public void setTimeStamp(double ts)
	{
		timestamp = ts;
	}
}
