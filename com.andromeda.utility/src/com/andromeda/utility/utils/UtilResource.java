package com.andromeda.utility.utils;

import java.io.File;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

import com.andromeda.utility.logging.WSConsole;

public class UtilResource {

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
			WSConsole.e(e.getMessage());
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
			WSConsole.e(e.getMessage());
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
}
