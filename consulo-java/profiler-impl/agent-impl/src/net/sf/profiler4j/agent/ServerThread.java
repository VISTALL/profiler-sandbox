package net.sf.profiler4j.agent;

import java.lang.management.MemoryUsage;
import java.lang.management.ThreadInfo;
import java.util.ArrayList;
import java.util.List;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.mustbe.consulo.profiler.TClassInfo;
import org.mustbe.consulo.profiler.TMemoryInfo;
import org.mustbe.consulo.profiler.TMemoryUsage;
import org.mustbe.consulo.profiler.TProfilerService;
import org.mustbe.consulo.profiler.TStackTraceElement;
import org.mustbe.consulo.profiler.TThreadInfo;

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
		arguments.processor(new TProfilerService.Processor<TProfilerService.Iface>(new TProfilerService.Iface()
		{
			@Override
			public void gc() throws TException
			{
				Log.print(0, "gc");
				System.gc();
			}

			@Override
			public TMemoryInfo memoryInfo() throws TException
			{
				Log.print(0, "memoryInfo");
				MemoryUsage heapMemoryUsage = Agent.membean.getHeapMemoryUsage();
				MemoryUsage nonHeapMemoryUsage = Agent.membean.getNonHeapMemoryUsage();

				return new TMemoryInfo(convert(heapMemoryUsage), convert(nonHeapMemoryUsage), Agent.membean.getObjectPendingFinalizationCount());
			}

			private TMemoryUsage convert(MemoryUsage memoryUsage)
			{
				return new TMemoryUsage(memoryUsage.getInit(), memoryUsage.getUsed(), memoryUsage.getCommitted(), memoryUsage.getMax());
			}

			@Override
			public List<TClassInfo> classes() throws TException
			{
				Log.print(0, "classes");
				Class[] classes = Agent.getLoadedClasses(true);
				List<TClassInfo> classInfoList = new ArrayList<TClassInfo>(classes.length);
				for(Class aClass : classes)
				{
					classInfoList.add(new TClassInfo(aClass.getName(), BytecodeTransformer.list.contains(aClass.getName())));
				}
				return classInfoList;
			}

			@Override
			public List<TThreadInfo> threads() throws TException
			{
				long[] allThreadIds = Agent.threadbean.getAllThreadIds();
				java.lang.management.ThreadInfo[] threadInfo = Agent.threadbean.getThreadInfo(allThreadIds, Integer.MAX_VALUE);

				List<TThreadInfo> list = new ArrayList<TThreadInfo>(threadInfo.length);
				for(ThreadInfo info : threadInfo)
				{
					StackTraceElement[] stackTrace = info.getStackTrace();
					List<TStackTraceElement> stackTraceElements = new ArrayList<TStackTraceElement>(stackTrace.length);
					for(StackTraceElement it : stackTrace)
					{
						stackTraceElements.add(new TStackTraceElement(it.getFileName(), it.getClassName(), it.getMethodName(), it.getLineNumber()));
					}
					list.add(new TThreadInfo(info.getThreadId(), info.getThreadName(), info.getThreadState().ordinal(), stackTraceElements));
				}
				return list;
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
