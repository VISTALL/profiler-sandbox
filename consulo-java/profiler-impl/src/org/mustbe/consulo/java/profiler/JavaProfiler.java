package org.mustbe.consulo.java.profiler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.JPanel;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.java.JavaIcons;
import org.mustbe.consulo.java.profiler.ui.JavaProcessDescriptionPanel;
import org.mustbe.consulo.xprofiler.XProfilerSession;
import org.mustbe.consulo.xprofiler.XProfiler;
import com.intellij.execution.ExecutionException;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.SimpleColoredComponent;
import com.intellij.ui.SimpleTextAttributes;
import com.jezhumble.javasysmon.JavaSysMon;
import com.jezhumble.javasysmon.OsProcess;
import com.sun.tools.attach.VirtualMachine;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVmUtil;
import sun.jvmstat.monitor.VmIdentifier;
import sun.jvmstat.perfdata.monitor.protocol.local.LocalMonitoredVm;

/**
 * @author VISTALL
 * @since 31.05.2015
 */
public class JavaProfiler extends XProfiler<JavaProfilerProcess>
{
	@NotNull
	@Override
	public Icon getIcon()
	{
		return JavaIcons.Java;
	}

	@Override
	public void renderProcess(JavaProfilerProcess process, SimpleColoredComponent coloredComponent)
	{
		JavaSysMon javaSysMon = new JavaSysMon();

		OsProcess osProcess = javaSysMon.processTree().find(Integer.parseInt(process.getId()));
		if(osProcess != null)
		{
			coloredComponent.append("[" + osProcess.processInfo().getName() + "] ", SimpleTextAttributes.GRAY_ATTRIBUTES);
		}

		String mainClass = process.getMainClass();
		if(!StringUtil.isEmpty(mainClass))
		{
			coloredComponent.append(mainClass);
		}
	}

	@NotNull
	@Override
	public XProfilerSession<JavaProfilerProcess> createSession(@NotNull JavaProfilerProcess process, @NotNull ProgressIndicator indicator) throws ExecutionException
	{
		try
		{
			VirtualMachine vm = VirtualMachine.attach(process.getId());
			vm.loadAgent("R:\\profiler4j\\target\\profiler4j-1.0-beta3\\agent.jar", "");  //TODO [VISTALL]
			return new JavaProfilerSession(this, process, vm);
		}
		catch(Throwable e)
		{
			throw new ExecutionException(e);
		}
	}

	@NotNull
	@Override
	public JPanel createDescriptionPanel(@NotNull Project project, JavaProfilerProcess process)
	{
		return new JavaProcessDescriptionPanel(project, process);
	}

	@Override
	public List<JavaProfilerProcess> getPossibleProcessesForAttach()
	{
		List<JavaProfilerProcess> profilerProcesses = new ArrayList<JavaProfilerProcess>();
		try
		{
			MonitoredHost host = MonitoredHost.getMonitoredHost((String)null);
			Set<Integer> set = host.activeVms();
			for(Integer o : set)
			{
				VmIdentifier vmIdentifier = new VmIdentifier(o.toString());
				LocalMonitoredVm localMonitoredVm = new LocalMonitoredVm(vmIdentifier, 10);
				if(MonitoredVmUtil.isAttachable(localMonitoredVm))
				{

					profilerProcesses.add(new JavaProfilerProcess(localMonitoredVm));
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return profilerProcesses;
	}
}
