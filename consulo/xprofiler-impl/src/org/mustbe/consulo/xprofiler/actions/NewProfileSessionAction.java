package org.mustbe.consulo.xprofiler.actions;

import java.util.Random;

import org.mustbe.consulo.xprofiler.file.XProfilerSessionFileType;
import com.intellij.icons.AllIcons;
import com.intellij.ide.actions.OpenFileAction;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.testFramework.LightVirtualFile;
import consulo.annotations.RequiredDispatchThread;

/**
 * @author VISTALL
 * @since 31.05.2015
 */
public class NewProfileSessionAction extends AnAction
{
	public NewProfileSessionAction()
	{
		super("New Profile Session", null, AllIcons.Actions.Preview);
	}

	@RequiredDispatchThread
	@Override
	public void actionPerformed(AnActionEvent e)
	{
		int i = new Random().nextInt(Short.MAX_VALUE);
		LightVirtualFile virtualFile = new LightVirtualFile("Session: " + i, XProfilerSessionFileType.INSTANCE, "");

		OpenFileAction.openFile(virtualFile, e.getProject());
	}
}
