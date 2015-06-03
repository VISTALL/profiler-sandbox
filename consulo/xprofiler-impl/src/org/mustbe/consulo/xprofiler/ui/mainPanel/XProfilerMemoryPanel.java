package org.mustbe.consulo.xprofiler.ui.mainPanel;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import org.jetbrains.annotations.Nullable;
import org.jfree.data.time.Millisecond;
import org.mustbe.consulo.xprofiler.XProfilerMemoryObjectInfo;
import org.mustbe.consulo.xprofiler.XProfilerMemorySample;
import org.mustbe.consulo.xprofiler.XProfilerSession;
import com.intellij.concurrency.JobScheduler;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.Pair;
import com.intellij.ui.DoubleClickListener;
import com.intellij.ui.OnePixelSplitter;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.components.panels.HorizontalLayout;
import com.intellij.ui.table.TableView;
import com.intellij.util.Consumer;
import com.intellij.util.PairConsumer;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.ListTableModel;
import com.intellij.util.ui.UIUtil;

/**
 * @author VISTALL
 * @since 31.05.2015
 */
public class XProfilerMemoryPanel extends JPanel implements Disposable
{
	private Future<?> myFuture;
	private Project myProject;

	private Map<Key<XProfilerMemorySample>, MemoryPlotPanel> myMemoryPanels = new HashMap<Key<XProfilerMemorySample>, MemoryPlotPanel>();

	public XProfilerMemoryPanel(Project project, final XProfilerSession<?> session)
	{
		super(new BorderLayout());
		myProject = project;

		OnePixelSplitter pixelSplitter = new OnePixelSplitter(true);
		add(pixelSplitter);

		Pair<String, Key<XProfilerMemorySample>>[] memoryWatchKeys = session.getMemoryWatchKeys();

		JPanel panel = new JPanel(new HorizontalLayout(0));

		for(Pair<String, Key<XProfilerMemorySample>> memoryWatchKey : memoryWatchKeys)
		{
			MemoryPlotPanel value = new MemoryPlotPanel(memoryWatchKey.getFirst());
			myMemoryPanels.put(memoryWatchKey.getSecond(), value);
			panel.add(value);
		}

		pixelSplitter.setFirstComponent(panel);

		final ListTableModel<XProfilerMemoryObjectInfo> tableModel = new ListTableModel<XProfilerMemoryObjectInfo>(new ColumnInfo[]{
				new ColumnInfo<XProfilerMemoryObjectInfo, String>("Type")
				{
					@Nullable
					@Override
					public String valueOf(XProfilerMemoryObjectInfo o)
					{
						return o.getType();
					}
				},
				new ColumnInfo<XProfilerMemoryObjectInfo, Integer>("Count")
				{
					@Nullable
					@Override
					public Comparator<XProfilerMemoryObjectInfo> getComparator()
					{
						return new Comparator<XProfilerMemoryObjectInfo>()
						{
							@Override
							public int compare(XProfilerMemoryObjectInfo o1, XProfilerMemoryObjectInfo o2)
							{
								return o2.getCount() - o1.getCount();
							}
						};
					}

					@Nullable
					@Override
					public Integer valueOf(XProfilerMemoryObjectInfo o)
					{
						return o.getCount();
					}

					@Override
					public int getWidth(JTable table)
					{
						return 100;
					}
				}
		}, Collections.<XProfilerMemoryObjectInfo>emptyList(), 1);

		final TableView<XProfilerMemoryObjectInfo> table = new TableView<XProfilerMemoryObjectInfo>(tableModel);
		new DoubleClickListener()
		{
			@Override
			protected boolean onDoubleClick(MouseEvent mouseEvent)
			{
				XProfilerMemoryObjectInfo selectedObject = table.getSelectedObject();
				if(selectedObject == null)
				{
					return false;
				}
				PairConsumer<Project, XProfilerMemoryObjectInfo> navigatable = selectedObject.getNavigatable();
				if(navigatable != null)
				{
					navigatable.consume(myProject, selectedObject);
				}
				return true;
			}
		}.installOn(table);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		pixelSplitter.setSecondComponent(ScrollPaneFactory.createScrollPane(table));

		myFuture = JobScheduler.getScheduler().scheduleAtFixedRate(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					session.fetchData(XProfilerSession.DEFAULT_OBJECT_INFOS, new Consumer<List<XProfilerMemoryObjectInfo>>()
					{
						@Override
						public void consume(final List<XProfilerMemoryObjectInfo> memoryObjectInfos)
						{
							UIUtil.invokeLaterIfNeeded(new Runnable()
							{
								@Override
								public void run()
								{
									XProfilerMemoryObjectInfo selectedObject = table.getSelectedObject();
									XProfilerMemoryObjectInfo newSelectedObject = selectedObject == null ? null : ContainerUtil.find(memoryObjectInfos,
											selectedObject);

									tableModel.setItems(memoryObjectInfos);

									table.setSelection(Collections.singletonList(newSelectedObject));
								}
							});
						}
					});

					final Millisecond m = new Millisecond();

					for(Map.Entry<Key<XProfilerMemorySample>, MemoryPlotPanel> entry : myMemoryPanels.entrySet())
					{
						Key<XProfilerMemorySample> key = entry.getKey();
						final MemoryPlotPanel value = entry.getValue();

						session.fetchData(key, new Consumer<XProfilerMemorySample>()
						{
							@Override
							public void consume(final XProfilerMemorySample memorySample)
							{
								UIUtil.invokeLaterIfNeeded(new Runnable()
								{
									@Override
									public void run()
									{
										value.addSample(m, memorySample);
									}
								});
							}
						});
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}, 1, 1, TimeUnit.SECONDS);
	}

	@Override
	public void dispose()
	{
		myFuture.cancel(false);
	}
}
