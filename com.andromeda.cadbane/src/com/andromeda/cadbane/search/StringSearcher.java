package com.andromeda.cadbane.search;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.search.ui.text.FileTextSearchScope;
import org.eclipse.search.ui.text.TextSearchQueryProvider;
import org.eclipse.search.ui.text.TextSearchQueryProvider.TextSearchInput;

import com.andromeda.utility.logging.WSConsole;

public class StringSearcher {

	private List<String> results;
	private IProject project;
	private String stringToSearch;
	private int searchType;
	
	public static final int FIND_ALL_OCCURANCES = 0;
	public static final int FIND_IN_LAYOUT = 1;

	public StringSearcher(String stringToSearch, IProject project, int searchType) {
		this.stringToSearch = stringToSearch;
		this.project = project;
		this.searchType = searchType;
	}

	public void search() {
		FileTextSearchScope scope = FileTextSearchScope.newSearchScope(new IResource[] { project }, new String[] { "*.xml" }, false);
		StringSearchQuery query = new StringSearchQuery(getIdResults(), scope, Pattern.compile(stringToSearch));
		query.run(null);

		scope = FileTextSearchScope.newSearchScope(new IResource[] { project }, new String[] { "*.xml", "*.java" }, false);

		String expressionToSearch = getExpressionToSearch();
		WSConsole.d("expressionToSearch = "+expressionToSearch);
		runUISearch(scope, expressionToSearch);

		// FIXME To debug
		for (String string : results) {
			System.out.println("ID RESULTS - " + string);
		}

	}

	private String getExpressionToSearch() {
		StringBuilder builder = new StringBuilder();

		if(searchType == FIND_IN_LAYOUT){
			// Only the layouts
			builder.append("(").append("@string/").append(stringToSearch).append(")");			
		}else if(searchType == FIND_ALL_OCCURANCES){
			builder.append("(").append("@string/").append(stringToSearch).append(")");
			// Add the Java version also
			builder.append("|").append("(").append("R.string.").append(stringToSearch).append(")");
			// Add the ID's also to the search bucket
			for (String id : results) {
				builder.append("|").append("(").append(id).append(")");
			}
		}

		// Clear the results once it is used
		results.clear();
		return builder.toString();
	}

	private void runUISearch(FileTextSearchScope scope, String expressionToSearch) {
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
					return expressionToSearch;
				}

				@Override
				public FileTextSearchScope getScope() {
					return scope;
				}
			};
			ISearchQuery createQuery = preferred.createQuery(input);
			NewSearchUI.runQueryInBackground(createQuery);
		} catch (IllegalArgumentException | CoreException e) {
			WSConsole.e(e);
		}
	}

	public List<String> getIdResults() {
		if (results == null) {
			results = new ArrayList<String>();
		}
		return results;
	}
}
