
package org.jpab;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.HashMap;
/**
 * The HostAPI class represents a possible choice as to which underlying audio
 * API your streams will utilize.
 * 
 * 
 * @author Ryan Holdren
 */
public class HostAPI implements PortAudio.Component {
  public class Type {
    private static final HashMap<Integer, HostAPI.Type> map =  new HashMap <Integer, Type> ();

    private static final HashSet<HostAPI.Type> set =  new HashSet <Type> ();

    private static final Set<HostAPI.Type> view =  Collections.unmodifiableSet(set);

    public static final HostAPI.Type IN_DEVELOPEMENT =  new Type("Under Development", 0);

    public static final HostAPI.Type DIRECT_SOUND =  new Type("DirectSound", 1);

    public static final HostAPI.Type MME =  new Type("MultiMedia Extensions", 2);

    public static final HostAPI.Type ASIO =  new Type("ASIO", 3);

    public static final HostAPI.Type SOUND_MANAGER =  new Type("Sound Manager", 4);

    public static final HostAPI.Type CORE_AUDIO =  new Type("Core Audio", 5);

    public static final HostAPI.Type OSS =  new Type("Open Sound System", 7);

    public static final HostAPI.Type ALSA =  new Type("Advanced Linux Sound Architecture", 8);

    public static final HostAPI.Type AL =  new Type("?", 9);

    public static final HostAPI.Type BEOS =  new Type("BeOS Media Kit", 10);

    public static final HostAPI.Type WDMKS =  new Type("WDM-KS", 11);

    public static final HostAPI.Type JACK =  new Type("JACK Audio Connection Kit", 12);

    public static final HostAPI.Type WASAPI =  new Type("Windows Audio Session API", 13);

    public static final HostAPI.Type AUDIO_SCIENCE_HPI =  new Type("Audio Science HPI", 14);

    public static Set<HostAPI.Type> values()
    {
			return view;
    }

    public static HostAPI.Type resolve(int code)
    {
			return map.get(code);
    }

    private final int code;

    private final String name;

    /**
     * The constructor automatically adds the new Type to the set so that
     * it can be used just as the constant Types are.
     * 
     * @param name The name of the API.
     * @param code The code that will be passed to PortAudio.
     */
    public Type(String name, int code) {
			if (map.containsValue(code)) throw new IllegalArgumentException();
			this.name = name;
			this.code = code;
			map.put(code, this);
			set.add(this);
    }

    public long getCode() {
			return code;
    }

    public String getName() {
			return name;
    }

    public String toString() {
			return name;
    }

  }

  private final int defaultInputDevice;

  private final int defaultOutputDevice;

  private final int deviceCount;

  private final int id;

  private final String name;

  private final HostAPI.Type type;

  protected HostAPI(ByteBuffer data) {
		defaultInputDevice = data.get();
		defaultOutputDevice = data.get();
		deviceCount = data.get();
		id = data.get();
		type = Type.resolve(data.get());
		final byte[] bytes = new byte[data.get()];
		data.get(bytes);
		this.name = new String(bytes);
  }

  public Device getDefaultInputDevice() throws PortAudioException {
		return PortAudio.getDevice(defaultInputDevice);
  }

  public Device getDefaultOutputDevice() throws PortAudioException {
		return PortAudio.getDevice(defaultOutputDevice);
  }

  public int getDeviceCount() {
		return deviceCount;
  }

  public List<Device> getDevices() throws PortAudioException {
		ArrayList <Device> devices = new ArrayList <Device> ();
		ByteBuffer data = PortAudio.getHostAPIsDevicesAsBuffer(id);
		data.order(ByteOrder.nativeOrder());
		try {
			while (data.remaining() > 0)
				devices.add(new Device(data));
		} finally {
			PortAudio.free(data);
		}
		return devices;
  }

  public String getName() {
		return name;
  }

  public HostAPI.Type getType() {
		return type;
  }

  public String toString() {
		return "Port Audio Host API {\n\tDefault Input Device ID: " + defaultInputDevice +
		"\n\tDefault Output Device ID: " + defaultOutputDevice +
		"\n\tDevice Count: " + deviceCount +
		"\n\tID: " + id +
		"\n\tName: " + name +
		"\n\tType: " + type + "\n}";
  }

  protected int getID() {
		return id;
  }

}
