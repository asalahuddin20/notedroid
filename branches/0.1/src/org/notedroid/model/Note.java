package org.notedroid.model;

import java.util.Date;

public class Note {
	
	public static final int TYPE_NOTE = 0;
    public static final int TYPE_FOLDER = 1;
	
	private long mId;
	private long mParentId;
	private int mType;
	private Date mCreationDate;
	private Date mModificationDate;
	private String mTitle;
	private String mBody;
	
	public Note(long id, long parentId, int type, Date creationDate, Date modificationDate, String title, String body) {
		mId = id;
		mParentId = parentId;
		mType = type;
		mCreationDate = creationDate;
		mModificationDate = modificationDate;
		mTitle = title;
		mBody = body;
	}
	
	public long getId() {
		return mId;
	}
	
	public long getParentId() {
		return mParentId;
	}
	
	public int getType() {
		return mType;
	}
	
	public Date getCreationDate() {
		return mCreationDate;
	}
	
	public Date getModificationDate() {
		return mModificationDate;
	}
	
	public String getTitle() {
		return mTitle;
	}
	
	public String getBody() {
		return mBody;
	}
}
