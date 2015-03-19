package com.andromeda.cadbane.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.PlatformUI;

import com.andromeda.cadbane.search.StringSearcher;

/**
 * Handler to search for a given text, currently a String.
 * 
 * @author tsaravana
 *
 */
public class SearchHandler extends AbstractHandler {

	/** current project to get the search scope */
	private IProject project;

	/** The string to search */
	private String searchString;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		// get the working set, the project
		ISelectionService selectionService = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService();
		ISelection selection = selectionService.getSelection();
		if (selection instanceof IStructuredSelection) {
			Object firstElement = ((IStructuredSelection) selection).getFirstElement();
			if (firstElement instanceof IResource) {
				project = ((IResource) firstElement).getProject();
			} else if (firstElement instanceof IJavaElement) {
				// FIXME Check for NPE
				project = ((IJavaElement) firstElement).getJavaProject().getProject();
			}
		}

		if (project != null) {
			// Get the searchString from USER probably from the dialog or remove
			// the
			searchString = "npi_please_search";

			StringSearcher searcher = new StringSearcher(searchString, project, StringSearcher.FIND_ALL_OCCURRENCES);
			searcher.search();
		}

		return null;
	}

}
