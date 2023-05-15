package oldmana.md.server;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class ModClassLoader extends URLClassLoader
{
	private ModLoader loader;
	
	public ModClassLoader(ModLoader loader, File jarFile, ClassLoader parent) throws MalformedURLException
	{
		super(new URL[] {new URL("jar:file:" + jarFile.getPath() + "!/")}, parent);
		this.loader = loader;
	}
	
	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException
	{
		return loadClass0(name, resolve, true);
	}
	
	public Class<?> loadClass0(String name, boolean resolve, boolean checkOtherMods) throws ClassNotFoundException
	{
		try
		{
			return super.loadClass(name, resolve);
		}
		catch (ClassNotFoundException e) {}
		if (checkOtherMods)
		{
			Class<?> clazz = loader.loadClass(name, resolve);
			if (clazz != null)
			{
				return clazz;
			}
		}
		throw new ClassNotFoundException(name);
	}
}
