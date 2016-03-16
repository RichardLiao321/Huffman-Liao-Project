package huffmanProjectREAL;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.io.FileInputStream;

public class Encode{
	/*
	Author: Richard Liao
	CMPT 435: Algorithms Analysis and Design
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException{
		String source=args[0];
		String target=args[1];
		compress(source, target);
	}
	//calls methods needed to read from file, create PQ<Node>, Normal codes, Canonical Codes, Writes Header and body and eof out 
	//*/
	public static void compress(String source, String target) throws FileNotFoundException, IOException {
		
		long startTime = System.currentTimeMillis();
		//IMPLEMENT READING FROM FILE
		String en = readInFile(source);
		System.out.println("File Read");
		//split string into array of strings
		char[] inputArray=inputSplit(en);
		System.out.println("Input Split");
		//Build normal Huffman
		Node x=buildTree(checkFreq(en));//Root node of regular Huffman
		System.out.println("Frequencies Calculated");
		String s = "";
		HashMap<Character, String> codeTable= new HashMap<Character, String>();
		buildCode(codeTable, x, s);
		System.out.println("Huffman Codes Made");
		//System.out.println("Huffman Codes\n"+codeTable);
		
		//Build Canonical Code words
		HashMap<Character, String> canonTable= canonicalCodes(codeTable);
		//System.out.println("\nCanonical codes \n"+canonTable);
		//write the encoded data to file
		//String xfv="1234";
		//System.out.println(xfv.length());
		
		writeToFile(canonTable, inputArray, inputArray.length,target);
		System.out.println("Encoded");
		///*
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println(totalTime);
		//*/
	}
	//Reads in file and returns it as string
	public static String readInFile(String source) throws IOException{
		InputStream in = new FileInputStream(new File(source));
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder out = new StringBuilder();
		int i;
		while ((i=reader.read()) != -1) {
			out.append((char)i);
		}
		reader.close();
		return out.toString();
	}
	//Writes out to file. Takes the canonical code table.
	@SuppressWarnings("static-access")
	public static void writeToFile(HashMap<Character, String> canonTable,char[] inputArray, int strLength, String target){
	BinaryOps bo=new BinaryOps(target);
	//String out="";//out is for testing.
	//HEADER STUFF-----------------------------------------
	//first 8 bits: num of chars
	int size=canonTable.size();
	bo.writeByte(size);
	//Each char gets 8 bits and its code length gets 8bits
	Iterator<Entry<Character, String>> it = canonTable.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Character, String> pair = (Map.Entry<Character, String>)it.next();
			//System.out.println("KEY: "+pair.getKey() +" CODELEN: "+pair.getValue().length());
			bo.writeChar(pair.getKey());
			bo.writeByte(pair.getValue().length());
		}
	
	//BODY STUFF-------------------------------------------
	//loop through input string
	for(int i=0;i<strLength;i++){
		char inChar=inputArray[i];
		String code = canonTable.get(inChar);//look up code for character at the index
		for(int j=0; j<code.length();j++){//Loop through selected code word. Write bits as true or false depending on char.
			if(code.charAt(j)=='1'){
				bo.writeBit(true);
			}else if(code.charAt(j)=='0'){
				bo.writeBit(false);
			}else{
				System.out.println("WHAT DID YOU DO");
			}
		}
		//out=out+code;
	}
	
	//EOF STUFF------------------------------------------
	// Write out the EOF to the end of the file. 
	char stringeof = '\u0000';
	String eofadd = canonTable.get(stringeof);
	
