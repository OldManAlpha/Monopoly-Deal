package oldmana.md.server.mod;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ModLoader
{
	private List<ServerMod> mods = new ArrayList<ServerMod>();
	private List<ModClassLoader> classLoaders = new CopyOnWriteArrayList<ModClassLoader>();
	
	public void loadMods(File modsFolder)
	{
		List<ModInfo> modInfos = new ArrayList<ModInfo>();
		for (File file : modsFolder.listFiles())
		{
			if (file.isFile() && file.getName().endsWith(".jar"))
			{
				try
				{
					ModInfo info = getModInfo(file);
					if (info != null)
					{
						info.checkValidity();
						modInfos.add(info);
					}
				}
				catch (Exception e)
				{
					System.out.println("Error loading " + file.getName() + ":");
					e.printStackTrace();
				}
			}
		}
		
		modInfos.sort((m1, m2) -> m2.dependsOn(m1) ? -1 : 0);
		for (ModInfo info : modInfos)
		{
			if (!checkDependencies(info))
			{
				continue;
			}
			loadMod(info);
		}
		
		List<ServerMod> mods = new ArrayList<ServerMod>(this.mods);
		mods.sort((m1, m2) -> m2.dependsOn(m1) ? -1 : 0);
		
		for (ServerMod mod : mods)
		{
			try
			{
				mod.onEnable();
			}
			catch (Exception | Error e)
			{
				System.err.println("Error while enabling " + mod.getName());
				e.printStackTrace();
			}
		}
	}
	
	public ModInfo getModInfo(File jarFile)
	{
		try (JarFile jar = new JarFile(jarFile))
		{
			JarEntry modDesc = jar.getJarEntry("mod.json");
			if (modDesc == null)
			{
				jar.close();
				System.out.println(jarFile.getName() + " does not contain a mod.json file!");
				return null;
			}
			try (InputStream is = jar.getInputStream(modDesc))
			{
				JSONObject obj = new JSONObject(new JSONTokener(is));
				return new ModInfo(jarFile, obj.getString("mainClass"), obj.getString("name"), obj.getString("version"),
						obj.has("dependencies") ? obj.getJSONArray("dependencies").toStringList() : null,
						obj.has("softDependencies") ? obj.getJSONArray("softDependencies").toStringList() : null);
			}
			catch (Exception e)
			{
				System.err.println("Error reading mod.json in " + jarFile.getName());
				e.printStackTrace();
				return null;
			}
		}
		catch (Exception e)
		{
			System.out.println("Error loading " + jarFile.getName() + ":");
			e.printStackTrace();
		}
		return null;
	}
	
	private ServerMod loadMod(ModInfo info)
	{
		File jarFile = info.jarFile;
		try
		{
			ModClassLoader classLoader = new ModClassLoader(this, jarFile, getClass().getClassLoader());
			
			String mainClassPath = info.mainClassPath;
			String modName = info.name;
			String modVersion = info.version;
			
			Class<?> mainClass = classLoader.loadClass(mainClassPath);
			if (!ServerMod.class.isAssignableFrom(mainClass))
			{
				System.out.println("Error loading " + jarFile.getName() + ": " + mainClassPath +
						" is not an instance of ServerMod!");
				return null;
			}
			ServerMod mod = (ServerMod) mainClass.newInstance();
			mod.setName(modName);
			mod.setVersion(modVersion);
			mod.setDependencies(new HashSet<String>(info.dependencies));
			mod.setSoftDependencies(new HashSet<String>(info.softDependencies));
			mod.generateModRule();
			mods.add(mod);
			classLoaders.add(classLoader);
			System.out.println("Loading Mod: " + mod.getName() + " Version " + mod.getVersion());
			mod.onLoad();
			return mod;
		}
		catch (Exception | Error e)
		{
			System.err.println("Error loading " + jarFile.getName() + ":");
			e.printStackTrace();
		}
		return null;
	}
	
	public ServerMod loadMod(File file)
	{
		ModInfo info = getModInfo(file);
		if (info == null)
		{
			return null;
		}
		info.checkValidity();
		if (!checkDependencies(info))
		{
			return null;
		}
		ServerMod mod = loadMod(info);
		if (mod == null)
		{
			return null;
		}
		try
		{
			mod.onEnable();
		}
		catch (Exception | Error e)
		{
			System.err.println("Error while enabling " + mod.getName());
			e.printStackTrace();
		}
		return mod;
	}
	
	private boolean checkDependencies(ModInfo info)
	{
		String missingDependency = findMissingDependency(info.dependencies);
		if (missingDependency != null)
		{
			System.out.println("Could not load " + info.name + " Version " + info.version +
					": Missing dependency (" + missingDependency + ")");
			System.out.println("Please install this missing dependency and restart the server.");
			return false;
		}
		return true;
	}
	
	/**
	 * Returns the first missing dependency found.
	 * @param dependencies The list of mods to check
	 * @return The first missing dependency, or null if all are loaded
	 */
	private String findMissingDependency(List<String> dependencies)
	{
		for (String dependency : dependencies)
		{
			if (!isModLoaded(dependency))
			{
				return dependency;
			}
		}
		return null;
	}
	
	public List<ServerMod> getMods()
	{
		return Collections.unmodifiableList(mods);
	}
	
	public ServerMod getModByName(String name)
	{
		for (ServerMod mod : mods)
		{
			if (mod.getName().equals(name))
			{
				return mod;
			}
		}
		return null;
	}
	
	public boolean isModLoaded(String name)
	{
		return getModByName(name) != null;
	}
	
	public Class<?> loadClass(String name, boolean resolve)
	{
		for (ModClassLoader loader : classLoaders)
		{
			try
			{
				return loader.loadClass(name, resolve, false);
			}
			catch (ClassNotFoundException e) {}
		}
		return null;
	}
	
	
	public static class ModInfo
	{
		public File jarFile;
		public String mainClassPath;
		public String name;
		public String version;
		public List<String> dependencies;
		public List<String> softDependencies;
		
		public ModInfo(File jarFile, String mainClassPath, String name, String version, List<String> dependencies, List<String> softDependencies)
		{
			this.jarFile = jarFile;
			this.mainClassPath = mainClassPath;
			this.name = name;
			this.version = version;
			this.dependencies = dependencies != null ? dependencies : Collections.emptyList();
			this.softDependencies = softDependencies != null ? softDependencies : Collections.emptyList();
		}
		
		public boolean dependsOn(ModInfo other)
		{
			return dependencies.contains(other.name) || softDependencies.contains(other.name);
		}
		
		public void checkValidity()
		{
			if (name.contains(" ") || name.contains("."))
			{
				throw new RuntimeException("Mod name cannot contain spaces or periods. Offender: \"" + name + "\"");
			}
		}
	}
}
