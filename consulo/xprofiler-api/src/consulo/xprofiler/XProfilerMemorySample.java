package consulo.xprofiler;

/**
 * @author VISTALL
 * @since 03.06.2015
 */
public class XProfilerMemorySample
{

	double totalMem;
	double usedMem;

	public XProfilerMemorySample(double totalMem, double usedMem)
	{
		this.totalMem = totalMem;
		this.usedMem = usedMem;
	}

	public double getTotalMem()
	{
		return totalMem;
	}

	public double getUsedMem()
	{
		return usedMem;
	}
}
