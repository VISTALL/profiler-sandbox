package org.mustbe.consulo.java.profiler;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.thrift.TException;
import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.profiler.MemoryInfo;
import org.mustbe.consulo.profiler.MemoryUsage;
import org.mustbe.consulo.profiler.ProfilerService;
import org.mustbe.consulo.xprofiler.XProfiler;
import org.mustbe.consulo.xprofiler.XProfilerMemoryObjectInfo;
import org.mustbe.consulo.xprofiler.XProfilerMemorySample;
import org.mustbe.consulo.xprofiler.XProfilerSession;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.Consumer;
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
			PsiClass aClass = JavaPsiFacade.getInstance(project).findClass(xProfilerMemoryObjectInfo.getType(), GlobalSearchScope.allScope(project));
			if(aClass != null)
			{
				aClass.navigate(true);
			}
		}
	};

	public static final Key<XProfilerMemorySample> HEAP_MEMORY_SAMPLE = Key.create("heap.memory.sample");
	public static final Key<XProfilerMemorySample> NONHEAP_MEMORY_SAMPLE = Key.create("non.heap.memory.sample");

	private HotSpotVirtualMachine myVirtualMachine;
	private ProfilerService.Client myClient;

	public JavaProfilerSession(XProfiler<JavaProfilerProcess> profiler,
			JavaProfilerProcess process,
			VirtualMachine virtualMachine,
			ProfilerService.Client client)
	{
		super(profiler, process);
		myClient = client;
		myVirtualMachine = (HotSpotVirtualMachine) virtualMachine;
	}

	@NotNull
	@Override
	public Pair<String, Key<XProfilerMemorySample>>[] getMemoryWatchKeys()
	{
		return new Pair[]{
				Pair.create("Heap Memory", HEAP_MEMORY_SAMPLE),
				Pair.create("Non Heap Memory", NONHEAP_MEMORY_SAMPLE),
		};
	}

	@Override
	protected void fetchDataImpl(@NotNull Key<?> key, @NotNull final Consumer<Object> consumer)
	{
		try
		{
			System.out.println(key);
			if(key == DEFAULT_OBJECT_INFOS)
			{
				InputStream inputStream = myVirtualMachine.heapHisto("-live");
				HeapHistory heapHistory = new HeapHistory(inputStream);

				Set<HeapHistory.ClassInfoImpl> heapHistogram = heapHistory.getHeapHistogram();
				List<XProfilerMemoryObjectInfo> list = new ArrayList<XProfilerMemoryObjectInfo>();
				for(HeapHistory.ClassInfoImpl classInfo : heapHistogram)
				{
					list.add(new XProfilerMemoryObjectInfo(classInfo.getName(), (int) classInfo.getInstancesCount(), ourNavigatable));
				}
				consumer.consume(list);
			}
			else if(key == HEAP_MEMORY_SAMPLE)
			{
				MemoryInfo result = myClient.memoryInfo();
				MemoryUsage heap = result.getHeap();
				consumer.consume(new XProfilerMemorySample(heap.getCommitted(), heap.getUsed()));
			}
			else if(key == NONHEAP_MEMORY_SAMPLE)
			{
				MemoryInfo result = myClient.memoryInfo();
				MemoryUsage heap = result.getNonHeap();
				consumer.consume(new XProfilerMemorySample(heap.getCommitted(), heap.getUsed()));
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		catch(TException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void dispose()
	{
		try
		{
			myVirtualMachine.detach();
		}
		catch(IOException ignored)
		{
		}
	}
}
