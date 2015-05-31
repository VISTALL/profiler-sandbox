package org.mustbe.consulo.xprofiler.ui.attachPanel;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.xprofiler.XProfiler;
import org.mustbe.consulo.xprofiler.XProfilerProcess;
import org.mustbe.consulo.xprofiler.XProfilerSession;
import com.intellij.concurrency.JobScheduler;
import com.intellij.concurrency.ResultConsumer;
import com.intellij.execution.ExecutionException;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.ui.ColoredListCellRendererWrapper;
import com.intellij.ui.DoubleClickListener;
import com.intellij.ui.OnePixelSplitter;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.SortedListModel;
import com.intellij.ui.components.JBList;
import com.intellij.util.ui.UIUtil;

/**
 * @author VISTALL
 * @since 31.05.2015
 */
public class XProfilerAttachSessionPanel extends JPanel implements Disposable
{
	public static class XProfilerProcessItem
	{
		private XProfiler<?> myProfiler;
		private XProfilerProcess myProcess;

		public XProfilerProcessItem(XProfiler<?> profiler, XProfilerProcess process)
		{
			myProfiler = profiler;
			myProcess = process;
		}

		public XProfiler<?> getProfiler()
		{
			return myProfiler;
		}

		public XProfilerProcess getProcess()
		{
			return myProcess;
		}
	}

	private Future<?> myTask;

	private JPanel myDescriptionPanel;

	private AtomicBoolean myProgress = new AtomicBoolean(false);
	private Project myProject;

	public XProfilerAttachSessionPanel(@NotNull final Project project, @NotNull final ResultConsumer<XProfilerSession> consumer)
	{
		super(new BorderLayout());
		myProject = project;

		final OnePixelSplitter splitter = new OnePixelSplitter(false, 0.3f);

		final SortedListModel<XProfilerProcessItem> model = new SortedListModel<XProfilerProcessItem>(new Comparator<XProfilerProcessItem>()
		{
			@Override
			public int compare(XProfilerProcessItem o1, XProfilerProcessItem o2)
			{
				return o1.getProcess().getId().compareTo(o2.getProcess().getId());
			}
		});
		final JBList processList = new JBList(model);
		processList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		processList.setCellRenderer(new ColoredListCellRendererWrapper<XProfilerProcessItem>()
		{
			@Override
			protected void doCustomize(JList jList, XProfilerProcessItem xProfilerProcessItem, int i, boolean b, boolean b2)
			{
				XProfiler profiler = xProfilerProcessItem.getProfiler();
				XProfilerProcess process = xProfilerProcessItem.getProcess();

				//noinspection unchecked
				profiler.renderProcess(process, this);
				setIcon(profiler.getIcon());
			}
		});

		new DoubleClickListener()
		{
			@Override
			protected boolean onDoubleClick(MouseEvent mouseEvent)
			{
				final XProfilerProcessItem selectedValue = (XProfilerProcessItem) processList.getSelectedValue();
				if(selectedValue == null)
				{
					return false;
				}

				if(myProgress.compareAndSet(false, true))
				{
					final XProfiler profiler = selectedValue.getProfiler();
					new Task.Backgroundable(project, "Attaching to process [" + selectedValue.getProcess().getId() + "]" , false)
					{
						@Override
						public void run(@NotNull ProgressIndicator indicator)
						{
							indicator.setIndeterminate(true);
							try
							{
								XProfilerSession session = profiler.createSession(selectedValue.getProcess(), indicator);
								stopUpdate();
								consumer.onSuccess(session);
							}
							catch(ExecutionException e)
							{
								myProgress.set(false);
								consumer.onFailure(e);
							}
						}
					}.queue();
				}
				return true;
			}
		}.installOn(processList);

		splitter.setFirstComponent(ScrollPaneFactory.createScrollPane(processList));

		myDescriptionPanel = new JPanel(new VerticalFlowLayout(true, true));
		splitter.setSecondComponent(myDescriptionPanel);


		processList.addListSelectionListener(new ListSelectionListener()
		{
			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				if(e.getValueIsAdjusting())
				{
					return;
				}
				Object selectedValue = processList.getSelectedValue();
				if(selectedValue instanceof XProfilerProcessItem)
				{
					select((XProfilerProcessItem) selectedValue);
				}
				else
				{
					select(null);
				}
			}
		});
		myTask = JobScheduler.getScheduler().scheduleAtFixedRate(new Runnable()
		{
			@Override
			public void run()
			{
				final List<XProfilerProcessItem> profilerProcessItems = new ArrayList<XProfilerProcessItem>();

				for(XProfiler<?> profiler : XProfiler.EP_NAME.getExtensions())
				{
					List<? extends XProfilerProcess> possibleProcessesForAttach = profiler.getPossibleProcessesForAttach();
					for(XProfilerProcess process : possibleProcessesForAttach)
					{
						profilerProcessItems.add(new XProfilerProcessItem(profiler, process));
					}
				}

				UIUtil.invokeLaterIfNeeded(new Runnable()
				{
					@Override
					public void run()
					{
						XProfilerProcessItem selectedValue = (XProfilerProcessItem) processList.getSelectedValue();

						model.setAll(profilerProcessItems);

						XProfilerProcessItem item = null;
						if(selectedValue != null)
						{
							for(XProfilerProcessItem profilerProcessItem : profilerProcessItems)
							{
								if(profilerProcessItem.getProcess().equals(selectedValue.getProcess()))
								{
									item = profilerProcessItem;
								}
							}
						}
						processList.setSelectedValue(item, false);
					}
				});
			}
		}, 0, 5, TimeUnit.SECONDS);

		add(splitter, BorderLayout.CENTER);
	}

	public void stopUpdate()
	{
		myTask.cancel(false);
	}

	private void select(@Nullable XProfilerProcessItem item)
	{
		myDescriptionPanel.removeAll();

		if(item != null)
		{
			XProfiler profiler = item.getProfiler();
			JPanel selftPanel = profiler.createDescriptionPanel(myProject, item.getProcess());
			myDescriptionPanel.add(selftPanel);
		}
		myDescriptionPanel.validate();
	}

	@Override
	public void dispose()
	{
		stopUpdate();
	}
}
