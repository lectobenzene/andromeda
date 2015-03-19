package com.andromeda.cadbane.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.search.ui.text.FileTextSearchScope;
import org.eclipse.search.ui.text.TextSearchQueryProvider;
import org.eclipse.search.ui.text.TextSearchQueryProvider.TextSearchInput;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.PlatformUI;

import com.andromeda.cadbane.search.StringSearchQuery;

public class SearchHandler extends AbstractHandler {

	private IProject project;
	private List<String> idResults;
	private String searchString;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Auto-generated method stub
		System.out.println("Executing SearchHandler...");

		// get the working set, the project
		ISelectionService selectionService = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getSelectionService();
		ISelection selection = selectionService.getSelection();
		if (selection instanceof IStructuredSelection) {
			Object firstElement = ((IStructuredSelection) selection)
					.getFirstElement();
			if (firstElement instanceof IResource) {
				project = ((IResource) firstElement).getProject();
			} else if (firstElement instanceof IJavaElement) {
				// FIXME Check for NPE
				project = ((IJavaElement) firstElement).getJavaProject()
						.getProject();
			}
		}

		if (project != null) {
			System.out.println(project.getFullPath());

			// Create a search scope
			FileTextSearchScope scope = FileTextSearchScope.newSearchScope(
					new IResource[] { project }, new String[] { "*.xml" },
					false);
			searchString = "pv_check_back_title";
			StringSearchQuery query = new StringSearchQuery(getIdResults(), scope,
					Pattern.compile(searchString));
			query.run(null);

			for (String string : idResults) {
				System.out.println("ID RESULTS - " + string);
			}
		
			
			// Run the UI search query
			try {
				TextSearchQueryProvider preferred = TextSearchQueryProvider.getPreferred();
				TextSearchInput input = new TextSearchInput() {
					
					@Override
					public boolean isRegExSearch() {
						return true;
					}
					
					@Override
					public boolean isCaseSensitiveSearch() {
						return false;
					}
					
					@Override
					public String getSearchText() {
						// TODO Auto-generated method stub
						return constructRegExpString();
					}
					
					private String constructRegExpString() {
						StringBuilder builder = new StringBuilder();
						builder = builder.append("(").append("@string/").append(searchString).append(")").append("|").append("(").append("R.string.").append(searchString).append(")");
						for (String id : idResults) {
							builder.append("|").append("(").append(id).append(")");
						}
						
						// Clear the idResults once it is used
						idResults.clear();
						return builder.toString();
					}

					@Override
					public FileTextSearchScope getScope() {
						return FileTextSearchScope.newSearchScope(
								new IResource[] { project }, new String[] { "*.xml","*.java" },
								false);
					}
				};
				ISearchQuery createQuery = preferred.createQuery(input);
				NewSearchUI.runQueryInBackground(createQuery);
			} catch (IllegalArgumentException | CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return null;
	}


	public List<String> getIdResults() {
		if(idResults == null){
			idResults = new ArrayList<String>();
		}
		return idResults;
	}

}
