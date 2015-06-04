package org.mustbe.consulo.xprofiler.ui;

import java.awt.CardLayout;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.xprofiler.XProfilerSession;
import org.mustbe.consulo.xprofiler.ui.attachPanel.XProfilerAttachSessionPanel;
import org.mustbe.consulo.xprofiler.ui.mainPanel.XProfilerMainPanel;
import com.intellij.codeHighlighting.BackgroundEditorHighlighter;
import com.intellij.concurrency.ResultConsumer;
import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.fileEditor.FileEditorStateLevel;
import com.intellij.openapi.project.PossiblyDumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ui.UIUtil;

/**
 * @author VISTALL
 * @since 31.05.2015
 */
public class XProfilerSessionFileEditor extends UserDataHolderBase implements FileEditor, PossiblyDumbAware
{
	private JPanel myRootPanel;

	public XProfilerSessionFileEditor(final Project project, VirtualFile virtualFile)
	{
		final CardLayout cardLayout = new CardLayout();
		myRootPanel = new JPanel(cardLayout);

		final XProfilerMainPanel mainPanel = new XProfilerMainPanel(project);
		XProfilerAttachSessionPanel attachSessionPanel = new XProfilerAttachSessionPanel(project, new ResultConsumer<XProfilerSession>()
		{
			@Override
			public void onSuccess(final XProfilerSession value)
			{
				Disposer.register(XProfilerSessionFileEditor.this, value);

				UIUtil.invokeLaterIfNeeded(new Runnable()
				{
					@Override
					public void run()
					{
						mainPanel.init(value);
						cardLayout.show(myRootPanel, "main");
					}
				});
			}

			@Override
			public void onFailure(final Throwable t)
			{
				UIUtil.invokeLaterIfNeeded(new Runnable()
				{
					@Override
					public void run()
					{
						Messages.showErrorDialog(project, t.getMessage(), "Attach Failed");
					}
				});
			}
		});

		myRootPanel.add(attachSessionPanel, "attach");
		myRootPanel.add(mainPanel, "main");

		Disposer.register(this, attachSessionPanel);
		Disposer.register(this, mainPanel);
	}

	@NotNull
	@Override
	public JComponent getComponent()
	{
		return myRootPanel;
	}

	@Nullable
	@Override
	public JComponent getPreferredFocusedComponent()
	{
		return null;
	}

	@NotNull
	@Override
	public String getName()
	{
		return "";
	}

	@NotNull
	@Override
	public FileEditorState getState(@NotNull FileEditorStateLevel fileEditorStateLevel)
	{
		return FileEditorState.INSTANCE;
	}

	@Override
	public void setState(@NotNull FileEditorState fileEditorState)
	{

	}

	@Override
	public boolean isModified()
	{
		return false;
	}

	@Override
	public boolean isValid()
	{
		return true;
	}

	@Override
	public void selectNotify()
	{

	}

	@Override
	public void deselectNotify()
	{

	}

	@Override
	public void addPropertyChangeListener(@NotNull PropertyChangeListener propertyChangeListener)
	{

	}

	@Override
	public void removePropertyChangeListener(@NotNull PropertyChangeListener propertyChangeListener)
	{

	}

	@Nullable
	@Override
	public BackgroundEditorHighlighter getBackgroundHighlighter()
	{
		return null;
	}

	@Nullable
	@Override
	public FileEditorLocation getCurrentLocation()
	{
		return null;
	}

	@Nullable
	@Override
	public StructureViewBuilder getStructureViewBuilder()
	{
		return null;
	}

	@Nullable
	@Override
	public VirtualFile getVirtualFile()
	{
		return null;
	}

	@Override
	public void dispose()
	{
		Disposer.dispose(this);
	}

	@Override
	public boolean isDumbAware()
	{
		return true;
	}
}
