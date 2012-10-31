package com.shortstacksoft.libreconverter;

import java.io.OutputStream;

import com.sun.star.io.BufferSizeExceededException;
import com.sun.star.io.NotConnectedException;
import com.sun.star.io.XOutputStream;

public class OOOutputStream implements XOutputStream {
	
	private OutputStream output;
	
	public OOOutputStream ( OutputStream output )
	{
		this.output = output;
	}

	public void writeBytes(byte[] values) throws NotConnectedException,
			BufferSizeExceededException, com.sun.star.io.IOException {
		try {
			output.write(values);
		} catch (java.io.IOException ioe) {
			throw (new com.sun.star.io.IOException(ioe.getMessage()));
		}
	}

	public void closeOutput() throws NotConnectedException,
			BufferSizeExceededException, com.sun.star.io.IOException {
		try {
			output.flush();
			output.close();
		} catch (java.io.IOException ioe) {
			throw (new com.sun.star.io.IOException(ioe.getMessage()));
		}
	}

	public void flush() {
		try {
			output.flush();
		} catch (java.io.IOException ignored) {
		}
	}
	
}