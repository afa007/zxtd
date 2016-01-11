/*
 * Created on 2005-5-7
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.cmpp.pdu;

import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.Gson;
import com.cmpp.util.CmppConstant;
import com.cmpp.sms.ByteBuffer;
import com.cmpp.sms.NotEnoughDataInByteBufferException;
import com.cmpp.sms.PDUException;

/**
 * @author lucien
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class Connect extends Request {

	private String clientId = "";
	private byte[] authClient = new byte[16];
	private byte version = (byte) 0x00;
	private String sharedSecret = "";

	public Connect() {
		super(CmppConstant.CMD_CONNECT);
	}

	public Connect(byte version) {
		super(CmppConstant.CMD_CONNECT);
		setVersion(version);
	}

	public void setBody(ByteBuffer buffer) throws PDUException {
		try {
			setClientId(buffer.removeStringEx(6));
			setAuthClient(buffer.removeBytes(16).getBuffer());
			setVersion(buffer.removeByte());
			setTimeStamp(buffer.removeInt());
		} catch (NotEnoughDataInByteBufferException e) {
			e.printStackTrace();
			throw new PDUException(e);
		}
	}

	public ByteBuffer getBody() {
		ByteBuffer buffer = new ByteBuffer();
		buffer.appendString(getClientId(), 6);
		buffer.appendBytes(getAuthClient(), 16);
		buffer.appendByte(getVersion());
		buffer.appendInt(getTimeStamp());
		return buffer;
	}

	public byte[] genAuthClient() {
		/*
		byte[] result = new byte[16];
		try {
			ByteBuffer buffer = new ByteBuffer();
			buffer.appendString(clientId, clientId.length());
			
			byte[] ba = new byte[9];
			for(int i = 0; i< 9; i++){
				ba[i] = 0;
			}
			buffer.appendBytes(ba);
			
			buffer.appendString(sharedSecret, sharedSecret.length());

			Date date = new Date();
			Format formatter = new SimpleDateFormat("MMddHHmmss");
			
			String stimeStamp = formatter.format(date);
			logger.error("stimeStamp: " +stimeStamp);
			
			buffer.appendInt(Integer.valueOf(stimeStamp));
			
			MessageDigest md = MessageDigest.getInstance("MD5");
			result = md.digest(buffer.getBuffer());
		} catch (Exception ex) {
			logger.error("Failed genAuthClient!");
		}
		return result;
		*/
		SimpleDateFormat formatter = new SimpleDateFormat("MMddHHmmss");
        String time = formatter.format(new Date());
        this.setTimeStamp(Integer.parseInt(time));
        try {
			authClient = MessageDigest.getInstance("MD5").digest(
			        (clientId + "\0\0\0\0\0\0\0\0\0" + sharedSecret + time).getBytes());
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return authClient;
	}
	
	public static byte[] intToByteArray(int a) {  
	    return new byte[] {  
	        (byte) ((a >> 24) & 0xFF),  
	        (byte) ((a >> 16) & 0xFF),     
	        (byte) ((a >> 8) & 0xFF),     
	        (byte) (a & 0xFF)  
	    };  
	}

	public static String getHexDump(byte[] data) {
		String dump = "";
		try {
			int dataLen = data.length;
			for (int i = 0; i < dataLen; i++) {
				dump += Character.forDigit((data[i] >> 4) & 0x0f, 16);
				dump += Character.forDigit(data[i] & 0x0f, 16);
			}
		} catch (Throwable t) {
			// catch everything as this is for debug
			dump = "Throwable caught when dumping = " + t;
		}
		return dump;
	}

	public String byteBufferToString() {
		String result = "";
		StringBuffer stringBuffer = new StringBuffer();

		byte[] byteArray = this.getData().getBuffer();
		byte[] tmp = new byte[4];
		for (int i = 0; i < 4; i++) {
			tmp[i] = byteArray[i];
		}
		stringBuffer.append(String.format("%04d", byteArrayToInt(tmp)));

		for (int i = 0; i < 4; i++) {
			tmp[i] = byteArray[4 + i];
		}
		stringBuffer.append(String.format("%04d", byteArrayToInt(tmp)));

		for (int i = 0; i < 4; i++) {
			tmp[i] = byteArray[8 + i];
		}
		stringBuffer.append(String.format("%04d", byteArrayToInt(tmp)));

		result = stringBuffer.toString();
		ByteBuffer bf = this.getBody();
		try {
			result = result.concat(bf.removeStringEx(6));
			result = result.concat(bf.removeStringEx(16));

			byte ver = bf.removeByte();
			int high = ver & 0xF0;
			int low = ver & 0x0F;
			
			result = result.concat(String.format("%d", high));
			result = result.concat(String.format("%d", low));
			
			result = result.concat(String.format("%d", bf.removeInt()));
			
		} catch (NotEnoughDataInByteBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		return result;
	}

	public static int byteArrayToInt(byte[] bytes) {
		int value = 0;
		// 由高位到低位
		for (int i = 0; i < 4; i++) {
			int shift = (4 - 1 - i) * 8;
			value += (bytes[i] & 0x000000FF) << shift;// 往高位游
		}
		return value;
	}

	public int genTimeStamp() {
		Date date = new Date();
		Format formatter = new SimpleDateFormat("MMddHHmmss");
		int timeStamp = Integer.valueOf(formatter.format(date));
		return timeStamp;
	}

	public byte[] getAuthClient() {
		return authClient;
	}

	public void setAuthClient(byte[] authClient) {
		this.authClient = authClient;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public int getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(int timeStamp) {
		this.timeStamp = timeStamp;
	}

	public byte getVersion() {
		return version;
	}

	public void setVersion(byte version) {
		this.version = version;
	}

	public String getSharedSecret() {
		return sharedSecret;
	}

	public void setSharedSecret(String sharedSecret) {
		this.sharedSecret = sharedSecret;
	}

	protected Response createResponse() {
		return new ConnectResp();
	}

	public boolean isTransmitter() {
		return (version == (byte) 0x18);
	}

	public boolean isReceiver() {
		return (version == (byte) 0x01);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cmpp.sms.ByteData#setData(cmpp.sms.util.ByteBuffer)
	 */
	public void setData(ByteBuffer buffer) throws PDUException {
		header.setData(buffer);
		setBody(buffer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cmpp.sms.ByteData#getData()
	 */
	public ByteBuffer getData() {
		ByteBuffer bodyBuf = getBody();
		header.setCommandLength(CmppConstant.PDU_HEADER_SIZE + bodyBuf.length());
		ByteBuffer buffer = header.getData();
		buffer.appendBuffer(bodyBuf);
		return buffer;
	}

	public String name() {
		return "CMPP Connect";
	}

	// private void writeObject(java.io.ObjectOutputStream out) throws
	// IOException {
	// out.write(this.getData().getBuffer());
	// }
	//
	// private void readObject(java.io.ObjectInputStream in) throws IOException,
	// ClassNotFoundException {
	// int length = in.readInt();
	// byte[] bytemsg = new byte[length - 4];
	// byte[] bytes = new byte[length];
	// in.read(bytemsg);
	// System.arraycopy(bytes, 0, length, 0, 4);
	// System.arraycopy(bytes, 4, bytemsg, 0, bytemsg.length);
	// try {
	// this.setData(new ByteBuffer(bytes));
	// } catch (PDUException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }

	public String dump() {
		Gson gson = new Gson();
		return gson.toJson(this, this.getClass());
	}

	@Override
	public ConnectResp getResponse() {
		ConnectResp conResp = (ConnectResp) super.getResponse();
		conResp.setCommandId(CmppConstant.CMD_CONNECT_RESP);
		return conResp;
	}
}
