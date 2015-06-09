package org.mustbe.consulo.xprofiler.ui.mainPanel;

import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jfree.data.time.Millisecond;
import org.mustbe.consulo.xprofiler.XProfilerMemoryObjectInfo;
import org.mustbe.consulo.xprofiler.XProfilerMemorySample;
import org.mustbe.consulo.xprofiler.XProfilerSession;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.Pair;
import com.intellij.ui.DoubleClickListener;
import com.intellij.ui.OnePixelSplitter;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.table.TableView;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.util.Consumer;
import com.intellij.util.PairConsumer;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.ListTableModel;
import com.intellij.util.ui.TableViewModel;
import com.intellij.util.ui.UIUtil;

/**
 * @author VISTALL
 * @since 31.05.2015
 */
public class XProfilerMemoryPanel extends XProfilerMainSubPanel
{
	private static final ColumnInfo[] ourColumns = new ColumnInfo[]{
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
	};

	private Map<Key<XProfilerMemorySample>, MemoryPlotPanel> myMemoryPanels;
	private TableView<XProfilerMemoryObjectInfo> myTableView;

	public XProfilerMemoryPanel(Project project, final XProfilerSession<?> session)
	{
		super(project, session);
	}

	@NotNull
	@Override
	public JComponent createRootComponent()
	{
		myMemoryPanels = new HashMap<Key<XProfilerMemorySample>, MemoryPlotPanel>();

		OnePixelSplitter pixelSplitter = new OnePixelSplitter(true);

		Pair<String, Key<XProfilerMemorySample>>[] memoryWatchKeys = mySession.getMemoryWatchKeys();

		JPanel memoryPanel = new JPanel(new GridLayoutManager(1, memoryWatchKeys.length));

		for(int i = 0; i < memoryWatchKeys.length; i++)
		{
			Pair<String, Key<XProfilerMemorySample>> memoryWatchKey = memoryWatchKeys[i];
			MemoryPlotPanel value = new MemoryPlotPanel(memoryWatchKey.getFirst());
			myMemoryPanels.put(memoryWatchKey.getSecond(), value);
			memoryPanel.add(value, new GridConstraints(0, i, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
					GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null));
		}

		pixelSplitter.setFirstComponent(memoryPanel);

		final ListTableModel<XProfilerMemoryObjectInfo> tableModel = new ListTableModel<XProfilerMemoryObjectInfo>(ourColumns,
				Collections.<XProfilerMemoryObjectInfo>emptyList(), 1);

		myTableView = new TableView<XProfilerMemoryObjectInfo>(tableModel);
		new DoubleClickListener()
		{
			@Override
			protected boolean onDoubleClick(MouseEvent mouseEvent)
			{
				XProfilerMemoryObjectInfo selectedObject = myTableView.getSelectedObject();
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
		}.installOn(myTableView);
		myTableView.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		pixelSplitter.setSecondComponent(ScrollPaneFactory.createScrollPane(myTableView));
		return pixelSplitter;
	}

	@Override
	public void run()
	{
		mySession.fetchData(XProfilerSession.DEFAULT_OBJECT_INFOS, new Consumer<List<XProfilerMemoryObjectInfo>>()
		{
			@Override
			public void consume(final List<XProfilerMemoryObjectInfo> memoryObjectInfos)
			{
				UIUtil.invokeLaterIfNeeded(new Runnable()
				{
					@Override
					public void run()
					{
						XProfilerMemoryObjectInfo selectedObject = myTableView.getSelectedObject();
						XProfilerMemoryObjectInfo newSelectedObject = selectedObject == null ? null : ContainerUtil.find(memoryObjectInfos,
								selectedObject);

						//noinspection unchecked
						TableViewModel<XProfilerMemoryObjectInfo> model = (TableViewModel<XProfilerMemoryObjectInfo>) myTableView.getModel();

						model.setItems(memoryObjectInfos);

						myTableView.setSelection(Collections.singletonList(newSelectedObject));
					}
				});
			}
		});

		final Millisecond m = new Millisecond();

		for(Map.Entry<Key<XProfilerMemorySample>, MemoryPlotPanel> entry : myMemoryPanels.entrySet())
		{
			Key<XProfilerMemorySample> key = entry.getKey();
			final MemoryPlotPanel value = entry.getValue();

			mySession.fetchData(key, new Consumer<XProfilerMemorySample>()
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
}
