package com.andromeda.cadbane.handlers;

import java.util.regex.Pattern;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.search.core.text.TextSearchEngine;
import org.eclipse.search.core.text.TextSearchRequestor;
import org.eclipse.search.ui.text.FileTextSearchScope;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.PlatformUI;

import com.andromeda.cadbane.search.StringSearchQuery;

public class SearchHandler extends AbstractHandler {

	private IProject project;
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Auto-generated method stub
		System.out.println("Executing SearchHandler...");
		
		// get the working set, the project
		ISelectionService selectionService = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService();
		ISelection selection = selectionService.getSelection();
		if(selection instanceof IStructuredSelection){
			Object firstElement = ((IStructuredSelection) selection).getFirstElement();
			if(firstElement instanceof IResource){
				project = ((IResource) firstElement).getProject();
			}else if(firstElement instanceof IJavaElement){
				// FIXME Check for NPE
				project = ((IJavaElement) firstElement).getJavaProject().getProject();
			}
		}
		
		if(project != null){
			System.out.println(project.getFullPath());
			
			// Create a search scope
			FileTextSearchScope scope = FileTextSearchScope.newSearchScope(new IResource[]{project}, new String[]{"*.xml"}, false);
			TextSearchRequestor requestor = (new StringSearchQuery()).getRequestor();
			TextSearchEngine.create().search(scope, requestor, Pattern.compile("npi_please_search"), null);
			
		}
		 
		return null;
	}

}