	for(int j=0; j<eofadd.length();j++){
		if(eofadd.charAt(j)=='1'){
			bo.writeBit(true);
		}else if(eofadd.charAt(j)=='0'){
			bo.writeBit(false);
		}
		
	}
	//out=out+eofadd;
	//System.out.println("\nOutput Binary: \n"+out);
	bo.close();
	
	}
	/*
	 Assign first character code of binary zero
	For each next char, increment the binary val of code by 1
	If next char has a shorter length
		Increment 1st 
		THEN shorten code to correct length 
	 */
	//Generates canonical codes from regular code table. Uses BinaryOps for bit shifting.
	protected static  HashMap<Character, String> canonicalCodes(HashMap<Character, String> codeTable) {
		PriorityQueue<Node> cct = new PriorityQueue<Node>();
		HashMap<Character, String> cct2 = new HashMap<Character, String>();
		buildPQ(null, codeTable,cct);

		int cCode=-1;//start at -1 so first increment puts cCode at 0
		int prevLen=0;
		for (int i=0; i<codeTable.size();i++) {//Loop through all of CodeTable
			//Node with longest codes. Equal code lengths sorted by alphabet.
			Node a = cct.poll();//pop out Node with longest code/first in alphabet
			cCode++;//Increment
			if(cCode==0){//Run once at start to set previous length
				prevLen=a.code.length();
			}
				//a.printCanon();//Node with longest code/alphabetical first
			int len=a.code.length();
			if(len<prevLen){//shift if len is shorter
				//System.out.print(cCode); //view shift
				int shift = prevLen - len; // calculate the shift
				cCode = cCode >> shift; // right shift the difference
				//System.out.println("->"+cCode);
				prevLen=a.code.length();//set new length of comparison
			}
			String bincCode = Integer.toBinaryString(cCode);//convert code to binary
			int diff=prevLen-bincCode.length();//Number of bits needed to pad is prev max len - bincCode
			if(diff>0){//Only pad if there is a diff
				String x=String.format("%0"+diff+"d",0);
				String finalCode= x+bincCode;
				a.code=finalCode;
			}else{
				a.code=bincCode;
			}
			//put the char and code in hashmap
			cct2.put(a.ch,a.code);
		}
		return cct2;
	}
	//Small func to divide input string into array to tablulate freq
	public static char[] inputSplit(String inputString){
		char[] strArray = inputString.toCharArray();
		return strArray;
	}
	//takes string array returns char and freq
	public static HashMap<Character, Integer> checkFreq(String strArray){
		HashMap<Character, Integer> hashmap = new HashMap<Character, Integer>();
		//checking each char of strArray
		for (int i=1;i<strArray.length();i++){
			Character chars='\u0000';
			chars=strArray.charAt(i);
			if(hashmap.containsKey(chars)){
				//if array has char increment count
				hashmap.put(chars, hashmap.get(chars)+1);
			}else{
				//else add char and set value to 1
				hashmap.put(chars, 1);
			}
			
		}
		return hashmap;
	}
	//buildPQ: build a priority queue out of a hashmap
	//Builds PQ from Hashmap
	 public static PriorityQueue<Node> buildPQ(HashMap<Character, Integer> hashmap,HashMap<Character, String> canonical,PriorityQueue<Node> ht){
		if(hashmap!=null){//if codes is provided, Regular Huffman PQ
			Iterator<Entry<Character, Integer>> it = hashmap.entrySet().iterator();
			char stringeof = '\u0000';
			ht.offer(new Node(stringeof,1,"",null,null));//ADD EOF NODE
			while (it.hasNext()) {//iterate hashmap offer node from each pair
				Map.Entry<Character, Integer> pair = (Map.Entry<Character, Integer>)it.next();
				Node n=new Node(pair.getKey(),pair.getValue(),"",null,null);
				ht.offer(n);
		}
		}else if(canonical!=null){//If Canonical is provided, generate Canonical PQ
			Iterator<Entry<Character, String>> it = canonical.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry<Character, String> pair = (Map.Entry<Character, String>)it.next();
					Node n=new Node(pair.getKey(),-1,pair.getValue(),null,null);
					ht.offer(n);
				}
		}
		return ht;
	}
	//Builds a regular huffman tree.
	public static Node buildTree(HashMap<Character, Integer> hashmap){
		PriorityQueue<Node> ht = new PriorityQueue<Node>();
		buildPQ(hashmap,null,ht);
		while (ht.size() > 1) {
			// two Node with least frequency
			Node a = ht.poll();
			Node b = ht.poll();
			//a.print();
			//b.print();
			//Create new NodeBranch of nodes w/ least freq
			ht.offer(new Node('\u0000',a.freq+b.freq,"",a, b));
		 }
		 //System.out.println(ht.peek().print());
		 return ht.poll();
	}
	//Build a hashmap of the codes and characters. Traverse the tree and build codes 
	public static HashMap<Character, String> buildCode(HashMap<Character, String> st, Node x, String s) {
		if (!x.isLeaf()) {//if x is not a leaf, recursively build left and right until leaf is found.
			buildCode(st, x.left,  s + '0');
			buildCode(st, x.right, s + '1');
		}else {
			st.put(x.ch,s);//when a leaf is reached, add the character and the code to PQ
		}
	return st;
	}


	
}
