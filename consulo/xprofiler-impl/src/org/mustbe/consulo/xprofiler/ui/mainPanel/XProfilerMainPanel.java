package org.mustbe.consulo.xprofiler.ui.mainPanel;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.mustbe.consulo.xprofiler.XProfilerSession;
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

		tabbedPaneWrapper.addTab("Memory", new XProfilerMemoryPanel(myProject, session));

		add(tabbedPaneWrapper.getComponent(), BorderLayout.CENTER);
	}

	@Override
	public void dispose()
	{
		Disposer.dispose(this);
	}
}
