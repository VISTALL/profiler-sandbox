package net.sf.profiler4j.console.client;

import java.util.Comparator;

/**
 * @author VISTALL
 * @since 03.06.2015
 */
public class MethodComparators
{
	public static final Comparator<Snapshot.Method> byNetTimeComparator = new Comparator<Snapshot.Method>()
	{
		public int compare(Snapshot.Method g1, Snapshot.Method g2)
		{
			return (int) -Math.signum(g1.getNetTime() - g2.getNetTime());
		}

		@Override
		public String toString()
		{
			return "BY_TOTAL_TIME";
		}
	};

	public static final Comparator<Snapshot.Method> bySelfTimeComparator = new Comparator<Snapshot.Method>()
	{
		public int compare(Snapshot.Method g1, Snapshot.Method g2)
		{
			return (int) -Math.signum(g1.getSelfTime() - g2.getSelfTime());
		}

		@Override
		public String toString()
		{
			return "BY_TOTAL_LOCAL_TIME";
		}
	};

	public static final Comparator<Snapshot.Method> byHitsComparator = new Comparator<Snapshot.Method>()
	{
		public int compare(Snapshot.Method g1, Snapshot.Method g2)
		{
			return -compareLong(g1.getHits(), g2.getHits());
		}

		@Override
		public String toString()
		{
			return "BY_HITS";
		}
	};

	private static int compareLong(long l1, long l2)
	{
		return (l1 > l2) ? 1 : ((l1 < l2) ? -1 : 0);
	}
}
