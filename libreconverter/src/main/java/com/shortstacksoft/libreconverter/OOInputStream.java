package com.shortstacksoft.libreconverter;

import java.io.InputStream;

import com.sun.star.io.BufferSizeExceededException;
import com.sun.star.io.IOException;
import com.sun.star.io.NotConnectedException;
import com.sun.star.io.XInputStream;

public class OOInputStream implements XInputStream {
	
	private InputStream input;
	
	public OOInputStream ( InputStream input )
	{
		this.input = input;
	}

	public int available() throws NotConnectedException, IOException {
		try {
			return input.available();
		} catch (java.io.IOException e) {
			e.printStackTrace();
			throw new IOException ( e.getMessage() );
		}
	}

	public void closeInput() throws NotConnectedException, IOException {
		try {
			input.close();
		} catch (java.io.IOException e) {
			e.printStackTrace();
			throw new IOException ( e.getMessage() );
		}
	
	}

	public int readBytes(byte[][] p1, int p2) throws NotConnectedException,
			BufferSizeExceededException, IOException {
		try {
			byte[] b = new byte[p2];
			int res = input.read(b);
			if (res > 0) {
				if (res < p2) {
					byte[] b2 = new byte[res];
					System.arraycopy(b, 0, b2, 0, res);
					b = b2;
				}
			} else {
				b = new byte[0];
				res = 0;
			}
			p1[0] = b;
			return res;
		} catch (java.io.IOException e) {
			e.printStackTrace();
			throw new com.sun.star.io.IOException(e.getMessage(), this);
		}
	}

	public int readSomeBytes(byte[][] p1, int p2)
			throws NotConnectedException, BufferSizeExceededException,
			IOException {
		return readBytes(p1,p2);
	}

	public void skipBytes(int arg0) throws NotConnectedException,
			BufferSizeExceededException, IOException {
		try {
			input.skip(arg0);
		} catch (java.io.IOException e) {
			e.printStackTrace();
			throw new IOException ( e.getMessage() );
		}
	}

}
