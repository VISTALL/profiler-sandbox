package org.mustbe.consulo.xprofiler;

import org.jetbrains.annotations.NotNull;

/**
 * @author VISTALL
 * @since 31.05.2015
 */
public class XProfilerProcess
{
	private String myId;

	public XProfilerProcess(@NotNull String id)
	{
		myId = id;
	}

	public String getId()
	{
		return myId;
	}

	@Override
	public int hashCode()
	{
		return myId.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj != null && obj.getClass() == getClass() && ((XProfilerProcess) obj).getId().equals(getId());
	}
}
