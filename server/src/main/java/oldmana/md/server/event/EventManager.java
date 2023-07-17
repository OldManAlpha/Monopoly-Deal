package oldmana.md.server.event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class EventManager
{
	// Mappings: EventListener -> (Event Class -> List of Methods)
	private Map<EventListener, Map<Class<? extends Event>, List<Method>>> listenerMap = new HashMap<EventListener, Map<Class<? extends Event>, List<Method>>>();
	
	public void registerEvents(EventListener listener)
	{
		Map<Class<? extends Event>, List<Method>> eventMap = new HashMap<Class<? extends Event>, List<Method>>();
		for (Method m : listener.getClass().getMethods())
		{
			if (m.isAnnotationPresent(EventHandler.class))
			{
				if (m.getParameterTypes().length == 1 && Event.class.isAssignableFrom(m.getParameterTypes()[0]))
				{
					@SuppressWarnings("unchecked")
					Class<? extends Event> clazz = (Class<? extends Event>) m.getParameterTypes()[0];
					if (!eventMap.containsKey(clazz))
					{
						eventMap.put(clazz, new ArrayList<Method>());
					}
					eventMap.get(clazz).add(m);
				}
			}
		}
		listenerMap.put(listener, eventMap);
	}
	
	public void unregisterEvents(EventListener listener)
	{
		listenerMap.remove(listener);
	}
	
	public void callEvent(Event event)
	{
		Map<EventListener, Map<Class<? extends Event>, List<Method>>> listenerMap = new HashMap<EventListener, Map<Class<? extends Event>,
				List<Method>>>(this.listenerMap);
		for (EventPriority priority : EventPriority.values())
		{
			for (Entry<EventListener, Map<Class<? extends Event>, List<Method>>> listener : listenerMap.entrySet())
			{
				Map<Class<? extends Event>, List<Method>> eventMap = listener.getValue();
				if (eventMap.containsKey(event.getClass()))
				{
					for (Method m : eventMap.get(event.getClass()))
					{
						if (m.getAnnotation(EventHandler.class).priority() == priority)
						{
							try
							{
								m.setAccessible(true);
								m.invoke(listener.getKey(), event);
							}
							catch (Exception | Error e)
							{
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
	}
}
