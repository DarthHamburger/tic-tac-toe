/** Copyright 2016 Bryan Charles Bettis
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package core;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/** A base class for loading and/or handling game resources (images, sounds,
 * etc.) into memory, instead of reloading them from a file before every use.
 * @author Bryan Charles Bettis
 */
public abstract class GameResourcesBuffer
{
	/** Where all game resources are stored. */
	public static final String RES_DIR = "game/resources/";
	/** Loads all resources for a subclass. */
	public abstract void loadAll();
	/** How to load a single resource for a subclass.
	 * @param filePath the path to the file to load
	 */
	public abstract void load(String filePath);
	// TODO implement absolute resource path loading?
//	/** Checks if a resource buffer class supports the extension on the
//	 * specified file.
//	 * @param filePath the path containing the file name and extension
//	 * @return true if it is supported, false otherwise
//	 */
//	public abstract boolean extensionSupported(String filePath);
	/** Check if the specified resource exists.
	 * @param resource the resource to look for
	 * @return true if the resource exists, false otherwise
	 */
	public abstract boolean resExists(String resource);
	/** Gets the specified resource. The class/type of the returned object
	 * is changed by an implementing subclass.
	 * @param resource the resource to get
	 * @return the resource specified, or null if the resource was not found
	 */
	public abstract Object getRes(String resource);
	
	/** Indexes all files in the specified directory (including
	 * sub-directories), then tries to load all those files using the
	 * load function of a subclass.
	 * @param rootDir the root directory of the subclasses resources
	 * @param resType the type of files to display for debug/progress output
	 */
	protected final void loadAll(String rootDir, String resType)
	{
		System.out.println("Preloading/Checking " + resType + "...");
		// The list of relative paths for files to try to load
		LinkedList<String> toLoad = new LinkedList<String>();
		// Loading resources from a jar
		if (core.GameSession.getURIScheme().equals("jar"))
		{
			// Get the URL for the jar file
			URL jarFile = getClass().getProtectionDomain().getCodeSource().getLocation();
			// Open the jar as a zip input stream
			ZipInputStream jar;
			try
			{
				jar = new ZipInputStream(jarFile.openStream());
			}
			catch (IOException e)
			{
				System.out.println("Error opening Jar to locate " + resType + ".");
				return;
			}
			// Check all entries in the jar to find the ones in rootDir
			while (true)
			{
				ZipEntry entry;
				// Get the next entry
				try
				{
					entry = jar.getNextEntry();
				}
				catch (IOException e)
				{
					System.out.println("Error finding " + resType + " in the jar.");
					break;
				}
				// Last entry has been checked, stop looking
				if (entry == null)
				{
					break;
				}
				// Get the internal jar path to the entry
				String name = entry.getName();
				// Entry is in our resource folder
				if (name.contains(rootDir) && name.contains("."))
				{
					// Add it to the list of files to try to load
					toLoad.add(name.replace(rootDir, ""));
				}
			}
		}
		// Loading resources not in a jar
		else
		{
			// File system paths to check for files
			LinkedList<String> toCheck = new LinkedList<String>();
			// Start off with the root resource directory
			toCheck.add(
					new File(
							core.GameSession.class.getResource(
									"/" + rootDir
									).getPath()
							).getAbsolutePath()
					);
			// Keep checking until out of file system objects to check
			while (!toCheck.isEmpty())
			{
				// Get the next path to check
				String checking = toCheck.poll();
				// Get a file system object for the path
				File node = new File(checking);
				// Add contents of directories to be checked later
				if (node.isDirectory())
				{
					for (File newCheck : node.listFiles())
					{
						toCheck.addLast(newCheck.getAbsolutePath());
					}
				}
				// Add a file to the list of files to load
				else if (node.isFile())
				{
					toLoad.add(checking.replace(File.separatorChar, '/').split(rootDir,2)[1]);
				}
			}
		}
		// Try to load all possible resource files found
		for (int i = 0; i < toLoad.size(); ++i)
		{
			load(toLoad.get(i));
			System.out.println("Loading/Checking " + resType + "..." + (toLoad.size() - i));
		}
	}
	/** Checks if the given file path or file name contains a supported
	 * format/extension.
	 * @param supported the list of supported extensions
	 * @param filePath the path to or name of the file to be checked
	 * @return true if the extension is supported, false otherwise
	 */
	protected boolean extensionSupported(String[] supported, String filePath)
	{
		// Check for each supported extension
		for (String se : supported)
		{
			if (filePath.contains(se) || filePath.contains(se.toUpperCase()))
			{
				return true;
			}
		}
		// Unsupported extension
		return false;
	}
	
	/** Gets an input stream, usually used for initially loading the resource.
	 * @param path the path to the file to load
	 * @return an input stream that can work both inside and outside a jar
	 */
	protected final InputStream getInputStream(String path)
	{
		InputStream is = this.getClass().getResourceAsStream(path);
		return new BufferedInputStream(is);
	}
}
