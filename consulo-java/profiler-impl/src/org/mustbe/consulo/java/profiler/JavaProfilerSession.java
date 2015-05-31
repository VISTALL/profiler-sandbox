package org.mustbe.consulo.java.profiler;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.mustbe.consulo.xprofiler.XProfiler;
import org.mustbe.consulo.xprofiler.XProfilerMemoryObjectInfo;
import org.mustbe.consulo.xprofiler.XProfilerSession;
import com.sun.tools.attach.VirtualMachine;
import sun.tools.attach.HotSpotVirtualMachine;

/**
 * @author VISTALL
 * @since 31.05.2015
 */
public class JavaProfilerSession extends XProfilerSession<JavaProfilerProcess>
{
	private HotSpotVirtualMachine myVirtualMachine;

	public JavaProfilerSession(XProfiler<JavaProfilerProcess> profiler, JavaProfilerProcess process, VirtualMachine virtualMachine)
	{
		super(profiler, process);
		myVirtualMachine = (HotSpotVirtualMachine) virtualMachine;
	}

	@Override
	public List<XProfilerMemoryObjectInfo> getMemoryObjectInfos()
	{
		try
		{
			InputStream inputStream = myVirtualMachine.heapHisto("-live");
			HeapHistory heapHistory = new HeapHistory(inputStream);

			Set<HeapHistory.ClassInfoImpl> heapHistogram = heapHistory.getHeapHistogram();
			List<XProfilerMemoryObjectInfo> list = new ArrayList<XProfilerMemoryObjectInfo>();
			for(HeapHistory.ClassInfoImpl classInfo : heapHistogram)
			{
				list.add(new XProfilerMemoryObjectInfo(classInfo.getName(), (int) classInfo.getInstancesCount()));
			}
			return list;
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		return super.getMemoryObjectInfos();
	}
}
