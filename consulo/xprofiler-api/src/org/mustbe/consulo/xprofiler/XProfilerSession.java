package org.mustbe.consulo.xprofiler;


import java.util.List;

import org.consulo.lombok.annotations.Logger;
import org.jetbrains.annotations.NotNull;
import com.intellij.execution.ExecutionException;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.KeyWithDefaultValue;

/**
 * @author VISTALL
 * @since 31.05.2015
 */
@Logger
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
	public final <T> T fetchData(@NotNull Key<T> key) throws ExecutionException
	{
		try
		{
			//noinspection unchecked
			return (T) fetchDataImpl(key);
		}
		catch(Throwable e)
		{
			if(key instanceof KeyWithDefaultValue)
			{
				return ((KeyWithDefaultValue<T>) key).getDefaultValue();
			}
			throw new ExecutionException(e);
		}
	}

	@NotNull
	public Object fetchDataImpl(@NotNull Key<?> key)  throws Throwable
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void dispose()
	{
	}
}
