package com.andromeda.utility.logging;

import java.io.PrintStream;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import com.andromeda.utility.Activator;
import com.andromeda.utility.preferences.AndromedaPreferenceConstants;

public class WSConsole {

	private static final String PRODUCT_NAME = "Andromeda";
	private static MessageConsole console;
	private static MessageConsoleStream out;
	private static boolean isDebug;
	private static boolean isError;
	private static boolean isInfo;

	/**
	 * Creates a console or retrieves one if found
	 * 
	 * @param name
	 *            Name of the console
	 * @return The console
	 */
	public static MessageConsole findConsole(String name) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++)
			if (name.equals(existing[i].getName()))
				return (MessageConsole) existing[i];
		// no console found, so create a new one
		MessageConsole myConsole = new MessageConsole(name, null);
		conMan.addConsoles(new IConsole[] { myConsole });
		return myConsole;
	}

	/**
	 * Creates a console with the specified name
	 * 
	 * @param name
	 *            Name of the console
	 * @return The console
	 */
	@Deprecated
	public static MessageConsole getConsole(String name) {
		return findConsole(name);
	}

	/**
	 * Prints a message in console
	 * 
	 * @param message
	 *            Message to print
	 * 
	 */
	@Deprecated
	public static void println(String message) {
		getMessageStream();
		out.println(message);
	}

	/**
	 * Sets the logging preferences
	 */
	public static void setLoggingPreferences() {
		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		isDebug = preferenceStore.getBoolean(AndromedaPreferenceConstants.PREF_BOOLEAN_LOGGING_DEBUG);
		isError = preferenceStore.getBoolean(AndromedaPreferenceConstants.PREF_BOOLEAN_LOGGING_ERROR);
		isInfo = preferenceStore.getBoolean(AndromedaPreferenceConstants.PREF_BOOLEAN_LOGGING_INFO);
	}

	/**
	 * Prints DEBUG message in console
	 * 
	 * @param message
	 *            Message to print
	 */
	public static void d(String message) {
		if (isDebug) {
			getMessageStream();
			out.println("[DEBUG] : " + message);
		}
	}

	/**
	 * Prints the exception stacktrace
	 * 
	 * @param exception
	 *            Exception object
	 */
	public static void e(Exception exception) {
		if (isError) {
			getMessageStream();
			out.println("[ERROR] : " + "----------------- EXCEPTION STACK TRACE -----------------");
			exception.printStackTrace(new PrintStream(out));
			out.println("[ERROR] : " + "----------------- EXCEPTION STACK TRACE -----------------");
		}
	}

	/**
	 * Prints ERROR message in console
	 * 
	 * @param message
	 *            Message to print
	 */
	public static void e(String message) {
		if (isError) {
			getMessageStream();
			out.println("[ERROR] : " + message);
		}
	}

	/**
	 * Prints INFO message in console
	 * 
	 * @param message
	 *            Message to print
	 */
	public static void i(String message) {
		if (isInfo) {
			getMessageStream();
			out.println("[INFO]  : " + message);
		}
	}

	/**
	 * Create and return a message stream
	 */
	private static void getMessageStream() {
		if (console == null || out == null) {
			console = getConsole(PRODUCT_NAME);
			out = console.newMessageStream();
		}
	}
}
