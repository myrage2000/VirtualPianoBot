package fr.vpb.main;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Track;

public class Main{
	
	private static boolean[] NoteToKeyShift = new boolean[128];
	private static int[] NoteToKey = new int[128];
	private static Robot Robot;
	
	private static Sequencer Sequencer;
	
	
	private static void createNoteToKey(){
		NoteToKey[24] = KeyEvent.VK_1;
		NoteToKey[26] = KeyEvent.VK_2;
		NoteToKey[28] = KeyEvent.VK_3;
		NoteToKey[29] = KeyEvent.VK_4;
		NoteToKey[31] = KeyEvent.VK_5;
		NoteToKey[33] = KeyEvent.VK_6;
		NoteToKey[35] = KeyEvent.VK_7;
		NoteToKey[36] = KeyEvent.VK_8;
		NoteToKey[38] = KeyEvent.VK_9;
		NoteToKey[40] = KeyEvent.VK_0;
		NoteToKey[41] = KeyEvent.VK_Q;
		NoteToKey[43] = KeyEvent.VK_W;
		NoteToKey[45] = KeyEvent.VK_E;
		NoteToKey[47] = KeyEvent.VK_R;
		NoteToKey[48] = KeyEvent.VK_T;
		NoteToKey[50] = KeyEvent.VK_Y;
		NoteToKey[52] = KeyEvent.VK_U;
		NoteToKey[53] = KeyEvent.VK_I;
		NoteToKey[55] = KeyEvent.VK_O;
		NoteToKey[57] = KeyEvent.VK_P;
		NoteToKey[59] = KeyEvent.VK_A;
		NoteToKey[60] = KeyEvent.VK_S;
		NoteToKey[62] = KeyEvent.VK_D;
		NoteToKey[64] = KeyEvent.VK_F;
		NoteToKey[65] = KeyEvent.VK_G;
		NoteToKey[67] = KeyEvent.VK_H;
		NoteToKey[69] = KeyEvent.VK_J;
		NoteToKey[71] = KeyEvent.VK_K;
		NoteToKey[72] = KeyEvent.VK_L;
		NoteToKey[74] = KeyEvent.VK_Z;
		NoteToKey[76] = KeyEvent.VK_X;
		NoteToKey[77] = KeyEvent.VK_C;
		NoteToKey[79] = KeyEvent.VK_V;
		NoteToKey[81] = KeyEvent.VK_B;
		NoteToKey[83] = KeyEvent.VK_N;
		
		NoteToKeyShift[25] = true;
		NoteToKeyShift[27] = true;
		NoteToKeyShift[30] = true;
		NoteToKeyShift[32] = true;
		NoteToKeyShift[34] = true;
		NoteToKeyShift[37] = true;
		NoteToKeyShift[39] = true;
		NoteToKeyShift[42] = true;
		NoteToKeyShift[44] = true;
		NoteToKeyShift[46] = true;
		NoteToKeyShift[49] = true;
		NoteToKeyShift[51] = true;
		NoteToKeyShift[54] = true;
		NoteToKeyShift[56] = true;
		NoteToKeyShift[58] = true;
		NoteToKeyShift[61] = true;
		NoteToKeyShift[63] = true;
		NoteToKeyShift[66] = true;
		NoteToKeyShift[68] = true;
		NoteToKeyShift[70] = true;
		NoteToKeyShift[73] = true;
		NoteToKeyShift[75] = true;
		NoteToKeyShift[78] = true;
		NoteToKeyShift[80] = true;
		NoteToKeyShift[82] = true;
	}
	
	public static void main(String[] args) throws Exception{		
		System.out.println("This program will press and release keys of your keyboard");
		System.out.println("There is no key to stop playing the file");
		System.out.println("if you want to stop playing the file, close this window");
		System.out.println("USE THIS PROGRAM AT YOUR OWN RISKS");
		System.out.println("----------------------------------------");
		System.out.println("Write here the file you want to play :");
		
		Scanner Scanner = new Scanner(System.in);
		
		try{
			Robot = new Robot();
		}
		catch(AWTException e){
			e.printStackTrace();
			System.exit(0);
		}
		
		createNoteToKey();
		
		InputStream InputStream = null;
		try{
		InputStream = new BufferedInputStream(
				new FileInputStream(Scanner.nextLine()));
		}
		catch(FileNotFoundException e){
			System.out.println("File not found !");
			System.exit(0);
		}
		
		Sequence Sequence = MidiSystem.getSequence(InputStream);
		Sequencer = MidiSystem.getSequencer();
		Sequencer.open();
		
		MetaEventListener Mel = new MetaEventListener(){
			@Override
			public void meta(MetaMessage Meta){
				final int Type = Meta.getType();
				if(Type == TrackManager.MetaEventOffset || Type == TrackManager.MetaEventOffset + 1){
					final byte[] Data = Meta.getData();
					if(Data.length == 3){
						final int Channel = Data[0]&0x0F;
						final int Note = Data[1];
						final int Velocity = Data[2];
						System.out.println(Note);
						int Key = NoteToKey[Note];
						boolean Shift = false;
						if(NoteToKeyShift[Note]){
							Key = NoteToKey[Note-1];
							Shift = true;
						}
						if(Key != 0){
							if(Type == TrackManager.MetaEventOffset && Velocity >= 5){
								if(Shift){Robot.keyPress(KeyEvent.VK_SHIFT);}
								Robot.keyPress(Key);
								if(Shift){Robot.keyRelease(KeyEvent.VK_SHIFT);}
							}
							else{
								Robot.keyRelease(Key);
							}
						}
					}
				}
			}
		};
		Sequencer.addMetaEventListener(Mel);
		
		Track[] Tracks = Sequence.getTracks();
		Track NewTrack = Sequence.createTrack();
		for(Track Track : Tracks){
			TrackManager.addNotesToTrack(Track, NewTrack);
		}
		
		Sequencer.setSequence(Sequence);
		if(Sequencer instanceof Synthesizer){
			Synthesizer Synthesizer = (Synthesizer) Sequencer;
		}
		else{
			System.out.println("Sequencer isn't instanceof Synthesizer");
			//System.exit(1);
		}
		
		Sequencer.start();
		
	}
	
}
