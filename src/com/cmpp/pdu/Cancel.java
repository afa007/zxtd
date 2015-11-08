package   com.cmpp.pdu;

import com.google.gson.Gson;
import   com.cmpp.sms.ByteBuffer;
import   com.cmpp.sms.NotEnoughDataInByteBufferException;
import   com.cmpp.sms.PDUException;
import com.cmpp.util.CmppConstant;

public class Cancel extends Request {

	private String msgId = "";

	protected Response createResponse() {
		return new CancelResp();
	}

	public ByteBuffer getData() {
		ByteBuffer bodyBuf = getBody();
		header.setCommandLength(CmppConstant.PDU_HEADER_SIZE + bodyBuf.length());
		ByteBuffer buffer = header.getData();
		buffer.appendBuffer(bodyBuf);
		return buffer;
	}

	public void setData(ByteBuffer buffer) throws PDUException {
		header.setData(buffer);
		setBody(buffer);
	}

	public ByteBuffer getBody() {
		ByteBuffer buffer = new ByteBuffer();
		buffer.appendString(msgId, 8);
		return buffer;
	}
	
	public void setBody(ByteBuffer buffer) 
	throws PDUException {
		try {
			msgId = buffer.removeStringEx(8);
		} catch (NotEnoughDataInByteBufferException e) {
			e.printStackTrace();
			throw new PDUException(e);
		}
	}
	
	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}
	
	public String name() {
		return "CMPP Cancel";
	}
	

    public String dump() {
        Gson gson = new Gson();
        return gson.toJson(this, this.getClass());
    }
}
