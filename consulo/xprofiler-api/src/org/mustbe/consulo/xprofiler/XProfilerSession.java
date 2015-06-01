package org.mustbe.consulo.xprofiler;


import java.util.List;

import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.util.Key;

/**
 * @author VISTALL
 * @since 31.05.2015
 */
public class XProfilerSession<P extends XProfilerProcess> implements Disposable
{
	public static final Key<List<XProfilerMemoryObjectInfo>> DEFAULT_OBJECT_INFOS = Key.create("default.object.infos");

	private XProfiler<P> myProfiler;
	private P myProcess;

	public XProfilerSession(@NotNull XProfiler<P> profiler, @NotNull P process)
	{
		myProfiler = profiler;
		myProcess = process;
	}

	@NotNull
	public <T> T fetchData(@NotNull Key<T> key)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void dispose()
	{
	}
}
