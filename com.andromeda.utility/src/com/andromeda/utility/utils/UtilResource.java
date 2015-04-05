package com.andromeda.utility.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

import com.andromeda.utility.logging.WSConsole;

/**
 * Utility that holds everything that relates to Resources
 * 
 * @author tsaravana
 *
 */
public class UtilResource {

	private static final String ANDROID_MANIFEST_XML = "AndroidManifest.xml";

	/**
	 * 
	 * @param resource the resource within the project
	 * @return true if the resource is from the Android project, false otherwise
	 */
	public static boolean isAndroidProject(IResource resource) {
		IProject project = resource.getProject();
		IResource findMember = project.findMember(ANDROID_MANIFEST_XML);

		if (findMember == null) {
			return false;
		} else {
			return true;
		}
	}
		
	/**
	 * Creates a resource and refreshes the workspace
	 * 
	 * @param resource
	 *            The resource to create
	 */
	public static void createFile(IResource resource) {
		File file = new File(resource.getLocation().toOSString());
		if (file.isFile()) {
			file.getParentFile().mkdirs();
		} else {
			file.mkdirs();
		}

		// refresh the workspace
		try {
			resource.getProject().refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e) {
			WSConsole.e(e);
		}
	}

	/**
	 * Creates a resource and refreshes the workspace
	 * 
	 * @param path
	 *            Path relative to the workspace
	 */
	public static void createFile(String path) {
		IResource resource = getResource(path);
		File file = new File(resource.getLocation().toOSString());
		if (file.isFile()) {
			file.getParentFile().mkdirs();
		} else {
			file.mkdirs();
		}

		// refresh the workspace
		try {
			resource.getProject().refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e) {
			WSConsole.e(e);
		}
	}

	/**
	 * Returns a {@code IResource} object for the given path
	 * 
	 * @param path
	 *            The location for which the resource is claimed
	 * @return The resource pointing to the path
	 */
	public static IResource getResource(String path) {
		return ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(path));
	}

	/**
	 * Returns a {@code IResource} HANDLE object for the given path
	 * 
	 * @param path
	 *            The location for which the resource is claimed
	 * @return The resource pointing to the path
	 */
	public static IResource getResourceHandle(String path) {
		Path fullPath = new Path(path);
		if (fullPath.getFileExtension() != null) {
			return ResourcesPlugin.getWorkspace().getRoot().getFile(fullPath);
		} else {
			return ResourcesPlugin.getWorkspace().getRoot().getFolder(fullPath);
		}
	}

	/**
	 * Returns the file name without the extension part
	 * 
	 * @param fileName
	 *            Name of the file
	 * @return
	 */
	public static String getFileNameWithoutExtension(String fileName) {
		int lastIndex = fileName.lastIndexOf(".");
		return fileName.substring(0, lastIndex);
	}

	/**
	 * Returns the extension of the file
	 * 
	 * @param fileName
	 *            name of the file
	 * @return
	 */
	public static String getFileExtension(String fileName) {
		String[] split = fileName.split("\\.");
		return split[split.length - 1];
	}

	/**
	 * Searches the file for the string and returns true if found
	 * 
	 * @param searchString
	 *            the string to find
	 * @param file
	 *            the file to search in
	 * @return true if found, false otherwise
	 */
	public static boolean searchFile(String searchString, File file) {
		Scanner scanner = null;
		try {
			scanner = new Scanner(file);
			while (scanner.hasNextLine()) {
				String nextLine = scanner.nextLine();
				if (nextLine.contains(searchString)) {
					WSConsole.d("searchString found in the nextLine = " + nextLine);
					return true;
				}
			}
		} catch (FileNotFoundException e) {
			WSConsole.e(e);
		} finally {
			scanner.close();
		}
		return false;

	}

}
