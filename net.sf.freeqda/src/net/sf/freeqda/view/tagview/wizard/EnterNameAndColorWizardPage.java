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
package net.sf.freeqda.view.tagview.wizard;

import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class EnterNameAndColorWizardPage extends WizardPage {
	
	private Composite container;
	private Label labelName;
	private Text textName;
	private Label labelColor;
	private Button buttonColor;
	private ColorSelector colorSelector;

	private RGB defaultColor;
	private String defaultText;
	
	public EnterNameAndColorWizardPage(String pageName, String title, String description, String defaultText, RGB defaultColor) {
		super(pageName);
		setTitle(title);
		setDescription(description);
		if (defaultText == null) defaultText = "?";  //$NON-NLS-1$
		this.defaultText = defaultText;
		if (defaultColor == null) defaultColor = new RGB(0,255,255);
		this.defaultColor = defaultColor;
	}
	
	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NULL);
		GridLayout thisLayout = new GridLayout();
		thisLayout.numColumns = 2;
		container.setLayout(thisLayout);
		container.setSize(261, 86);
		{
			labelName = new Label(container, SWT.NONE);
			GridData labelNameLData = new GridData();
			labelName.setLayoutData(labelNameLData);
			labelName.setText(Messages.NameAndColorWizardPage_LabelNameText0);
		}
		{
			textName = new Text(container, SWT.BORDER);
			GridData textNameLData = new GridData();
			textNameLData.grabExcessHorizontalSpace = true;
			textNameLData.horizontalAlignment = GridData.FILL;
			textName.setLayoutData(textNameLData);
			textName.setText(defaultText);
			
			textName.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					setPageComplete(! textName.getText().isEmpty());
				}
			});

		}
		{
			labelColor = new Label(container, SWT.NONE);
			GridData labelColorLData = new GridData();
			labelColor.setLayoutData(labelColorLData);
			labelColor.setText(Messages.NameAndColorWizardPage_LabelNameTextLabelColorText);
		}
		{
			GridData buttonColorLData = new GridData();
			
			colorSelector = new ColorSelector(container);
			colorSelector.setColorValue(new RGB(255, 255, 255));
			buttonColor = colorSelector.getButton();
			buttonColor.setLayoutData(buttonColorLData);
			buttonColor.setToolTipText(Messages.NameAndColorWizardPage_LabelNameTextButtonColorToolTipText);
			colorSelector.setColorValue(defaultColor);
		}
		container.layout();
		
		setControl(container);
		setPageComplete((colorSelector.getColorValue() != null && (! textName.getText().isEmpty())));

	}

	public String getTagName() {
		return textName.getText();
	}
	
	public RGB getTagColor() {
		return colorSelector.getColorValue();
	}
}
