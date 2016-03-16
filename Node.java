package huffmanProjectREAL;
/*
Author: Richard Liao
CMPT 435: Algorithms Analysis and Design
 */
public class Node implements Comparable<Node>{
	public char ch;
	public int freq=-1;
	public Node left;
	public Node right;
	public String code;
	//PRIMARY CONSTRUCTOR
	Node(char ch, int freq, String code, Node l, Node r) {
		this.ch = ch;
		this.freq = freq;
		this.code=code;
		this.left=l;
		this.right=r;
	}
	//checks to see if Node is a leaf
	public boolean isLeaf() {
		if(this.left==null && this.right==null){
			return true;
		}else
			return false;
	}
	//Prints information on Normal Nodes
	public char print(){
		System.out.print("{"+this.ch+","+this.freq+"}");
		return ch;
	}
	//Prints info on Canonical 
	public char printCanon(){
		System.out.print("{"+this.ch+","+this.code+"}");
		return ch;

	}
	// compare, based on frequency,THEN code length, THEN alphabetically, else alphabetically
	public int compareTo(Node that) {
		///*
		if(this.freq!=-1&&that.freq!=-1){
			return this.freq - that.freq;
		}else if(freq==-1 && that.freq==-1&&this.code.length()!=that.code.length()){
			return that.code.length()-this.code.length();
		}else if(this.code.length()==that.code.length()&&this.code!=null&&that.code!=null){
			
			return Character.valueOf(this.ch).compareTo(Character.valueOf(that.ch));
		}else{
			return Character.valueOf(this.ch).compareTo(Character.valueOf(that.ch));
			
		}
		//*/
	}
}



//if(this.code.length()==that.code.length()&&this.code!=null&&that.code!=null){
//    return this.ch.compareTo(that.ch);
//}

