package consulo.xprofiler.ui.mainPanel;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import consulo.xprofiler.XProfilerSession;
import consulo.xprofiler.XProfilerThreadPanelProvider;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.TabbedPaneWrapper;

/**
 * @author VISTALL
 * @since 31.05.2015
 */
public class XProfilerMainPanel extends JPanel implements Disposable
{
	private Project myProject;

	public XProfilerMainPanel(Project project)
	{
		super(new BorderLayout());
		myProject = project;
	}

	public void init(XProfilerSession<?> session)
	{
		TabbedPaneWrapper tabbedPaneWrapper = new TabbedPaneWrapper(this);
		tabbedPaneWrapper.addTab("CPU", new JPanel());

		XProfilerMemoryPanel memoryPanel = new XProfilerMemoryPanel(myProject, session);
		memoryPanel.init();

		Disposer.register(this, memoryPanel);
		tabbedPaneWrapper.addTab("Memory", memoryPanel);

		XProfilerThreadPanelProvider<?> threadProvider = session.getThreadProvider();
		if(threadProvider != null)
		{
			//noinspection unchecked
			XProfilerThreadPanel threadPanel = new XProfilerThreadPanel(myProject, session, threadProvider);
			threadPanel.init();

			Disposer.register(this, threadPanel);
			tabbedPaneWrapper.addTab("Threads", threadPanel);
		}

		add(tabbedPaneWrapper.getComponent(), BorderLayout.CENTER);
	}

	@Override
	public void dispose()
	{
		Disposer.dispose(this);
	}
}
