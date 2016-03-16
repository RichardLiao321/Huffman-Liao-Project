package huffmanProjectREAL;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Map.Entry;


public class Decode extends Encode{
	/*
	Author: Richard Liao
	CMPT 435: Algorithms Analysis and Design
	 */
	public static void main(String[] args) throws IOException{
		long startTime = System.currentTimeMillis();
		String source=args[0];
		String target=args[1];
		BufferedInputStream bi = new BufferedInputStream(new FileInputStream(new File(source)));
		HashMap<Character, String> canonTable = canonicalCodes(decodeHeader(bi));//Reuse CanonicalCodes from Encode to get the canonical code Table
		//System.out.println(canonTable);
		
		System.out.println("Canonical Table Done:");
		Node root = new Node('-',-1,"",null,null);
		//String s="";
		PriorityQueue<Node> d= new PriorityQueue<Node>();
		buildPQ(null,canonTable,d);
		//ArrayList<Node> hold = new ArrayLiss
		//System.out.println(isCode(codeListFilter,"00000"));
		
		//convert hashmap into an Array list for easy iteration
		ArrayList<Node> nodeArray= buildNodeArray(canonTable);
		//build the canonical tree from the root
		buildCanonicalTree(nodeArray,root);
		System.out.println("Tree Built");
		expand(bi,root,target);
		System.out.println("Expand Done.");
		
		bi.close();
		///*
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Program Run Time: "+totalTime);
		//*/
	}
	//Reads from file, calculates reads the header generates canonical codes.
	public static HashMap<Character, String> decodeHeader(BufferedInputStream bi) throws IOException{
		//Build a HashMap with <Code Char, Code(with appropriate length)>
		HashMap<Character, String> canonTable = new HashMap<Character, String>();
			int headerLength = (bi.read() * 2)+1; //read in first byte, header length is (2*first byte) +1
			System.out.println("Header Length:  " + headerLength );
			for(int i = 0; i < headerLength-1; i = i + 2) { // Loops over the header and pulls out the bytes
				char nodeChar = (char)bi.read();
				int codeLength=bi.read();
				String codePlaceHold= String.format("%0"+codeLength+"d", 0);//Make a placeholder code word with equal length as the real Code
				//System.out.println("Char: " + nodeChar + " Code Length: " + codeLength+" Code Place Holder: "+codePlaceHold);
				canonTable.put(nodeChar, codePlaceHold); //add to Hashmap < Byte character, String with "0"= to Next byte>
			}
			return canonTable;
		
	}
	///*
	//Take in an array list of all Nodes and root Node and build a tree
	public static void buildCanonicalTree(ArrayList<Node> y, Node n) {
		//ArrayList<String> canonList=p;
		String s =n.code;
		//System.out.println(s);
		if(!isNode(y,s)){//First check to see if root of (sub)tree is a node in y
			if(isNode(y,s+'0')){//If the node to the left is a tree
				//System.out.println("Left Node Found at level: "+(x.code.length()+1));
				//Find the associated Node
				for(int i=0;i<y.size();i++){
					if(y.get(i).code.equals(s+"0")){
					n.left=y.get(i);
					//y.get(i).printCanon();
					}
				}
				
			}else{
				//if the node Left is not found, filter Array List down and build another Node to the left.
				buildCanonicalTree(filterNodes(y,s+"0"),buildLeft(n,s));
			}
			//Same as other if, but for right
			if(isNode(y,s+'1')){
				//System.out.println("Right Node Found at level: "+(x.code.length()+1));
				for(int i=0;i<y.size();i++){
					if(y.get(i).code.equals(s+"1")){
						n.right=y.get(i);
						//y.get(i).printCanon();
					}			
				}
				
			}else{
				buildCanonicalTree(filterNodes(y,s+"1"),buildRight(n,s));

			}
		}else{
			System.out.println("SHOULDNT RUN. WHAT HAPPENED?");
			n.printCanon();
			return ;
		}
	}
	//converts Hashmap into an ArrayList of Nodes. Similar code from encode.
	public static ArrayList<Node> buildNodeArray(HashMap<Character,String> canonical){
		ArrayList<Node> ht = new ArrayList<Node>();
		Iterator<Entry<Character, String>> it = canonical.entrySet().iterator();
		   while (it.hasNext()) {
		     Map.Entry<Character, String> pair = (Map.Entry<Character, String>)it.next();
		     Node n=new Node(pair.getKey(),-1,pair.getValue(),null,null);
		     ht.add(n);
		   }
		   return ht;
	}
	//checks Array list<Node> for the whole String s, if true, then a Node is found
	public static boolean isNode(ArrayList<Node> y, String s){
		for(int i=0;i<y.size();i++){
			if(y.get(i).code.equals(s)){
				//y.get(i).printCanon();
				return true;
			}			
		}
		return false;
	}
	//Makes a node to the left of root, 
	public static Node buildLeft(Node root, String x){
		Node left = new Node('/',-1,root.code+'0',null,null);
		root.left=left;
		return left;
	}
	//Makes a node to the right of root
	public static Node buildRight(Node root, String x){
		Node right = new Node('/',-1,root.code+'1',null,null);
		root.right=right;
		return right;
	}
	//*/
	//Filters Arraylist<Node> down by code
	public static ArrayList<Node> filterNodes(ArrayList<Node> x, String sCode){
		ArrayList<Node> y= new ArrayList<Node>();
		int i=0;
		//Loop through ArrayList and add Nodes that BEGIN with sCode to another ArrayList
		while(i<x.size()){
			if(x.get(i).code.startsWith(sCode)){//If the code starts with sCode, then add it to y
				//x.get(i).printCanon();
				y.add(x.get(i));
			}
			i++;
		}
		//System.out.println("FILTERED LIST ");
		/*
		for(Node n:y){
			n.printCanon();
		}
		*/
		//y will only contain Nodes.Codes that start with sCode
		return y;
		
	}
	//read in a byte
	//keep current Node
		//until(node Is Leaf)
		//left if bit=0
		//right if bit=1
	//if node is leaf
		//write out the character
	//read until EOF char
	//Reads in Encoded File and decodes it while writing out found characters to file.
	//EXPAND TAKES TOO LONG
	public static void expand(BufferedInputStream bi, Node root,String target) throws IOException{
		BinaryOps bo=new BinaryOps(target);
		long x = System.currentTimeMillis();
		Node currentNode=root;
		char eof='\u0000';
		while(!(currentNode.ch==(eof))){//Continue to read int bytes until the currentNode is EOF
			int cbyte = bi.read();
			//System.out.println(cbyte);
			//System.out.println(Integer.toBinaryString(cbyte));
			for(int i=0;i<8;i++){//loop through byte, check each bit.
				//currentNode.printCanon();
				int y = cbyte >> (7-i)&1;
				if(y==1){//If the bit is 1
					if(!currentNode.isLeaf()){//if the current Node is not a leaf, set the currentNode to the right
						currentNode=currentNode.right;
					}else if(currentNode.ch==eof){//If EOF is hit, end computation
							System.out.println("EOF");
							break;
					}else if(currentNode.isLeaf()){// if Node is a leaf, write out the char to file.
							//System.out.println(currentNode.ch);
							bo.writeChar(currentNode.ch);
							if(y==1){//Set the current Node to root's left or right based off next bit
								currentNode=root.right;
							}else{
								currentNode=root.left;
							}
					}
				}
				//Same as above code, but with left.
				if(y==0){
					if(!currentNode.isLeaf()){
					currentNode=currentNode.left;
					}else if(currentNode.ch==eof){
						System.out.println("EOF");
						break;
					}else if(currentNode.isLeaf()){
						//System.out.println(currentNode.ch);
						bo.writeChar(currentNode.ch);
						if(y==1){
							currentNode=root.right;
						}else{
							currentNode=root.left;
						}
					}
				}
				
				
			}
			//currentNode.printCanon();
			//wee++;
		}
		bo.close();
		long y   = System.currentTimeMillis();
		long totalt = y - x;
		System.out.println("Expand takes: "+totalt);
	}
	
}
