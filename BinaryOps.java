package huffmanProjectREAL;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/*
Author: Richard Liao
CMPT 435: Algorithms Analysis and Design
 */
public class BinaryOps {
	private static BufferedOutputStream out = new BufferedOutputStream(System.out);
	private static int currByte;// current byte to write out
	private static int r;//remaining bits in current byte
	public BinaryOps(String s) {
		try {
			FileOutputStream file =new FileOutputStream(s);
			out = new BufferedOutputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	//writeBit adds either true(1) or false(0) to the buffer. When it is full (8bits) write it out to file
	public static void writeBit(boolean bit){
		// add bit to byte
		currByte <<= 1;
		if (bit){ 
			currByte |= 1;
		}
		// if byte is full (8 bits), write out as a single byte
		r++;
		if (r == 8) {
			writeOut();
		}
	} 
	//Writes the byte.
	public static void writeOut(){
		//If remaining bits !=0 left shift until byte is full
		if (r > 0){
			currByte <<= (8 - r);	
		}
		try {
			out.write(currByte); 	
		}catch (IOException e){ 
			e.printStackTrace(); 
		}
		r = 0;//reset current byte
		currByte = 0;
	}
	//Write Out current Byte and flush
	public static void flush() {
		writeOut();
		try { 
			out.flush();
		}catch (IOException e){ 
			e.printStackTrace();
		}
	}
	// Write out, flush, then close BufferedOutPutStream 
	public void close() {
		flush();
		try {
			out.close();
		}catch (IOException e){
			e.printStackTrace(); 
		}
	}
	//Write an int as a while byte
	public static void writeByte(int x){
	//loop through 8 bits.
		for(int i =0;i<8;i++){
			int y = x >>> (7-i)&1;//If x right shift (7-i)&1 is 1, write true, else right false.
			if(y==1){
				boolean bit=true;
				writeBit(bit);
			}else{
				boolean bit=false;
				writeBit(bit);
			}

		}
	}
	//Write out a string.
	public void writeStr(String x) {
		for (int i = 0; i < x.length(); i++){//loop through string and writeChar for each char
			writeChar(x.charAt(i));
		}
	}
	//Write out a character.
	public void writeChar(char x) {
		writeByte(x);
	}

}
