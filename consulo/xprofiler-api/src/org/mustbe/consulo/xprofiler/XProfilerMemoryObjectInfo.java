package org.mustbe.consulo.xprofiler;

/**
 * @author VISTALL
 * @since 31.05.2015
 */
public class XProfilerMemoryObjectInfo
{
	private String myType;
	private int myCount;

	public XProfilerMemoryObjectInfo(String type, int count)
	{
		myType = type;
		myCount = count;
	}

	public String getType()
	{
		return myType;
	}

	public int getCount()
	{
		return myCount;
	}
}
