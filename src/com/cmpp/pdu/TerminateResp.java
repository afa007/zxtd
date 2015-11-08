/*
 * Created on 2005-5-7
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package   com.cmpp.pdu;

import com.google.gson.Gson;
import com.cmpp.util.CmppConstant;
import   com.cmpp.sms.ByteBuffer;
import   com.cmpp.sms.PDUException;

/**
 * @author lucien
 *
 *         TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style -
 *         Code Templates
 */
public class TerminateResp extends Response {

    public TerminateResp() {
        super(CmppConstant.CMD_TERMINATE_RESP);
    }

    /*
     * (non-Javadoc)
     * 
     * @see cmpp.sms.ByteData#setData(cmpp.sms.util.ByteBuffer)
     */
    public void setData(ByteBuffer buffer) throws PDUException {
        header.setData(buffer);

    }

    /*
     * (non-Javadoc)
     * 
     * @see cmpp.sms.ByteData#getData()
     */
    public ByteBuffer getData() {
        return header.getData();
    }

    public String name() {
        return "CMPP TerminateResp";
    }

    public String dump() {
        Gson gson = new Gson();
        return gson.toJson(this, this.getClass());
    }
}
