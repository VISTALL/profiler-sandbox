package consulo.xprofiler;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.util.Key;
import com.intellij.ui.SimpleColoredComponent;

/**
 * @author VISTALL
 * @since 04.06.2015
 */
public interface XProfilerThreadPanelProvider<T>
{
	@NotNull
	Key<List<T>> getFetchKey();

	void renderItem(@NotNull SimpleColoredComponent coloredComponent, @NotNull T item);
}
