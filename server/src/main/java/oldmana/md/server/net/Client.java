package oldmana.md.server.net;

import oldmana.general.mjnetworkingapi.packet.Packet;

import java.util.List;
import java.util.function.Consumer;

public interface Client
{
	void addInPacket(Packet packet);
	
	List<Packet> getInPackets();
	
	void addOutPacket(Packet packet);
	
	List<Packet> getOutPackets();
	
	boolean hasOutPackets();
	
	String getHostAddress();
	
	boolean isConnected();
	
	void closeConnection();
	
	void setCloseHandler(Consumer<Throwable> handler);
}
