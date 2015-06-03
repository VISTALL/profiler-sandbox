package net.sf.profiler4j.agent;

/**
 * @author VISTALL
 * @since 03.06.2015
 */
public interface SnapshotConstants
{
	/**
	 * Magic number that identifies snapshots created by Profiler4j. This should never
	 * change.
	 */
	public static final int SNAPSHOT_MAGIC = 0xbabaca00;
	/**
	 * Current version of the protocol. Different versions may have nothing in common, so
	 * there is no assumption of backwards compatiblity. This is expected to change only
	 * in drastic cases.
	 */
	public static final int SNAPSHOT_PROTOCOL_VERSION = 0x00000001;
	/**
	 * Type of snaphot that contains the statistics of methods timings. Currently this is
	 * the only type supported.
	 */
	public static final int SNAPSHOT_TYPE_CALLTRACE = 0x00000001;
}
