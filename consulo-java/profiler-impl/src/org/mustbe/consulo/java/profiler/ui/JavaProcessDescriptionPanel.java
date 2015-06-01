package org.mustbe.consulo.java.profiler.ui;

import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.mustbe.consulo.java.profiler.JavaProfilerProcess;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.components.labels.LinkLabel;
import com.intellij.ui.components.labels.LinkListener;

/**
 * @author VISTALL
 * @since 31.05.2015
 */
public class JavaProcessDescriptionPanel extends JPanel
{
	public JavaProcessDescriptionPanel(final Project project, JavaProfilerProcess process)
	{
		super(new VerticalFlowLayout(FlowLayout.LEFT));
		String mainClass = process.getMainClass();
		if(StringUtil.isEmpty(mainClass))
		{
			add(LabeledComponent.left(new JLabel("<unknown>"), "Main Class"));
		}
		else
		{
			add(LabeledComponent.left(new LinkLabel<String>(mainClass, null, new LinkListener<String>()
			{
				@Override
				public void linkSelected(LinkLabel linkLabel, String o)
				{
					PsiClass aClass = JavaPsiFacade.getInstance(project).findClass(o, GlobalSearchScope.allScope(project));
					if(aClass != null)
					{
						aClass.navigate(true);
					}
				}
			}, mainClass), "Main Class"));
		}

		String mainArguments = process.getMainArguments();
		if(!StringUtil.isEmpty(mainArguments))
		{
			JTextArea component = new JTextArea(mainArguments);
			component.setEditable(false);
			component.setLineWrap(true);
			add(LabeledComponent.create(component, "Main Arguments"));
		}

		String vmVersion = process.getVmVersion();
		if(!StringUtil.isEmpty(mainClass))
		{
			JTextArea component = new JTextArea(vmVersion);
			component.setEditable(false);
			component.setLineWrap(true);
			add(LabeledComponent.create(component, "VM Version"));
		}

		String vmArguments = process.getVmArguments();
		if(!StringUtil.isEmpty(vmArguments))
		{
			JTextArea component = new JTextArea(vmArguments);
			component.setEditable(false);
			component.setLineWrap(true);
			add(LabeledComponent.create(component, "VM Arguments"));
		}
	}
}
