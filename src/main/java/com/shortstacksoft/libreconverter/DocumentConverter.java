package com.shortstacksoft.libreconverter;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.sun.star.uno.UnoRuntime;

/**
 * The class <CODE>DocumentConverter</CODE> allows you to convert all documents
 * in a given directory and in its subdirectories to a given type. A converted
 * document will be created in the same directory as the origin document.
 * 
 */
public class DocumentConverter {
	com.sun.star.frame.XComponentLoader xCompLoader;

	public DocumentConverter() throws Exception {
		com.sun.star.uno.XComponentContext xContext = null;

		// get the remote office component context
		xContext = com.sun.star.comp.helper.Bootstrap.bootstrap();
		// System.out.println("Connected to a running office ...");

		// get the remote office service manager
		com.sun.star.lang.XMultiComponentFactory xMCF = xContext.getServiceManager();

		Object oDesktop = xMCF.createInstanceWithContext("com.sun.star.frame.Desktop", xContext);

		xCompLoader = (com.sun.star.frame.XComponentLoader) UnoRuntime.queryInterface(com.sun.star.frame.XComponentLoader.class, oDesktop);

	}

	public void exportPdf(InputStream input, OutputStream output) throws Exception {
		exportDocument(input, output, "writer_pdf_Export");
	}

	void exportDocument(InputStream input, OutputStream output, String filter) throws Exception {

		// write the input stream to a temp file because that's a lot faster
		// that using a stream
		// Create temp file.
		File temp = File.createTempFile("aardvark", ".tmp");

		// Delete temp file when program exits.
		temp.deleteOnExit();

		// Write to temp file
		{
			FileOutputStream out = new FileOutputStream(temp);
			InputStream inputFile = new BufferedInputStream(input);
			byte[] byteBuffer = new byte[4096];
			int byteBufferLength = 0;
			while ((byteBufferLength = inputFile.read(byteBuffer)) > 0) {
				out.write(byteBuffer, 0, byteBufferLength);
			}
			inputFile.close();
			out.close();
		}

		// Composing the URL by replacing all backslashs
		String sUrl = "file:///" + temp.getAbsolutePath().replace('\\', '/');
		// System.out.println("Temporary File: " + sUrl);

		// Prepare Url for the output directory
		String sOutUrl = "private:stream"; // "file:///" +
											// output.getAbsolutePath().replace(
											// '\\', '/' );
		OOOutputStream out = new OOOutputStream(output);

		// Loading the wanted document
		com.sun.star.beans.PropertyValue propertyValues[] = new com.sun.star.beans.PropertyValue[1];
		propertyValues[0] = new com.sun.star.beans.PropertyValue();
		propertyValues[0].Name = "Hidden";
		propertyValues[0].Value = new Boolean(true);

		Object oDocToStore = xCompLoader.loadComponentFromURL(sUrl, "_blank", 0, propertyValues);

		// delete the temporary file
		temp.delete();

		// Getting an object that will offer a simple way to store
		// a document to a URL.
		com.sun.star.frame.XStorable xStorable = (com.sun.star.frame.XStorable) UnoRuntime.queryInterface(com.sun.star.frame.XStorable.class, oDocToStore);

		// Preparing properties for converting the document
		propertyValues = new com.sun.star.beans.PropertyValue[2];
		// Setting the output stream
		propertyValues[0] = new com.sun.star.beans.PropertyValue();
		propertyValues[0].Name = "OutputStream";
		propertyValues[0].Value = out;
		// Setting the filter name
		propertyValues[1] = new com.sun.star.beans.PropertyValue();
		propertyValues[1].Name = "FilterName";
		propertyValues[1].Value = filter;

		// Storing and converting the document
		xStorable.storeToURL(sOutUrl, propertyValues);

		// Closing the converted document. Use XCloseable.close if the
		// interface is supported, otherwise use XComponent.dispose
		com.sun.star.util.XCloseable xCloseable = (com.sun.star.util.XCloseable) UnoRuntime.queryInterface(com.sun.star.util.XCloseable.class, xStorable);

		if (xCloseable != null) {
			xCloseable.close(false);
		} else {
			com.sun.star.lang.XComponent xComp = (com.sun.star.lang.XComponent) UnoRuntime.queryInterface(com.sun.star.lang.XComponent.class, xStorable);

			xComp.dispose();
		}
	}

	/**
	 * Bootstrap UNO, getting the remote component context, getting a new
	 * instance of the desktop (used interface XComponentLoader) and calling the
	 * static method traverse
	 * 
	 * @param args
	 *            The array of the type String contains the directory, in which
	 *            all files should be converted, the favoured converting type
	 *            and the wanted extension
	 */
	public static void main(String args[]) {
		if (args.length < 2) {
			System.out.println("usage: java -jar DocumentConverter.jar " + "\"<file to convert>\" \"<file to write>\"");
			System.out.println("\ne.g.:");
			System.out.println("usage: java -jar DocumentConverter.jar " + "\"c:/myoffice/document.odt\" \"c:/myoffice/document.pdf\"");
			System.exit(1);
		}

		try {
			// Getting the given starting directory
			File input = new File(args[0]);
			File output = new File(args[1]);

			DocumentConverter dc = new DocumentConverter();
			dc.exportPdf(new FileInputStream(input), new FileOutputStream(output));

			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace(System.err);
			System.exit(1);
		}
	}
}