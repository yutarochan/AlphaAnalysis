package com.tigytech.alpha.core.representations;

import java.util.List;
import java.util.Vector;

public abstract interface Frame {
	public abstract boolean entityP();

	public abstract boolean entityP(String paramString);

	public abstract boolean functionP();

	public abstract boolean functionP(String paramString);

	public abstract boolean relationP();

	public abstract boolean relationP(String paramString);

	public abstract boolean sequenceP();

	public abstract boolean sequenceP(String paramString);

	public abstract boolean isA(String paramString);

	public abstract boolean isAllOf(String[] paramArrayOfString);

	public abstract boolean isNoneOf(String[] paramArrayOfString);

	public abstract String getType();

	public abstract void addType(String paramString);

	public abstract void addType(String paramString1, String paramString2);

	public abstract void setSubject(Entity paramEntity);

	public abstract Entity getSubject();

	public abstract void setObject(Entity paramEntity);

	public abstract Entity getObject();

	public abstract void addElement(Entity paramEntity);

	public abstract Vector<Entity> getElements();

	public abstract Entity getElement(int paramInt);

	public abstract Thread getThread(String paramString);

	public abstract List<Entity> getAllComponents();

	public abstract Bundle getBundle();

	public abstract Vector<Entity> getModifiers();

	public abstract void addModifier(Entity paramEntity);

	public abstract void addModifier(int paramInt, Entity paramEntity);

	public abstract void setModifiers(Vector<Entity> paramVector);

	public abstract void clearModifiers();

	public abstract char getPrettyPrintType();
}