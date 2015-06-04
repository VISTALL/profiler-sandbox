package org.mustbe.consulo.xprofiler.ui.mainPanel;

import java.util.Collections;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.xprofiler.XProfilerSession;
import org.mustbe.consulo.xprofiler.XProfilerThreadPanelProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.ColoredListCellRendererWrapper;
import com.intellij.ui.OnePixelSplitter;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.components.JBList;
import com.intellij.util.Consumer;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.ui.UIUtil;

/**
 * @author VISTALL
 * @since 04.06.2015
 */
public class XProfilerThreadPanel<T> extends XProfilerMainSubPanel
{
	private XProfilerThreadPanelProvider<T> myThreadProvider;
	private CollectionListModel<T> myModel;
	private JBList myThreadList;

	public XProfilerThreadPanel(@NotNull Project project, @NotNull XProfilerSession<?> session, XProfilerThreadPanelProvider<T> threadProvider)
	{
		super(project, session);
		myThreadProvider = threadProvider;
	}

	@NotNull
	@Override
	public JComponent createRootComponent()
	{
		OnePixelSplitter splitter = new OnePixelSplitter(0.3f);

		myModel = new CollectionListModel<T>(Collections.<T>emptyList());

		myThreadList = new JBList(myModel);
		myThreadList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		myThreadList.setCellRenderer(new ColoredListCellRendererWrapper<T>()
		{
			@Override
			protected void doCustomize(JList jList, T t, int i, boolean b, boolean b2)
			{
				myThreadProvider.renderItem(this, t);
			}
		});

		splitter.setFirstComponent(ScrollPaneFactory.createScrollPane(myThreadList));
		splitter.setSecondComponent(new JPanel());
		return splitter;
	}

	@Override
	public void run()
	{
		Key<List<T>> fetchKey = myThreadProvider.getFetchKey();

		mySession.fetchData(fetchKey, new Consumer<List<T>>()
		{
			@Override
			public void consume(final List<T> objects)
			{
				UIUtil.invokeLaterIfNeeded(new Runnable()
				{
					@Override
					public void run()
					{
						//noinspection unchecked
						T selectedValue = (T) myThreadList.getSelectedValue();

						T newValue = selectedValue == null ? null : (T) ContainerUtil.find(objects, selectedValue);

						myModel.replaceAll(objects);

						myThreadList.setSelectedValue(newValue, false);
					}
				});
			}
		});
	}
}
