package org.mustbe.consulo.java.profiler;

import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.xprofiler.XProfilerProcess;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredVmUtil;
import sun.jvmstat.monitor.StringMonitor;
import sun.jvmstat.perfdata.monitor.protocol.local.LocalMonitoredVm;

/**
 * @author VISTALL
 * @since 31.05.2015
 */
public class JavaProfilerProcess extends XProfilerProcess
{
	private LocalMonitoredVm myLocalMonitoredVm;

	public JavaProfilerProcess(LocalMonitoredVm localMonitoredVm)
	{
		super(localMonitoredVm.getVmIdentifier().getHost());
		myLocalMonitoredVm = localMonitoredVm;
	}

	public String getMainClass()
	{
		try
		{
			return MonitoredVmUtil.mainClass(myLocalMonitoredVm, true);
		}
		catch(MonitorException e)
		{
			return e.getMessage();
		}
	}

	@Nullable
	public String getVmVersion()
	{
		StringMonitor monitor = null;
		try
		{
			monitor = (StringMonitor)myLocalMonitoredVm.findByName("sun.rt.internalVersion");
		}
		catch(MonitorException e)
		{
			return null;
		}

		return monitor.stringValue();
	}

	@Nullable
	public String getMainArguments()
	{
		try
		{
			return MonitoredVmUtil.mainArgs(getLocalMonitoredVm());
		}
		catch(MonitorException e)
		{
			return null;
		}
	}

	@Nullable
	public String getVmArguments()
	{
		try
		{
			return MonitoredVmUtil.jvmArgs(getLocalMonitoredVm());
		}
		catch(MonitorException e)
		{
			return null;
		}
	}

	public LocalMonitoredVm getLocalMonitoredVm()
	{
		return myLocalMonitoredVm;
	}
}
