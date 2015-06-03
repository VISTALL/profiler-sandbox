namespace java org.mustbe.consulo.profiler

struct ClassInfo
{
	1:string name;

	2:bool instrumented;
}

struct MemoryUsage
{
	1:i64 init;
	2:i64 used;
	3:i64 committed;
	4:i64 max;
}

struct MemoryInfo
{
	1:MemoryUsage heap;
	2:MemoryUsage nonHeap;
	3:i32 objectPendingFinalizationCount;
}

service ProfilerService
{
	void gc();

	MemoryInfo memoryInfo();

	list<ClassInfo> classes();
}