package org.mustbe.consulo.xprofiler.ui.mainPanel;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import com.intellij.ui.OnePixelSplitter;
import com.intellij.ui.components.JBList;

/**
 * @author VISTALL
 * @since 04.06.2015
 */
public class XProfilerThreadPanel extends JPanel
{
	public XProfilerThreadPanel()
	{
		super(new BorderLayout());

		OnePixelSplitter splitter = new OnePixelSplitter(0.3f);

		JBList threadList = new JBList();
	}
}
