package org.mustbe.consulo.xprofiler;

import java.awt.FlowLayout;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jetbrains.annotations.NotNull;
import com.intellij.execution.ExecutionException;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.ui.SimpleColoredComponent;

/**
 * @author VISTALL
 * @since 31.05.2015
 */
public abstract class XProfiler<P extends XProfilerProcess>
{
	public static final ExtensionPointName<XProfiler<?>> EP_NAME = ExtensionPointName.create("com.intellij.xprofiler");

	public abstract List<P> getPossibleProcessesForAttach();

	@NotNull
	public Icon getIcon()
	{
		return AllIcons.Debugger.Frame;
	}

	@NotNull
	public XProfilerSession<P> createSession(@NotNull P process, @NotNull ProgressIndicator indicator) throws ExecutionException
	{
		return new XProfilerSession<P>(this, process);
	}

	@NotNull
	public JPanel createDescriptionPanel(@NotNull Project project, P process)
	{
		JPanel panel = new JPanel(new VerticalFlowLayout(FlowLayout.LEFT));
		panel.add(LabeledComponent.left(new JLabel(process.getId()), "Name"));
		return panel;
	}

	public void renderProcess(P process, SimpleColoredComponent coloredComponent)
	{
		coloredComponent.append(process.getId());
		coloredComponent.setIcon(AllIcons.Actions.Preview);
	}
}
