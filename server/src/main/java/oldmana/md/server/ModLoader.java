package oldmana.md.server;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ModLoader
{
	private List<MDMod> mods = new ArrayList<MDMod>();
	private List<ModClassLoader> classLoaders = new CopyOnWriteArrayList<ModClassLoader>();
	
	public MDMod loadMod(File jarFile)
	{
		try
		{
			List<Class<?>> classes = new ArrayList<Class<?>>();
			ModClassLoader classLoader = new ModClassLoader(this, jarFile, getClass().getClassLoader());
			JarFile jar = new JarFile(jarFile);
			Enumeration<JarEntry> entries = jar.entries();
			while (entries.hasMoreElements())
			{
				JarEntry e = entries.nextElement();
				if (e.isDirectory() || !e.getName().endsWith(".class"))
				{
					continue;
				}
				String className = e.getName().substring(0, e.getName().length() - 6).replace('/', '.');
				Class<?> clazz = classLoader.loadClass(className);
				classes.add(clazz);
			}
			jar.close();
			boolean hasModClass = false;
			MDMod mod = null;
			for (Class<?> clazz : classes)
			{
				if (MDMod.class.isAssignableFrom(clazz))
				{
					mod = (MDMod) clazz.newInstance();
					System.out.println("Loading Mod: " + mod.getName());
					mods.add(mod);
					classLoaders.add(classLoader);
					mod.onLoad();
					hasModClass = true;
					break;
				}
			}
			if (!hasModClass)
			{
				System.out.println(jarFile.getName() + " is missing a mod class!");
				return null;
			}
			return mod;
		}
		catch (Exception e)
		{
			System.out.println("Error loading mod file " + jarFile.getName());
		}
		return null;
	}
	
	public List<MDMod> getMods()
	{
		return Collections.unmodifiableList(mods);
	}
	
	public Class<?> loadClass(String name, boolean resolve)
	{
		for (ModClassLoader loader : classLoaders)
		{
			try
			{
				return loader.loadClass0(name, resolve, false);
			}
			catch (ClassNotFoundException e) {}
		}
		return null;
	}
}
