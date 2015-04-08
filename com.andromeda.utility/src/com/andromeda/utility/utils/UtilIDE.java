package com.andromeda.utility.utils;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

public class UtilIDE {

	/**
	 * Get the resource reference for the file that is open in the editor
	 * 
	 * @return the resource file that is open in the current editor
	 */
	public static IResource getResourceFromEditor() {
		return ((IFileEditorInput) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getEditorInput()).getFile();
	}

	/**
	 * Get the current selection in the editor
	 * 
	 * @return current selection in the editor, or null if nothing is selected
	 */
	public static ITextSelection getCurrentSelection() {
		IEditorPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (part instanceof ITextEditor) {
			final ITextEditor editor = (ITextEditor) part;
			ISelection sel = editor.getSelectionProvider().getSelection();
			if (sel instanceof TextSelection) {
				ITextSelection textSel = (ITextSelection) sel;
				return textSel;
			}
		}
		return null;
	}
}
