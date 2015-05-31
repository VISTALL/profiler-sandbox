package org.mustbe.consulo.xprofiler;

import java.util.Collections;
import java.util.List;

/**
 * @author VISTALL
 * @since 31.05.2015
 */
public class XProfilerSession<P extends XProfilerProcess>
{
	private XProfiler<P> myProfiler;
	private P myProcess;

	public XProfilerSession(XProfiler<P> profiler, P process)
	{
		myProfiler = profiler;
		myProcess = process;
	}

	public List<XProfilerMemoryObjectInfo> getMemoryObjectInfos()
	{
		return Collections.emptyList();
	}
}
