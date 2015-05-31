package org.mustbe.consulo.xprofiler.ui;

import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.xprofiler.file.XProfilerSessionFileType;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * @author VISTALL
 * @since 31.05.2015
 */
public class XProfilerSessionEditorProvider implements FileEditorProvider
{
	@Override
	public boolean accept(@NotNull Project project, @NotNull VirtualFile virtualFile)
	{
		return virtualFile.getFileType() == XProfilerSessionFileType.INSTANCE;
	}

	@NotNull
	@Override
	public FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile virtualFile)
	{
		return new XProfilerSessionFileEditor(project, virtualFile);
	}

	@Override
	public void disposeEditor(@NotNull FileEditor fileEditor)
	{

	}

	@NotNull
	@Override
	public FileEditorState readState(@NotNull Element element, @NotNull Project project, @NotNull VirtualFile virtualFile)
	{
		return FileEditorState.INSTANCE;
	}

	@Override
	public void writeState(@NotNull FileEditorState fileEditorState, @NotNull Project project, @NotNull Element element)
	{

	}

	@NotNull
	@Override
	public String getEditorTypeId()
	{
		return "xprofiler.session.editor";
	}

	@NotNull
	@Override
	public FileEditorPolicy getPolicy()
	{
		return FileEditorPolicy.HIDE_DEFAULT_EDITOR;
	}
}
