package consulo.xprofiler.ui.mainPanel;

import java.awt.BorderLayout;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jetbrains.annotations.NotNull;
import consulo.xprofiler.XProfilerSession;
import com.intellij.concurrency.JobScheduler;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;

/**
 * @author VISTALL
 * @since 04.06.2015
 */
public abstract class XProfilerMainSubPanel extends JPanel implements Disposable, Runnable
{
	protected Project myProject;
	protected XProfilerSession<?> mySession;
	private Future<?> myFuture;

	public XProfilerMainSubPanel(@NotNull Project project, @NotNull XProfilerSession<?> session)
	{
		super(new BorderLayout());
		myProject = project;
		mySession = session;
	}

	@NotNull
	public XProfilerMainSubPanel init()
	{
		add(createRootComponent(), BorderLayout.CENTER);

		myFuture = JobScheduler.getScheduler().scheduleAtFixedRate(this, 1, 1, TimeUnit.SECONDS);
		return this;
	}

	@NotNull
	public abstract JComponent createRootComponent();

	@Override
	public void dispose()
	{
		myFuture.cancel(false);
	}
}
