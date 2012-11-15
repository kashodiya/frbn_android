package org.frb.sf.frbn.data;

public class Message {
	private final String messageBody;
	private final String title;
	private final String category;
	private final String district;
	private final long timestamp;
	
	public long getTimestamp() {
		return timestamp;
	}
	public String getMessageBody() {
		return messageBody;
	}
	public String getTitle() {
		return title;
	}
	public String getCategory() {
		return category;
	}
	public String getDistrict() {
		return district;
	}
	public Message(String messageBody, String title, String category,
			String district, long timestamp) {
		super();
		this.messageBody = messageBody;
		this.title = title;
		this.category = category;
		this.district = district;
		this.timestamp = timestamp;
	}
	
	@Override
	public String toString() {
		StringBuffer buff =  new StringBuffer();
		buff.append("body = ");
		buff.append(getMessageBody());
		buff.append(", title = ");
		buff.append(getTitle());
		buff.append(", category = ");
		buff.append(getCategory());
		buff.append(", district = ");
		buff.append(getDistrict());
		buff.append(", timestamp = ");
		buff.append(getTimestamp());
		return buff.toString();
	}

}
