package org.mustbe.consulo.xprofiler.ui.mainPanel;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.swing.JPanel;
import javax.swing.JTable;

import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.xprofiler.XProfilerMemoryObjectInfo;
import org.mustbe.consulo.xprofiler.XProfilerSession;
import com.intellij.concurrency.JobScheduler;
import com.intellij.ui.OnePixelSplitter;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.table.TableView;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.ListTableModel;
import com.intellij.util.ui.UIUtil;

/**
 * @author VISTALL
 * @since 31.05.2015
 */
public class XProfilerMemoryPanel extends JPanel
{
	private Future<?> myFuture;

	public XProfilerMemoryPanel(final XProfilerSession<?> session)
	{
		super(new BorderLayout());

		OnePixelSplitter pixelSplitter = new OnePixelSplitter(true);
		add(pixelSplitter);

		pixelSplitter.setFirstComponent(new JPanel());

		List<XProfilerMemoryObjectInfo> list = new ArrayList<XProfilerMemoryObjectInfo>();
		list.add(new XProfilerMemoryObjectInfo("java.lang.String", 2));
		list.add(new XProfilerMemoryObjectInfo("java.lang.Integer", 100));
		final ListTableModel<XProfilerMemoryObjectInfo> o = new ListTableModel<XProfilerMemoryObjectInfo>(new ColumnInfo[]{
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
		}, list, 1);

		TableView<XProfilerMemoryObjectInfo> table = new TableView<XProfilerMemoryObjectInfo>(o);

		pixelSplitter.setSecondComponent(ScrollPaneFactory.createScrollPane(table));

		myFuture = JobScheduler.getScheduler().scheduleAtFixedRate(new Runnable()
		{
			@Override
			public void run()
			{
				final List<XProfilerMemoryObjectInfo> memoryObjectInfos = session.getMemoryObjectInfos();
				UIUtil.invokeLaterIfNeeded(new Runnable()
				{
					@Override
					public void run()
					{
						o.setItems(memoryObjectInfos);
					}
				});
			}
		}, 1, 5, TimeUnit.SECONDS);
	}
}
