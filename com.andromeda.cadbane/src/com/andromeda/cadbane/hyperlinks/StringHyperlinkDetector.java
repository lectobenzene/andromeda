package com.andromeda.cadbane.hyperlinks;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;

import com.andromeda.cadbane.search.StringSearcher;
import com.andromeda.utility.logging.WSConsole;

public class StringHyperlinkDetector extends AbstractHyperlinkDetector {

	private String stringName;

	@Override
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {

		IDocument document = textViewer.getDocument();
		int offset = region.getOffset();

		IFile file = ((IFileEditorInput) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getEditorInput())
				.getFile();
		IProject project = file.getProject();

		IRegion lineRegion = null;
		String matchLine = null;

		try {
			lineRegion = document.getLineInformationOfOffset(offset);
			matchLine = document.get(lineRegion.getOffset(), lineRegion.getLength());
		} catch (BadLocationException e) {
			WSConsole.e(e);
		}

		ArrayList<IHyperlink> hyperlinks = new ArrayList<IHyperlink>();

		Pattern patternService = Pattern.compile("<string name=\"([^\"]*)\">");
		Matcher matcher = patternService.matcher(matchLine);

		while (matcher.find()) {
			stringName = matcher.group(1);

			int index = matchLine.indexOf(stringName);
			IRegion targetRegion = new Region(lineRegion.getOffset() + index, stringName.length());
			if (targetRegion != null) {
				if ((targetRegion.getOffset() <= offset) && (targetRegion.getOffset() + targetRegion.getLength()) > offset) {
					hyperlinks.add(new StringHyperlink(targetRegion, stringName, project, StringSearcher.FIND_ALL_OCCURANCES));
					hyperlinks.add(new StringHyperlink(targetRegion, stringName, project, StringSearcher.FIND_IN_LAYOUT));
				}
			}
		}
		if (hyperlinks.isEmpty()) {
			return null;
		} else {
			return hyperlinks.toArray(new IHyperlink[2]);
		}
	}

}
