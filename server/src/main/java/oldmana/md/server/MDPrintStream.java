package oldmana.md.server;

import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MDPrintStream extends PrintStream
{
	private PrintStream wrapped;
	private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
	
	public MDPrintStream(PrintStream out)
	{
		super(out);
		wrapped = out;
	}
	
	@Override
	public void println(String str)
	{
		wrapped.println(getDatePrefix() + str);
	}
	
	@Override
	public void println(boolean b)
	{
		println(String.valueOf(b));
	}
	
	@Override
	public void println(int i)
	{
		println(String.valueOf(i));
	}
	
	@Override
	public void println(long l)
	{
		println(String.valueOf(l));
	}
	
	@Override
	public void println(double d)
	{
		println(String.valueOf(d));
	}
	
	@Override
	public void println(char c)
	{
		println(String.valueOf(c));
	}
	
	@Override
	public void println(Object o)
	{
		println(String.valueOf(o));
	}
	
	private String getDatePrefix()
	{
		return "[" + timeFormatter.format(LocalDateTime.now()) + "] ";
	}
}
