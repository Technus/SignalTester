package com.github.technus.signalTester.plugin.mccUniversalLibrary.jna;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.List;
/**
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> , <a href="http://rococoa.dev.java.net/">Rococoa</a>, or <a href="http://jna.dev.java.net/">JNA</a>.
 */
public class DaqDeviceDescriptor extends Structure {
	/** C type : CHAR[64] */
	public byte[] ProductName = new byte[64];
	/** product ID */
	public int ProductID;
	/**
	 * @see DaqDeviceInterface <br>
	 * USB, BLUETOOTH, ...<br>
	 * C type : DaqDeviceInterface
	 */
	public int InterfaceType;
	/** C type : CHAR[64] */
	public byte[] DevString = new byte[64];
	/**
	 * unique identifier for device. Serial number for USB deivces and MAC address for  bth and net devices<br>
	 * C type : CHAR[64]
	 */
	public byte[] UniqueID = new byte[64];
	/** numeric representation of uniqueID */
	public long NUID;
	/**
	 * reserved for the future.<br>
	 * C type : CHAR[512]
	 */
	public byte[] Reserved = new byte[512];
	public DaqDeviceDescriptor() {
		super();
	}
	protected List<String> getFieldOrder() {
		return Arrays.asList("ProductName", "ProductID", "InterfaceType", "DevString", "UniqueID", "NUID", "Reserved");
	}
	/**
	 * @param ProductName C type : CHAR[64]<br>
	 * @param ProductID product ID<br>
	 * @param InterfaceType @see DaqDeviceInterface<br>
	 * USB, BLUETOOTH, ...<br>
	 * C type : DaqDeviceInterface<br>
	 * @param DevString C type : CHAR[64]<br>
	 * @param UniqueID unique identifier for device. Serial number for USB deivces and MAC address for  bth and net devices<br>
	 * C type : CHAR[64]<br>
	 * @param NUID numeric representation of uniqueID<br>
	 * @param Reserved reserved for the future.<br>
	 * C type : CHAR[512]
	 */
	public DaqDeviceDescriptor(byte[] ProductName, int ProductID, int InterfaceType, byte[] DevString, byte[] UniqueID, long NUID, byte[] Reserved) {
		super();
		if ((ProductName.length != this.ProductName.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.ProductName = ProductName;
		this.ProductID = ProductID;
		this.InterfaceType = InterfaceType;
		if ((DevString.length != this.DevString.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.DevString = DevString;
		if ((UniqueID.length != this.UniqueID.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.UniqueID = UniqueID;
		this.NUID = NUID;
		if ((Reserved.length != this.Reserved.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.Reserved = Reserved;
	}
	public DaqDeviceDescriptor(Pointer peer) {
		super(peer);
	}

	public static class ByReference extends DaqDeviceDescriptor implements Structure.ByReference {
		
	}

	public static class ByValue extends DaqDeviceDescriptor implements Structure.ByValue {
		
	}

	/** enum values */
	public interface DaqDeviceInterface {
		/** <i>native declaration : line 1510</i> */
		int USB_IFC = 1 << 0;
		/** <i>native declaration : line 1511</i> */
		int BLUETOOTH_IFC = 1 << 1;
		/** <i>native declaration : line 1512</i> */
		int ETHERNET_IFC = 1 << 2;
		/** <i>native declaration : line 1513</i> */
		int ANY_IFC = DaqDeviceInterface.USB_IFC | DaqDeviceInterface.BLUETOOTH_IFC | DaqDeviceInterface.ETHERNET_IFC;
	}

	static DaqDeviceDescriptor[] fromArrayPointer(Pointer pointer, int numberResults) {
		DaqDeviceDescriptor[] arr = new DaqDeviceDescriptor[numberResults];
		int offset = 0;
		for (int i = 0; i < numberResults; i++) {
			arr[i] = new DaqDeviceDescriptor(pointer.share(offset));
			offset += arr[i].size();
		}
		return arr;
	}
}
