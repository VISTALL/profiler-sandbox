/*
 * Copyright 2006 Antonio S. R. Gomes
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package consulo.xprofiler.ui.mainPanel;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Font;
import java.text.SimpleDateFormat;

import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.drawable.ColorPainter;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import consulo.xprofiler.XProfilerMemorySample;
import com.intellij.ui.JBColor;

public class MemoryPlotPanel extends JPanel
{

	private TimeSeries totalSeries;
	private TimeSeries usedSeries;

	public MemoryPlotPanel(String title)
	{
		this(5 * 60 * 1000, title);
	}

	public MemoryPlotPanel(int maxAge, String title)
	{
		super(new BorderLayout());

		totalSeries = new TimeSeries("Committed Memory");
		totalSeries.setMaximumItemAge(maxAge);
		usedSeries = new TimeSeries("Used Memory");
		usedSeries.setMaximumItemAge(maxAge);
		TimeSeriesCollection seriesCollection = new TimeSeriesCollection();
		seriesCollection.addSeries(totalSeries);
		seriesCollection.addSeries(usedSeries);

		NumberAxis numberAxis = new NumberAxis("Memory (KB)");

		numberAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		DateAxis dateAxis = new DateAxis("Time");

		dateAxis.setAutoRange(true);
		dateAxis.setLowerMargin(0);
		dateAxis.setUpperMargin(0);
		dateAxis.setTickLabelsVisible(true);
		dateAxis.setDateFormatOverride(new SimpleDateFormat("HH:mm:ss"));

		XYLineAndShapeRenderer lineRenderer = new XYLineAndShapeRenderer(true, false);
		lineRenderer.setSeriesPaint(0, JBColor.RED);
		lineRenderer.setSeriesPaint(1, JBColor.GREEN);
		lineRenderer.setDefaultStroke(new BasicStroke(2F, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

		XYPlot xyplot = new XYPlot(seriesCollection, dateAxis, numberAxis, lineRenderer);
		xyplot.setBackgroundPainter(new ColorPainter(JBColor.white));
		xyplot.setDomainGridlinePaint(JBColor.LIGHT_GRAY);
		xyplot.setRangeGridlinePaint(JBColor.LIGHT_GRAY);
		xyplot.setAxisOffset(new RectangleInsets(5D, 5D, 5D, 5D));

		JFreeChart chart = new JFreeChart(title, new Font("SansSerif", Font.PLAIN, 14), xyplot, true);
		chart.setBackgroundPainter(new ColorPainter(JBColor.white));

		add(new ChartPanel(chart, 300, 300, 0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE, true, true,
				false, false, false, false), BorderLayout.CENTER);
	}

	public void addSample(Millisecond m, XProfilerMemorySample memorySample)
	{
		totalSeries.add(m, memorySample.getTotalMem() / 1024);
		usedSeries.add(m, memorySample.getUsedMem() / 1024);
	}

	public void reset()
	{
		totalSeries.clear();
		usedSeries.clear();
	}
}
