package ttpservice;
import java.io.Serializable;

/*
 * TTP layer  header and data which goes into the UDP layer payload
 * padding 
 * */
@SuppressWarnings("serial")
public class TTP implements Serializable{

	private int SYN;//sequence number

	private int ACK;
	private Object data;//
	private int offset;//for fragment in unit of 8
	private char ID;
	private short length;// for each fragmented message 
	private char flag;//indicate whether has more fragment
	
	private char category;
	
	public TTP() {
		offset = 0;
		ID = 0;
		flag = 0;
	}

	public TTP(int ACK, int SYN, Object data, short length, char category){
		this.ACK = ACK;
		this.length = length;
		this.SYN = SYN;
		this.data = data;
		
		this.category = category;
		
		offset = 0;
		ID = 0;
		flag = 0;
	}
	
	public int getSYN(){
		return SYN;
	}
	
	public void setSYN(int syn) {
		this.SYN = syn;
	}
	
	public int getACK() {
		return ACK;
	}
	
	public void setACK(int ack) {
		this.ACK = ack;
	}
	
	public Object getData() {
		return data;
	}
	
	public void setData(Object data) {
		this.data = data;
	}
	
	public int getOffset() {
		return offset;
	}
	
	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	public char getID() {
		return ID;
	}
	
	public void setID(char ID) {
		this.ID = ID;
	}
	
	public char getFlag() {
		return flag;
	}
	
	public void setFlag(char flag) {
		this.flag = flag;
	}
	
	public short getCheckSum(){
		//using int to prevent overflow, will get the lower 16 bits
		int checkSum = 0; 
		checkSum += (short) getSYN() + (short)(getSYN() >> 16);
		checkSum += (short) getACK() + (short)(getACK() >> 16);
		checkSum += (short) getOffset();
		checkSum += (short) getID();
		checkSum += (short) getFlag();
		return (short)checkSum;	
	}

	public short getLength() {
		return length;
	}

	public void setLength(short length) {
		this.length = length;
	}
	
	public char getCategory() {
		return category;
	}
	
}
