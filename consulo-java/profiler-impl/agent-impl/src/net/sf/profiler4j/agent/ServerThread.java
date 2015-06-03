package net.sf.profiler4j.agent;

import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.List;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.mustbe.consulo.profiler.ClassInfo;
import org.mustbe.consulo.profiler.MemoryInfo;
import org.mustbe.consulo.profiler.ProfilerService;

/**
 * @author VISTALL
 * @since 03.06.2015
 */
public class ServerThread extends Thread
{
	private TServer myServer;
	private Config myConfig;

	public ServerThread(Config config) throws Exception
	{
		myConfig = config;
		TServerSocket tServerSocket = new TServerSocket(config.getPort());
		TServer.Args arguments = new TServer.Args(tServerSocket);
		arguments.protocolFactory(new TBinaryProtocol.Factory());
		arguments.processor(new ProfilerService.Processor<ProfilerService.Iface>(new ProfilerService.Iface()
		{
			@Override
			public void gc() throws TException
			{
				Log.print(0, "gc");
				System.gc();
			}

			@Override
			public MemoryInfo memoryInfo() throws TException
			{
				Log.print(0, "memoryInfo");
				MemoryUsage heapMemoryUsage = Agent.membean.getHeapMemoryUsage();
				MemoryUsage nonHeapMemoryUsage = Agent.membean.getNonHeapMemoryUsage();

				return new MemoryInfo(convert(heapMemoryUsage), convert(nonHeapMemoryUsage), Agent.membean.getObjectPendingFinalizationCount());
			}

			private org.mustbe.consulo.profiler.MemoryUsage convert(MemoryUsage memoryUsage)
			{
				return new org.mustbe.consulo.profiler.MemoryUsage(memoryUsage.getInit(), memoryUsage.getUsed(), memoryUsage.getCommitted(),
						memoryUsage.getMax());
			}

			@Override
			public List<ClassInfo> classes() throws TException
			{
				Log.print(0, "classes");
				Class[] classes = Agent.getLoadedClasses(true);
				List<ClassInfo> classInfoList = new ArrayList<ClassInfo>(classes.length);
				for(Class aClass : classes)
				{
					classInfoList.add(new ClassInfo(aClass.getName(), BytecodeTransformer.list.contains(aClass.getName())));
				}
				return classInfoList;
			}
		}));

		myServer = new TSimpleServer(arguments);
	}

	@Override
	public void run()
	{
		Log.print(0, "serve: " + myConfig.getPort());
		myServer.serve();
	}
}
