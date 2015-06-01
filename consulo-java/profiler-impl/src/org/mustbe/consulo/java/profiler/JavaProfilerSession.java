package org.mustbe.consulo.java.profiler;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.xprofiler.XProfiler;
import org.mustbe.consulo.xprofiler.XProfilerMemoryObjectInfo;
import org.mustbe.consulo.xprofiler.XProfilerSession;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.PairConsumer;
import com.sun.tools.attach.VirtualMachine;
import sun.tools.attach.HotSpotVirtualMachine;

/**
 * @author VISTALL
 * @since 31.05.2015
 */
public class JavaProfilerSession extends XProfilerSession<JavaProfilerProcess>
{
	private static final PairConsumer<Project, XProfilerMemoryObjectInfo> ourNavigatable = new PairConsumer<Project, XProfilerMemoryObjectInfo>()
	{
		@Override
		public void consume(Project project, XProfilerMemoryObjectInfo xProfilerMemoryObjectInfo)
		{
			PsiClass aClass = JavaPsiFacade.getInstance(project).findClass(xProfilerMemoryObjectInfo.getType(),
					GlobalSearchScope.allScope(project));
			if(aClass != null)
			{
				aClass.navigate(true);
			}
		}
	};

	private HotSpotVirtualMachine myVirtualMachine;

	public JavaProfilerSession(XProfiler<JavaProfilerProcess> profiler, JavaProfilerProcess process, VirtualMachine virtualMachine)
	{
		super(profiler, process);
		myVirtualMachine = (HotSpotVirtualMachine) virtualMachine;
	}

	@NotNull
	@Override
	public <T> T fetchData(@NotNull Key<T> key)
	{
		if(key == DEFAULT_OBJECT_INFOS)
		{
			try
			{
				InputStream inputStream = myVirtualMachine.heapHisto("-live");
				HeapHistory heapHistory = new HeapHistory(inputStream);

				Set<HeapHistory.ClassInfoImpl> heapHistogram = heapHistory.getHeapHistogram();
				List<XProfilerMemoryObjectInfo> list = new ArrayList<XProfilerMemoryObjectInfo>();
				for(HeapHistory.ClassInfoImpl classInfo : heapHistogram)
				{
					list.add(new XProfilerMemoryObjectInfo(classInfo.getName(), (int) classInfo.getInstancesCount(), ourNavigatable));
				}
				return (T) list;
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		return super.fetchData(key);
	}
}
