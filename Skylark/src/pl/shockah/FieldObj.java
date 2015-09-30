package pl.shockah;

import java.lang.reflect.Field;

public final class FieldObj {
	protected final Field field;
	protected final Object object;
	
	public FieldObj(Field field, Object object) {
		this.field = field;
		this.object = object;
	}
	
	public Field getField() {
		return field;
	}
	public Object getObject() {
		return object;
	}
	
	public Object get() throws IllegalArgumentException, IllegalAccessException { return field.get(object); }
	public boolean getBoolean() throws IllegalArgumentException, IllegalAccessException { return field.getBoolean(object); }
	public byte getByte() throws IllegalArgumentException, IllegalAccessException { return field.getByte(object); }
	public char getChar() throws IllegalArgumentException, IllegalAccessException { return field.getChar(object); }
	public short getShort() throws IllegalArgumentException, IllegalAccessException { return field.getShort(object); }
	public int getInt() throws IllegalArgumentException, IllegalAccessException { return field.getInt(object); }
	public long getLong() throws IllegalArgumentException, IllegalAccessException { return field.getLong(object); }
	public float getFloat() throws IllegalArgumentException, IllegalAccessException { return field.getFloat(object); }
	public double getDouble() throws IllegalArgumentException, IllegalAccessException { return field.getDouble(object); }
	
	public void set(Object obj) throws IllegalArgumentException, IllegalAccessException { field.set(object,obj); }
	public void setBoolean(boolean b) throws IllegalArgumentException, IllegalAccessException { field.setBoolean(object,b); }
	public void setByte(byte b) throws IllegalArgumentException, IllegalAccessException { field.setByte(object,b); }
	public void setChar(char c) throws IllegalArgumentException, IllegalAccessException { field.setChar(object,c); }
	public void setShort(short s) throws IllegalArgumentException, IllegalAccessException { field.setShort(object,s); }
	public void setInt(int i) throws IllegalArgumentException, IllegalAccessException { field.setInt(object,i); }
	public void setLong(long l) throws IllegalArgumentException, IllegalAccessException { field.setLong(object,l); }
	public void setFloat(float f) throws IllegalArgumentException, IllegalAccessException { field.setFloat(object,f); }
	public void setDouble(double d) throws IllegalArgumentException, IllegalAccessException { field.setDouble(object,d); }
}