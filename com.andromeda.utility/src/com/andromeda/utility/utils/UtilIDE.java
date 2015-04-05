package com.andromeda.utility.utils;

import org.eclipse.core.resources.IResource;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;

public class UtilIDE {

	/**
	 * Get the resource reference for the file that is open in the editor
	 * 
	 * @return the resource file that is open in the current editor
	 */
	public static IResource getResourceFromEditor() {
		return ((IFileEditorInput) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getEditorInput()).getFile();
	}
}
