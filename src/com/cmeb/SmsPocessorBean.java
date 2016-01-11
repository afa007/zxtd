package com.cmeb;

import java.util.Date;

public class SmsPocessorBean {
	
	private String SMSPOCESSOR_ID;
	private String MSISDN;
	private String TYPE;
	private String CONTENT;
	private String USERID;
	private Date CREATETIME;
	private String STATUS;
	private int SEQ_ID;

	public String getSMSPOCESSOR_ID() {
		return SMSPOCESSOR_ID;
	}

	public void setSMSPOCESSOR_ID(String sMSPOCESSOR_ID) {
		SMSPOCESSOR_ID = sMSPOCESSOR_ID;
	}

	public String getMSISDN() {
		return MSISDN;
	}

	public void setMSISDN(String mSISDN) {
		MSISDN = mSISDN;
	}

	public String getTYPE() {
		return TYPE;
	}

	public void setTYPE(String tYPE) {
		TYPE = tYPE;
	}

	public String getCONTENT() {
		return CONTENT;
	}

	public void setCONTENT(String cONTENT) {
		CONTENT = cONTENT;
	}

	public String getUSERID() {
		return USERID;
	}

	public void setUSERID(String uSERID) {
		USERID = uSERID;
	}

	public Date getCREATETIME() {
		return CREATETIME;
	}

	public void setCREATETIME(Date cREATETIME) {
		CREATETIME = cREATETIME;
	}

	public String getSTATUS() {
		return STATUS;
	}

	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}

	public int getSEQ_ID() {
		return SEQ_ID;
	}

	public void setSEQ_ID(int sEQ_ID) {
		SEQ_ID = sEQ_ID;
	}

}
