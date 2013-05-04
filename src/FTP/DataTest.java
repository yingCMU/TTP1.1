package FTP;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;
import java.util.LinkedList;

public class DataTest {
	static short MSS = 10;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		byte[] a = new byte[10];
		bufferRecv(new LinkedList(),6);
		try {
			reassembleSend("This is a String ~ GoGoGo");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	
	
	
	public static  int reassembleSend(Object data ) throws IOException{
		
		byte[] a = new byte[1];//java.lang.String
		short len = 0;
		short rem;
		InputStream in =null;
	    if(data.getClass().isInstance(a)){
	    	
	    	len = (short) ((byte [])data).length;
	    	System.out.println("binary data, length >"+len);
	    	in = new ByteArrayInputStream((byte[]) data);
	    }
	    else if(data.getClass().isInstance("a")){
	    	len = (short) ((String) data).length();
	    	System.out.println("String data ,length >"+len);
	    	System.out.println("data "+data);
	    	in = StringToInputStream((String)data);
	    }
	    else 
	    	System.out.println("invalid data type");
	    rem = len;
	    short nsend = 0;
	    short tosend =0;
	    int off = 0;
	    byte[] buffer = null ;
	    while(rem > 0 ){
	    	
	    	if (rem >= MSS)
	    		tosend = MSS;
	    	else 
	    		tosend = rem;
	    	buffer = new byte[MSS];
	    if ((nsend = (short) in.read(buffer , 0, tosend)) < 0) {
				
	    	   
	    		 return -1;      /* error caused by send() */ 
	    	} 
			
	    	else if (nsend == 0)
	    	    break;              /* EOF */
	    	rem -= nsend;
	    	
	    	//sendData(buffer,tosend);
	        }
	    String str = new String(buffer, "UTF8");
    	System.out.println("nsend "+ nsend+"> byte: "+str);
	        return (len - rem);   
	    }
	
	
	public static void bufferRecv(LinkedList buffer, int length){
		
		
		buffer.add("1234".getBytes());
		buffer.add("23".getBytes());
		
		Iterator ite =buffer.iterator();
		//int MAX = 15 * 10 ; 
		//StringBuffer result = new StringBuffer();
	    byte [] result = new byte[length];
		try{
			int offset = 0;
		while(ite.hasNext()){
			
			byte [] element = (byte[]) ite.next();
			int index = 0;
			while(index < element.length){
				result [index+offset]=element[index];
				index ++;
			}
			offset += element.length;
		}
			System.out.println("element "+new String(result));
	        
	        
		}
		
		// Charset charset = Charset.defaultCharset();  
	      //  CharsetDecoder decoder = charset.newDecoder();  
	      //  CharBuffer charBuffer = result.asCharBuffer();
	
		catch(Exception e){
			e.printStackTrace();
		}
		//System.out.println("charBuffer "+ new String(result.array()));
		//OutputStream stream = new FileOutputStream("c:\\\\output-text.txt");
		//WritableByteChannel channel = Channels.newChannel(stream);

		  // channel.write(result);
		
		 return ;
		
	}
	
private static InputStream StringToInputStream (String str ){
    

 
	// convert String into InputStream
	InputStream is = new ByteArrayInputStream(str.getBytes());
	
 
	// read it with BufferedReader
	BufferedReader br = new BufferedReader(new InputStreamReader(is));
 
	String line;
	
 
return is;
   }
}


