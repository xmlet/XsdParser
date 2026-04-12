package org.xmlet.xsdparser;

import java.net.MalformedURLException;
import java.net.URL;

import org.xmlet.xsdparser.core.XsdParserJar;

public class XsdParserMain {

	public static void main(String[] args) {
		try {
			new XsdParserJar(new URL(args[0]), args[1]);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
}
