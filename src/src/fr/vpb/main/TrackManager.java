package fr.vpb.main;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

public class TrackManager{
	
	public static final int MetaEventOffset = 1;
	
	public static final void addNotesToTrack(Track From, Track To) throws InvalidMidiDataException{
		for(int i = 0; i < From.size(); i++){
			MidiEvent Me = From.get(i);
			MidiMessage Mm = Me.getMessage();
			if(Mm instanceof ShortMessage){
				ShortMessage Sm = (ShortMessage) Mm;
				int Command = Sm.getCommand();
				int Com = -1;
				if(Command == ShortMessage.NOTE_ON){
					Com = MetaEventOffset;
				}
				else if(Command == ShortMessage.NOTE_OFF){
					Com = MetaEventOffset + 1;
				}
				if(Com > 0){
					byte[] b = Sm.getMessage();
					int l = (b == null ? 0 : b.length);
					MetaMessage MetaMessage = new MetaMessage(Com, b, l);
					MidiEvent Me2 = new MidiEvent(MetaMessage, Me.getTick());
					To.add(Me2);
				}
			}
		}
	}
	
}
