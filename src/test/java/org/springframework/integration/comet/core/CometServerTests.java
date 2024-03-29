/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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
package org.springframework.integration.comet.core;

import java.io.File;

import org.junit.Test;
import org.springframework.integration.comet.EmbeddedCometServer;

/**
 * @author Amol Nayak
 *
 */
public class CometServerTests {

	@Test
	public void startApp() throws Exception {
		EmbeddedCometServer server = new EmbeddedCometServer();
		server.initialize(8080);
		File file = new File(".");
		String current = file.getAbsolutePath();
		String webAppPath = 
				current.substring(0,current.lastIndexOf("\\")) 
					+ File.separator + "src" + File.separator + "test" + 
						File.separator + "resources" + File.separator + "webapp";		
		server.setWebappResourceBase(webAppPath, "/CometApp");
		server.start();
		server.join();
	}
}
