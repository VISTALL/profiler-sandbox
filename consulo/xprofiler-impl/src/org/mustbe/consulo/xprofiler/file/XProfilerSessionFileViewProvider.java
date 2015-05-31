package org.mustbe.consulo.xprofiler.file;

import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.SingleRootFileViewProvider;

/**
 * @author VISTALL
 * @since 31.05.2015
 */
public class XProfilerSessionFileViewProvider extends SingleRootFileViewProvider
{
	public XProfilerSessionFileViewProvider(@NotNull PsiManager manager, @NotNull VirtualFile file)
	{
		super(manager, file, false);
	}
}
