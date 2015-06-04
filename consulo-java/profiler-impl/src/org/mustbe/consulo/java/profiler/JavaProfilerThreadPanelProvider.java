package org.mustbe.consulo.java.profiler;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.profiler.TThreadInfo;
import org.mustbe.consulo.xprofiler.XProfilerThreadPanelProvider;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.Key;
import com.intellij.ui.SimpleColoredComponent;
import com.intellij.ui.SimpleTextAttributes;

/**
 * @author VISTALL
 * @since 04.06.2015
 */
public class JavaProfilerThreadPanelProvider implements XProfilerThreadPanelProvider<TThreadInfo>
{
	@NotNull
	@Override
	public Key<List<TThreadInfo>> getFetchKey()
	{
		return JavaProfilerSession.THREAD_INFOS;
	}

	@Override
	public void renderItem(@NotNull SimpleColoredComponent coloredComponent, @NotNull TThreadInfo item)
	{
		coloredComponent.append("[", SimpleTextAttributes.GRAY_ATTRIBUTES);
		coloredComponent.append(String.valueOf(item.getId()), SimpleTextAttributes.GRAY_ATTRIBUTES);
		coloredComponent.append("] ", SimpleTextAttributes.GRAY_ATTRIBUTES);
		coloredComponent.append(item.getName());

		Thread.State state = Thread.State.values()[item.getState()];
		coloredComponent.append(" ");
		coloredComponent.append(state.name(), SimpleTextAttributes.GRAY_ATTRIBUTES);

		switch(state)
		{
			case NEW:
				coloredComponent.setIcon(AllIcons.Debugger.ThreadRunning);  //FIXME [VISTALL] new icon?
				break;
			case RUNNABLE:
				coloredComponent.setIcon(AllIcons.Debugger.ThreadRunning);
				break;
			case BLOCKED:
				coloredComponent.setIcon(AllIcons.Debugger.ThreadFrozen);
				break;
			case WAITING:
				coloredComponent.setIcon(AllIcons.Debugger.ThreadSuspended);
				break;
			case TIMED_WAITING:
				coloredComponent.setIcon(AllIcons.Debugger.ThreadSuspended);
				break;
			case TERMINATED:
				coloredComponent.setIcon(AllIcons.Debugger.ThreadAtBreakpoint);
				break;
		}
	}
}
