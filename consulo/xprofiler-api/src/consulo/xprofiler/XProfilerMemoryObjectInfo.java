package consulo.xprofiler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.project.Project;
import com.intellij.util.PairConsumer;

/**
 * @author VISTALL
 * @since 31.05.2015
 */
public class XProfilerMemoryObjectInfo
{
	@NotNull
	private String myType;
	private int myCount;

	private PairConsumer<Project, XProfilerMemoryObjectInfo> myNavigatable;

	public XProfilerMemoryObjectInfo(@NotNull String type, int count, @Nullable PairConsumer<Project, XProfilerMemoryObjectInfo> navigatable)
	{
		myType = type;
		myCount = count;
		myNavigatable = navigatable;
	}

	public PairConsumer<Project, XProfilerMemoryObjectInfo> getNavigatable()
	{
		return myNavigatable;
	}

	@NotNull
	public String getType()
	{
		return myType;
	}

	public int getCount()
	{
		return myCount;
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o)
		{
			return true;
		}
		if(o == null || getClass() != o.getClass())
		{
			return false;
		}

		XProfilerMemoryObjectInfo that = (XProfilerMemoryObjectInfo) o;

		if(!myType.equals(that.myType))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return myType.hashCode();
	}
}
