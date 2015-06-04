namespace java org.mustbe.consulo.profiler

struct TClassInfo
{
	1:string name;

	2:bool instrumented;
}

struct TMemoryUsage
{
	1:i64 init;
	2:i64 used;
	3:i64 committed;
	4:i64 max;
}

struct TMemoryInfo
{
	1:TMemoryUsage heap;
	2:TMemoryUsage nonHeap;
	3:i32 objectPendingFinalizationCount;
}

struct TThreadInfo
{
	1:i64 id;
	2:string name;
	3:i32 state;
	4:list<TStackTraceElement> stackTrace;
}

struct TStackTraceElement
{
	1:string fileName;
	2:string className;
	3:string methodName;
	4:i32 lineNumber;
}

service TProfilerService
{
	void gc();

	TMemoryInfo memoryInfo();

	list<TClassInfo> classes();

	list<TThreadInfo> threads();
}