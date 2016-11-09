package consulo.xprofiler.file;

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.fileTypes.UIBasedFileType;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * @author VISTALL
 * @since 31.05.2015
 */
public class XProfilerSessionFileType implements UIBasedFileType
{
	public static final XProfilerSessionFileType INSTANCE = new XProfilerSessionFileType();

	@NotNull
	@Override
	public String getName()
	{
		return "XPROFILER_FILE";
	}

	@NotNull
	@Override
	public String getDescription()
	{
		return "xprofiler file";
	}

	@NotNull
	@Override
	public String getDefaultExtension()
	{
		return "";
	}

	@Nullable
	@Override
	public Icon getIcon()
	{
		return AllIcons.Actions.Preview;
	}

	@Override
	public boolean isBinary()
	{
		return true;
	}

	@Override
	public boolean isReadOnly()
	{
		return true;
	}

	@Nullable
	@Override
	public String getCharset(@NotNull VirtualFile file, byte[] content)
	{
		return null;
	}
}
