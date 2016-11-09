package consulo.xprofiler.file;

import org.jetbrains.annotations.NotNull;
import com.intellij.lang.Language;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.FileViewProviderFactory;
import com.intellij.psi.PsiManager;

/**
 * @author VISTALL
 * @since 31.05.2015
 */
public class XProfilerFileViewProviderFactory implements FileViewProviderFactory
{
	@Override
	public FileViewProvider createFileViewProvider(@NotNull VirtualFile file,
			Language language,
			@NotNull PsiManager manager,
			boolean eventSystemEnabled)
	{
		if(file.getFileType() == XProfilerSessionFileType.INSTANCE)
		{
			return new XProfilerSessionFileViewProvider(manager, file);
		}
		return null;
	}
}
