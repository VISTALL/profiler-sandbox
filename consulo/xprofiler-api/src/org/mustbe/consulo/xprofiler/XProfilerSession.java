package org.mustbe.consulo.xprofiler;


import java.util.List;

import org.consulo.lombok.annotations.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.Pair;
import com.intellij.util.Consumer;

/**
 * @author VISTALL
 * @since 31.05.2015
 */
@Logger
public class XProfilerSession<P extends XProfilerProcess> implements Disposable
{
	public static final Key<List<XProfilerMemoryObjectInfo>> DEFAULT_OBJECT_INFOS = Key.create("default.object.infos");
	public static final Key<XProfilerMemorySample> DEFAULT_MEMORY_SAMPLE = Key.create("default.memory.sample");
	public static final Key<XProfilerMemorySample> DEFAULT_THREAD_INFOS = Key.create("default.memory.sample");

	protected XProfiler<P> myProfiler;
	protected P myProcess;

	public XProfilerSession(@NotNull XProfiler<P> profiler, @NotNull P process)
	{
		myProfiler = profiler;
		myProcess = process;
	}

	@SuppressWarnings("unchecked")
	public final <T> void fetchData(@NotNull Key<T> key, @NotNull Consumer<T> consumer)
	{
		LOGGER.assertTrue(!ApplicationManager.getApplication().isDispatchThread(), "This method should not be called from dispatch thread, " +
				"due it can block it");
		fetchDataImpl(key, (Consumer<Object>) consumer);
	}

	protected void fetchDataImpl(@NotNull Key<?> key, @NotNull Consumer<Object> consumer)
	{
		throw new UnsupportedOperationException();
	}

	@Nullable
	public XProfilerThreadPanelProvider getThreadProvider()
	{
		return null;
	}

	@NotNull
	@SuppressWarnings("unchecked")
	public Pair<String, Key<XProfilerMemorySample>>[] getMemoryWatchKeys()
	{
		return new Pair[] {Pair.create("Memory", DEFAULT_MEMORY_SAMPLE)};
	}

	@Override
	public void dispose()
	{
	}
}
