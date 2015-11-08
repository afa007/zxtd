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
public class ActiveTest extends Request {

    public ActiveTest() {
        super(CmppConstant.CMD_ACTIVE_TEST);
    }

    protected Response createResponse() {
        return new ActiveTestResp();
    }

    public void setData(ByteBuffer buffer) throws PDUException {
        header.setData(buffer);
    }

    public ByteBuffer getData() {
        return header.getData();
    }

    public String name() {
        return "CMPP ActiveTest";
    }

    public String dump() {
        Gson gson = new Gson();
        return gson.toJson(this, this.getClass());
    }

    @Override
    public ActiveTestResp getResponse()
    {
        ActiveTestResp activeTestResp = (ActiveTestResp) super.getResponse();
        activeTestResp.setCommandId(CmppConstant.CMD_ACTIVE_TEST_RESP);
        return activeTestResp;
    }
}
