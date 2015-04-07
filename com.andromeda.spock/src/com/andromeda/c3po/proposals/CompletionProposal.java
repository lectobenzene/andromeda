package com.andromeda.c3po.proposals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposalComputer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.templates.persistence.TemplatePersistenceData;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.ui.editors.text.templates.ContributionContextTypeRegistry;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;

import com.andromeda.utility.logging.WSConsole;
import com.andromeda.utility.utils.UtilIDE;
import com.andromeda.utility.utils.UtilResource;

/**
 * Though this class should contribute completion proposals, in this particular case this is used to modify the completion proposals. Hence an empty
 * proposal list is returned. But in these callback methods, the templateStore is modified to suit the use case.
 * 
 * @author tsaravana
 *
 */
public class CompletionProposal implements IJavaCompletionProposalComputer {

	private static final String TEMPLATES_KEY = "org.eclipse.jdt.ui.text.custom_templates";

	// The Custom template IDs
	private static final String TEMPLATE_TRY = "com.eclipse.jdt.ui.templates.try";
	private static final String TEMPLATE_CATCH = "com.eclipse.jdt.ui.templates.catch";
	private static final String TEMPLATE_SYSOUT = "com.eclipse.jdt.ui.templates.sysout";

	// The JDT template IDs
	private static final String TEMPLATE_JDT_SYSOUT = "org.eclipse.jdt.ui.templates.sysout";
	private static final String TEMPLATE_JDT_TRY = "org.eclipse.jdt.ui.templates.try";
	private static final String TEMPLATE_JDT_CATCH = "org.eclipse.jdt.ui.templates.catch";

	private TemplateStore templateStore = null;

	public CompletionProposal() {
	}

	@Override
	public void sessionStarted() {
		WSConsole.d("Content Assist Session Started");
		filterTemplates();
	}

	/**
	 * Filters the templates. Actually, this just deletes the templates that are not to be shown. But the deleted templates will be restored during
	 * {@link #sessionEnded()}
	 */
	private void filterTemplates() {
		templateStore = getTemplateStore();

		IResource file = UtilIDE.getResourceFromEditor();

		if (UtilResource.isAndroidProject(file)) {
			WSConsole.d(file.getName() + " is from an Android Project");
			deleteTemplate(templateStore, TEMPLATE_JDT_SYSOUT);
			deleteTemplate(templateStore, TEMPLATE_JDT_TRY);
			deleteTemplate(templateStore, TEMPLATE_JDT_CATCH);
		} else {
			WSConsole.d(file.getName() + " is from a NON-Android Project");
			deleteTemplate(templateStore, TEMPLATE_SYSOUT);
			deleteTemplate(templateStore, TEMPLATE_TRY);
			deleteTemplate(templateStore, TEMPLATE_CATCH);
		}

		try {
			templateStore.save();
		} catch (IOException e) {
			WSConsole.e(e);
		}
	}

	/**
	 * Deletes the template from the template store
	 * 
	 * @param templateStore
	 *            the template store
	 * @param id
	 *            the id of the template data
	 */
	private void deleteTemplate(TemplateStore templateStore, String id) {
		TemplatePersistenceData templateData = templateStore.getTemplateData(id);
		WSConsole.d("Template Data that is deleted = " + id);
		if (templateData != null) {
			templateStore.delete(templateData);
		}
	}

	@Override
	public void sessionEnded() {
		WSConsole.d("Content Assist Session Ended");
		getTemplateStore().restoreDeleted();
	}

	/**
	 * Get the Template Store of the JDT UI.
	 * 
	 * @return the JDT template store
	 */
	private TemplateStore getTemplateStore() {
		if (templateStore == null) {
			System.out.println("templateStore is null - Creating a new one");

			final ContributionContextTypeRegistry registry = new ContributionContextTypeRegistry(JavaUI.ID_CU_EDITOR);
			final IPreferenceStore store = PreferenceConstants.getPreferenceStore();

			templateStore = new ContributionTemplateStore(registry, store, TEMPLATES_KEY);

			try {
				templateStore.load();
			} catch (IOException e) {
				WSConsole.e(e);
			}
			templateStore.startListeningForPreferenceChanges();
		}
		return templateStore;
	}

	@Override
	public List<ICompletionProposal> computeCompletionProposals(ContentAssistInvocationContext context, IProgressMonitor monitor) {
		return new ArrayList<ICompletionProposal>();
	}

	@Override
	public List<IContextInformation> computeContextInformation(ContentAssistInvocationContext context, IProgressMonitor monitor) {
		return new ArrayList<IContextInformation>();
	}

	@Override
	public String getErrorMessage() {
		return null;
	}
}
