package org.frb.sf.frbn.data;


public final class MessageItem {

	private final Message message;

	MessageItem(Message message) {
		this.message = message;
	}

	public Message getResult() {
		return message;		
	}

	public String getDisplayAndDetails() {
		StringBuilder displayTxt = new StringBuilder();
		displayTxt.append(message.getMessageBody());
		//TODO: Build display txt
		return displayTxt.toString();
	}

}
