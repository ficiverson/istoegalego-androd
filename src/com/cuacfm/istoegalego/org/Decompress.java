/*
 * Copyright (C) 2015 Fernando Souto Gonzalez @ficiverson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cuacfm.istoegalego.org;

import android.util.Log; 

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File; 
import java.io.FileInputStream; 
import java.io.FileOutputStream; 
import java.io.IOException;
import java.util.zip.ZipEntry; 
import java.util.zip.ZipInputStream; 

public class Decompress { 
	private String _zipFile; 
	private String _location; 
	private static int BUFFER_SIZE=1024;

	public Decompress(String zipFile, String location) { 
		_zipFile = zipFile; 
		_location = location; 

	} 


	public boolean unzip() throws IOException {
		int size;
		byte[] buffer = new byte[BUFFER_SIZE];

		try {
			if ( !_location.endsWith("/") ) {
				_location += "/";
			}
			File f = new File(_location);
			if(!f.isDirectory()) {
				f.mkdirs();
			}
			ZipInputStream zin = new ZipInputStream(new BufferedInputStream(new FileInputStream(_zipFile), BUFFER_SIZE));
			try {
				ZipEntry ze = null;
				while ((ze = zin.getNextEntry()) != null) {
					String path = _location + ze.getName();
					File unzipFile = new File(path);

					if (ze.isDirectory()) {
						if(!unzipFile.isDirectory()) {
							unzipFile.mkdirs();
						}
					} else {
						// check for and create parent directories if they don't exist
						File parentDir = unzipFile.getParentFile();
						if ( null != parentDir ) {
							if ( !parentDir.isDirectory() ) {
								parentDir.mkdirs();
							}
						}

						// unzip the file
						FileOutputStream out = new FileOutputStream(unzipFile, false);
						BufferedOutputStream fout = new BufferedOutputStream(out, BUFFER_SIZE);
						try {
							while ( (size = zin.read(buffer, 0, BUFFER_SIZE)) != -1 ) {
								fout.write(buffer, 0, size);
							}

							zin.closeEntry();
						}
						finally {
							fout.flush();
							fout.close();
						}
					}
				}
			}
			finally {
				zin.close();	
			}
			
		}
		catch (Exception e) {
			Log.e("", "Unzip exception", e);
			return false;
			
		}
		return true;
	}
} 