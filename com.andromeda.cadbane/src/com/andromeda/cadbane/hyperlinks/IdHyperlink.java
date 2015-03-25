package com.andromeda.cadbane.hyperlinks;

import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.IRegion;

import com.andromeda.cadbane.search.StringSearcher;
import com.andromeda.utility.logging.WSConsole;
import com.andromeda.utility.utils.UtilResource;

/**
 * Hyperlink when id tag is clicked in a layout file
 * 
 * @author tsaravana
 *
 */
public class IdHyperlink extends AbstractHyperlink {

	public static final Pattern patternIdHyperlink = Pattern.compile("android:id=\"(@(?:\\+)?id/([^\"]*))\"");

	private final IRegion targetRegion;

	/** the id to search */
	private final String idName;
	private final IProject project;

	private final IFile file;

	public IdHyperlink(IRegion region, String idName, IProject project, IFile file) {
		targetRegion = region;
		this.project = project;
		this.idName = idName;
		this.file = file;
	}

	@Override
	public IRegion getHyperlinkRegion() {
		return targetRegion;
	}

	@Override
	public String getTypeLabel() {
		return null;
	}

	@Override
	public String getHyperlinkText() {
		return "Find all occurrences";
	}

	@Override
	public void open() {
		WSConsole.d("IdHyperlink Open()");
		StringSearcher searcher = new StringSearcher(idName, project, StringSearcher.FIND_ALL_OCCURRENCES);
		String layoutName = UtilResource.getFileNameWithoutExtension(file.getName());

		searcher.search(StringSearcher.R_ID + idName, StringSearcher.R_LAYOUT + layoutName);
	}

}
