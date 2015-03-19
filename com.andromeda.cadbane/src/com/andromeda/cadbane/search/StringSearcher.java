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

/**
 * Holds methods to do a search. Currently supports only string resource. Should
 * make this more generic to support all sorts of resources
 * 
 * @author tsaravana
 *
 */
public class StringSearcher {

	/** the array that holds the results of the local search of id's */
	private List<String> results;

	/** current project that is used for getting the scope */
	private IProject project;

	/** the string to search */
	private String stringToSearch;

	/** the type of search, all occurrences or just in layout */
	private int searchType;

	/** Searches in both layout and java. Searches everywhere */
	public static final int FIND_ALL_OCCURRENCES = 0;
	/** Searches only in the layout */
	public static final int FIND_IN_LAYOUT = 1;

	public StringSearcher(String stringToSearch, IProject project, int searchType) {
		this.stringToSearch = stringToSearch;
		this.project = project;
		this.searchType = searchType;
	}

	/**
	 * Performs the internal search if searchType is All Occurrences, and then
	 * does a UI search.
	 */
	public void search() {
		FileTextSearchScope scope;

		// Don't perform the internal search if search type is Layout only.
		if (searchType == StringSearcher.FIND_ALL_OCCURRENCES) {
			// Scope to search the id from string in layout
			scope = FileTextSearchScope.newSearchScope(new IResource[] { project }, new String[] { "*.xml" }, false);
			StringSearchQuery query = new StringSearchQuery(getResults(), scope, Pattern.compile(stringToSearch));
			query.run(null);
		}

		// Scope to search every java and xml file
		scope = FileTextSearchScope.newSearchScope(new IResource[] { project }, new String[] { "*.xml", "*.java" }, false);

		String expressionToSearch = getExpressionToSearch();
		WSConsole.d("expressionToSearch = " + expressionToSearch);

		runUISearch(scope, expressionToSearch);
	}

	/**
	 * Constructs the regEx expression that has to be searched finally using the
	 * default UI functionality
	 * 
	 * @return the regEx expression that has to be searched
	 */
	private String getExpressionToSearch() {
		StringBuilder builder = new StringBuilder();

		// TODO Should refactor this code to make it more efficient.
		if (searchType == FIND_IN_LAYOUT) {
			// Only the layouts
			builder.append("(").append("@string/").append(stringToSearch).append(")");
		} else if (searchType == FIND_ALL_OCCURRENCES) {
			builder.append("(").append("@string/").append(stringToSearch).append(")");
			// Add the Java version also
			builder.append("|").append("(").append("R.string.").append(stringToSearch).append(")");
			// Add the ID's also to the search bucket
			for (String id : getResults()) {
				builder.append("|").append("(").append(id).append(")");
			}
		}

		// Clear the results once it is used
		getResults().clear();
		return builder.toString();
	}

	/**
	 * Searches for the regEx expression in the scope provided using the default
	 * Eclipse File search (UI search)
	 * 
	 * @param scope
	 *            The scope of search
	 * @param expressionToSearch
	 *            the regEx expression that has to be searched
	 */
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

	/**
	 * To obtain the results array, both to send as input and to fetch
	 * information back
	 * 
	 * @return the results array
	 */
	public List<String> getResults() {
		if (results == null) {
			results = new ArrayList<String>();
		}
		return results;
	}
}
