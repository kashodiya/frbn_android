package org.frb.sf.frbn;

import android.os.Parcel;
import android.os.Parcelable;

public class Message implements Parcelable{
	private String messageBody = "";
	private String title = "";
	private String category = "";
	private String district = "";
	private long timestamp = 0;
	private int mData;

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
	
	private Message(Parcel in) {
        mData = in.readInt(); 
    }
	
    public static final Parcelable.Creator<Message> CREATOR = new Parcelable.Creator<Message>() {
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        public Message[] newArray(int size) {
            return new Message[size];
        }
    };
	
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
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt(mData);
	}

}
