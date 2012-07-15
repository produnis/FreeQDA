/*******************************************************************************
 * FreeQDA,  a software for professional qualitative research data 
 * analysis, such as interviews, manuscripts, journal articles, memos
 * and field notes.
 *
 * Copyright (C) 2011 Dirk Kitscha, Jörg große Schlarmann
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package net.sf.freeqda.common.handler;

import net.sf.freeqda.common.printing.TaggableStyledTextPrintDataContainer;
import net.sf.freeqda.common.widget.ITaggableStyledTextProvider;
import net.sf.freeqda.common.widget.TaggableStyledText;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledTextPrintOptions;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;


public class PrintCurrentHandler extends AbstractHandler {

	private static final String HEADER_ADDITION_FQDA = Messages.PrintCurrentHandler_HeaderAddition;
	private static final String FOOTER_ADDITION_PAGE = Messages.PrintCurrentHandler_FooterAdditionPage;
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow workbenchWindow = HandlerUtil.getActiveWorkbenchWindow(event);
		final IEditorPart activeEditor = workbenchWindow.getActivePage().getActiveEditor();
		if ((activeEditor == null) || (! (activeEditor instanceof ITaggableStyledTextProvider))) return null;
		
		PrintDialog dialog = new PrintDialog(workbenchWindow.getShell(), SWT.NONE);
		PrinterData data = dialog.open();
		if (data == null) return null;
		
		final ITaggableStyledTextProvider provider = (ITaggableStyledTextProvider) activeEditor;
		final Printer printer = new Printer(data);
		final TaggableStyledTextPrintDataContainer printData = provider.getPrintableStyledText(); 
		final TaggableStyledText toPrint = printData.styledText;

		if (toPrint == null) return null;
		
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			public void run() {
				// TODO check trim areas
				// (http://www.eclipse.org/swt/faq.php#printertrim)

				StyledTextPrintOptions options = new StyledTextPrintOptions();
				options.header = activeEditor.getTitle() + StyledTextPrintOptions.SEPARATOR + StyledTextPrintOptions.SEPARATOR + HEADER_ADDITION_FQDA;
				options.footer = StyledTextPrintOptions.SEPARATOR + FOOTER_ADDITION_PAGE + StyledTextPrintOptions.PAGE_TAG + StyledTextPrintOptions.SEPARATOR;
				options.printLineBackground = true;
				options.printTextBackground = true;
				options.printTextFontStyle = true;
				options.printTextForeground = true;
				options.printLineNumbers = true;
				options.lineLabels = printData.lineNumberStrings;
				
				toPrint.print(printer, options).run();

				printer.dispose();
			}
		});

		return null;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
