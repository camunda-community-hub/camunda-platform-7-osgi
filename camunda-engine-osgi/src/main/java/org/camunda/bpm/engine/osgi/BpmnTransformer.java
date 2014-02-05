/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.engine.osgi;

import static org.camunda.bpm.engine.osgi.Constants.BUNDLE_PROCESS_DEFINITIONS_HEADER;
import static org.camunda.bpm.engine.osgi.Constants.BUNDLE_PROCESS_DEFINTIONS_DEFAULT;
import static org.osgi.framework.Constants.BUNDLE_MANIFESTVERSION;
import static org.osgi.framework.Constants.BUNDLE_SYMBOLICNAME;
import static org.osgi.framework.Constants.BUNDLE_VERSION;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Helper class to actually transform the BPMN xml file into a bundle.
 * 
 * @author <a href="gnodet@gmail.com">Guillaume Nodet</a>
 */
public class BpmnTransformer {

	static DocumentBuilderFactory dbf;
	static TransformerFactory tf;

	public static void transform(URL url, OutputStream os) throws Exception {
		// Build dom document
		Document doc = parse(url);
		// Heuristicly retrieve name and version
		String name = url.getPath();
		int idx = name.lastIndexOf('/');
		if (idx >= 0) {
			name = name.substring(idx + 1);
		}
		String[] str = NameVersionTypeExtractor.extractNameVersionType(name);
		// Create manifest
		Manifest m = new Manifest();
		m.getMainAttributes().putValue("Manifest-Version", "2");
		m.getMainAttributes().putValue(BUNDLE_MANIFESTVERSION, "2");
		m.getMainAttributes().putValue(BUNDLE_SYMBOLICNAME, str[0]);
		m.getMainAttributes().putValue(BUNDLE_VERSION, str[1]);
		m.getMainAttributes().putValue(BUNDLE_PROCESS_DEFINITIONS_HEADER,
				BUNDLE_PROCESS_DEFINTIONS_DEFAULT);
		// Extract manifest entries from the DOM
		NodeList l = doc.getElementsByTagName("manifest");
		if (l != null) {
			for (int i = 0; i < l.getLength(); i++) {
				Element e = (Element) l.item(i);
				String text = e.getTextContent();
				Properties props = new Properties();
				props.load(new ByteArrayInputStream(text.trim().getBytes()));
				Set<String> en = props.stringPropertyNames();
				for (String k : en) {
					String v = props.getProperty(k);
					m.getMainAttributes().putValue(k, v);
				}
				e.getParentNode().removeChild(e);
			}
		}

		JarOutputStream out = new JarOutputStream(os);
		ZipEntry e = new ZipEntry(JarFile.MANIFEST_NAME);
		out.putNextEntry(e);
		m.write(out);
		out.closeEntry();
		e = new ZipEntry("OSGI-INF/");
		out.putNextEntry(e);
		e = new ZipEntry(BUNDLE_PROCESS_DEFINTIONS_DEFAULT);
		out.putNextEntry(e);
		out.closeEntry();
		e = new ZipEntry(BUNDLE_PROCESS_DEFINTIONS_DEFAULT + name);
		out.putNextEntry(e);
		// Copy the new DOM
		if (tf == null) {
			tf = TransformerFactory.newInstance();
		}
		tf.newTransformer()
				.transform(new DOMSource(doc), new StreamResult(out));
		out.closeEntry();
		out.close();
	}

	protected static Document parse(URL url) throws Exception {
		if (dbf == null) {
			dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
		}
		DocumentBuilder db = dbf.newDocumentBuilder();
		return db.parse(url.toString());
	}

	protected static void copyInputStream(InputStream in, OutputStream out)
			throws Exception {
		byte[] buffer = new byte[4096];
		int len = in.read(buffer);
		while (len >= 0) {
			out.write(buffer, 0, len);
			len = in.read(buffer);
		}
	}

}
