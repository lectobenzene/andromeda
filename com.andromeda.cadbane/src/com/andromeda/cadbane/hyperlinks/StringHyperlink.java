package com.andromeda.cadbane.hyperlinks;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.IRegion;

import com.andromeda.cadbane.search.StringSearcher;

public class StringHyperlink extends AbstractHyperlink {

	private final IRegion targetRegion;
	private final String stringName;
	private final IProject project;
	private int hyperlinkType;

	public StringHyperlink(IRegion region, String stringName, IProject project, int hyperlinkType) {
		targetRegion = region;
		this.project = project;
		this.stringName = stringName;
		this.hyperlinkType = hyperlinkType;
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
		switch (hyperlinkType) {
		case StringSearcher.FIND_ALL_OCCURANCES:
			return "Find all occurances";

		case StringSearcher.FIND_IN_LAYOUT:
			return "Find in layout";

		default:
			return null;
		}
	}

	@Override
	public void open() {
		StringSearcher searcher = new StringSearcher(stringName, project, hyperlinkType);
		searcher.search();
	}

}
