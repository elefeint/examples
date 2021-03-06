/*
 * Copyright 2017-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.charset.Charset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * A REST Controller that exposes read and write operations on a Google Cloud Storage file
 * accessed using the Spring Resource Abstraction.
 *
 * @author Mike Eltsufin
 * @author Daniel Zou
 */
@RestController
public class WebController {

	@Value("${gcs-resource-test-bucket}")
	private String gcsBucket;

	private String fileName = "my-file.txt";

	@Autowired
	private Storage storage;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String readGcsFile() throws IOException {
		Blob blob = storage.get(BlobId.of(this.gcsBucket, this.fileName));
		return StreamUtils.copyToString(
				Channels.newInputStream(blob.reader()), Charset.defaultCharset()) + "\n";
	}

	/*@RequestMapping(value = "/", method = RequestMethod.POST)
	String writeGcs(@RequestBody String data) throws IOException {
		try (OutputStream os = ((WritableResource) this.gcsFile).getOutputStream()) {
			os.write(data.getBytes());
		}
		return "file was updated\n";
	}*/
}
