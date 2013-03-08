package org.opengda.detector.electronanalyser.client.sequenceeditor;

import gda.configuration.properties.LocalProperties;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.ui.dnd.EditingDomainViewerDropAdapter;
import org.eclipse.emf.edit.ui.dnd.LocalTransfer;
import org.eclipse.emf.edit.ui.dnd.ViewerDragAdapter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.INullSelectionListener;
import org.eclipse.ui.ISaveablePart;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.part.ViewPart;
import org.opengda.detector.electronanalyser.client.Camera;
import org.opengda.detector.electronanalyser.client.RegionDefinitionResourceUtil;
import org.opengda.detector.electronanalyser.client.RegionStepsTimeEstimation;
import org.opengda.detector.electronanalyser.client.regioneditor.RegionViewExtensionFactory;
import org.opengda.detector.electronanalyser.model.regiondefinition.api.Region;
import org.opengda.detector.electronanalyser.model.regiondefinition.api.RegiondefinitionFactory;
import org.opengda.detector.electronanalyser.model.regiondefinition.api.RegiondefinitionPackage;
import org.opengda.detector.electronanalyser.model.regiondefinition.api.Sequence;
import org.opengda.detector.electronanalyser.model.regiondefinition.api.Spectrum;
import org.opengda.detector.electronanalyser.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.swt.layout.RowData;

public class SequenceView extends ViewPart implements ISelectionProvider,
		IRegionDefinitionView, ISaveablePart {
	private static final Logger logger = LoggerFactory
			.getLogger(SequenceView.class);

	private List<ISelectionChangedListener> selectionChangedListeners;
	private Camera camera;

	public SequenceView() {
		setTitleToolTip("Create a new or editing an existing sequence");
		// setContentDescription("A view for editing sequence parameters");
		setPartName("Sequence Editor");
		this.selectionChangedListeners = new ArrayList<ISelectionChangedListener>();
	}

	private RegionDefinitionResourceUtil regionDefinitionResourceUtil;
	private Text txtNumberActives;
	private Text txtLocation;
	private Text txtUser;
	private Text txtSample;
	private Text txtFilename;
	private Text txtComments;
	private int nameCount;
	private String location;
	private String user;
	private String visit;

	private final String columnHeaders[] = { SequenceTableConstants.STATUS,
			SequenceTableConstants.ENABLED, SequenceTableConstants.REGION_NAME,
			SequenceTableConstants.LENS_MODE,
			SequenceTableConstants.PASS_ENERGY,
			SequenceTableConstants.X_RAY_SOURCE,
			SequenceTableConstants.ENERGY_MODE,
			SequenceTableConstants.LOW_ENERGY,
			SequenceTableConstants.HIGH_ENERGY,
			SequenceTableConstants.ENERGY_STEP,
			SequenceTableConstants.STEP_TIME, SequenceTableConstants.STEPS,
			SequenceTableConstants.TOTAL_TIME,
			SequenceTableConstants.X_CHANNEL_FROM,
			SequenceTableConstants.X_CHANNEL_TO,
			SequenceTableConstants.Y_CHANNEL_FROM,
			SequenceTableConstants.Y_CHANNEL_TO, SequenceTableConstants.SLICES,
			SequenceTableConstants.MODE };

	private ColumnLayoutData columnLayouts[] = {
			new ColumnWeightData(10, false), new ColumnWeightData(10, false),
			new ColumnWeightData(80, true), new ColumnWeightData(70, false),
			new ColumnWeightData(40, false), new ColumnWeightData(50, true),
			new ColumnWeightData(40, false), new ColumnWeightData(50, true),
			new ColumnWeightData(50, true), new ColumnWeightData(60, true),
			new ColumnWeightData(40, true), new ColumnWeightData(40, true),
			new ColumnWeightData(40, true), new ColumnWeightData(40, true),
			new ColumnWeightData(40, true), new ColumnWeightData(40, true),
			new ColumnWeightData(40, true), new ColumnWeightData(40, true),
			new ColumnWeightData(40, true) };

	private TableViewer sequenceTableViewer;
	private List<Region> regions;

	private Sequence sequence;

	private Spectrum spectrum;

	private Combo runMode;

	private Button btnNumberOfIterations;

	private Spinner spinner;

	private Button btnRepeatuntilStopped;

	private Button btnConfirmAfterEachInteration;

	protected boolean isDirty;

	private Resource resource;

	private Text txtEstimatedTime;

	protected Region lastSelection;

	public void createColumns(TableViewer tableViewer, TableColumnLayout layout) {
		for (int i = 0; i < columnHeaders.length; i++) {
			TableViewerColumn tableViewerColumn = new TableViewerColumn(
					tableViewer, SWT.None);
			TableColumn column = tableViewerColumn.getColumn();
			column.setResizable(columnLayouts[i].resizable);
			column.setText(columnHeaders[i]);
			column.setToolTipText(columnHeaders[i]);
			layout.setColumnData(column, columnLayouts[i]);
			tableViewerColumn
					.setEditingSupport(new SequenceColumnEditingSupport(
							tableViewer, tableViewerColumn));
		}
	}

	@Override
	public void createPartControl(final Composite parent) {
		// // Add action
		// getViewSite().getActionBars().getMenuManager().add(new Action() {
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		// super.run();
		// }
		// })
		GridLayout gl_root = new GridLayout();
		gl_root.horizontalSpacing = 2;
		Composite rootComposite = new Composite(parent, SWT.NONE);
		rootComposite.setLayout(gl_root);
		// new Label(rootComposite, SWT.NONE);

		Composite tableViewerContainer = new Composite(rootComposite, SWT.None);

		sequenceTableViewer = new TableViewer(tableViewerContainer, SWT.BORDER
				| SWT.FULL_SELECTION | SWT.MULTI);
		sequenceTableViewer.getTable().setHeaderVisible(true);
		sequenceTableViewer.getTable().setLinesVisible(true);
		sequenceTableViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {
					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						ISelection selection = event.getSelection();
						if (selection instanceof IStructuredSelection) {
							IStructuredSelection sel = (IStructuredSelection) selection;
							Object firstElement = sel.getFirstElement();
							if (firstElement instanceof Region) {
									Region region = (Region) firstElement;
									fireSelectionChanged(region);
							}
						}
					}
				});
		TableColumnLayout tableLayout = new TableColumnLayout();
		tableViewerContainer.setLayout(tableLayout);

		createColumns(sequenceTableViewer, tableLayout);

		tableViewerContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				true, true, 1, 1));

		sequenceTableViewer.setContentProvider(new SequenceViewContentProvider(
				regionDefinitionResourceUtil));
		SequenceViewLabelProvider labelProvider = new SequenceViewLabelProvider();
		labelProvider.setSourceSelectable(regionDefinitionResourceUtil
				.isSourceSelectable());
		if (regionDefinitionResourceUtil.isSourceSelectable()) {
			labelProvider.setXRaySourceEnergyLimit(regionDefinitionResourceUtil
					.getXRaySourceEnergyLimit());
		}
		labelProvider.setCamera(camera);
		sequenceTableViewer.setLabelProvider(labelProvider);
		regions = Collections.emptyList();

		try {
			resource = regionDefinitionResourceUtil.getResource();
			resource.eAdapters().add(notifyListener);
			sequenceTableViewer.setInput(resource);
		} catch (Exception e2) {
			logger.error("Cannot load resouce from file.", e2);
		}

		Composite controlArea = new Composite(rootComposite, SWT.None);
		// Contains region actions, sequence parameters, file saving info and
		// comments.
		controlArea.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		controlArea.setLayout(new GridLayout(2, false));

		Composite leftArea = new Composite(controlArea, SWT.None);
		leftArea.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		leftArea.setLayout(new GridLayout(4, false));

		Composite rightArea = new Composite(controlArea, SWT.None);
		rightArea.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false,
				1, 1));
		rightArea.setLayout(new GridLayout());

		Group grpRegion = new Group(leftArea, SWT.NONE);
		GridData gd_grpRegion = new GridData(GridData.FILL_HORIZONTAL);
		gd_grpRegion.grabExcessHorizontalSpace = false;
		gd_grpRegion.horizontalAlignment = SWT.LEFT;
		gd_grpRegion.widthHint = 257;
		grpRegion.setLayoutData(gd_grpRegion);
		grpRegion.setText("Region Control");
		grpRegion.setLayout(new RowLayout());

		Button btnNew = new Button(grpRegion, SWT.NONE);
		btnNew.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					Region newRegion = RegiondefinitionFactory.eINSTANCE
							.createRegion();
					nameCount = StringUtils.largestIntAtEndStringsWithPrefix(
							getRegionNames(), newRegion.getName());
					if (nameCount != -1) {
						// increment the name
						nameCount++;
						newRegion.setName(newRegion.getName() + nameCount);
					}
					editingDomain.getCommandStack().execute(
							AddCommand.create(editingDomain,
									regionDefinitionResourceUtil.getSequence(),
									RegiondefinitionPackage.eINSTANCE
											.getSequence_Region(), newRegion));
					// sequenceTableViewer.setSelection(new StructuredSelection(
					// newRegion), true);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		btnNew.setText("New");

		Button button = new Button(grpRegion, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					if (getSelectedRegion() != null) {
						Region copy = EcoreUtil.copy(getSelectedRegion());
						copy.setRegionId(EcoreUtil.generateUUID());
						String regionNamePrefix = StringUtils
								.prefixBeforeInt(copy.getName());
						int largestIntInNames = StringUtils
								.largestIntAtEndStringsWithPrefix(
										getRegionNames(), regionNamePrefix);
						if (largestIntInNames != -1) {
							largestIntInNames++;
							copy.setName(regionNamePrefix + largestIntInNames);
						}
						editingDomain.getCommandStack().execute(
								AddCommand.create(editingDomain,
										regionDefinitionResourceUtil
												.getSequence(),
										RegiondefinitionPackage.eINSTANCE
												.getSequence_Region(), copy));
						// sequenceTableViewer.setSelection(
						// new StructuredSelection(copy), true);
					} else {
						MessageDialog msgd = new MessageDialog(parent
								.getShell(), "No region selected", null,
								"You have not selected a region to duplicate.",
								MessageDialog.ERROR, new String[] { "OK" }, 0);
						msgd.open();
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		button.setText("Duplicate");

		Button btnDelete = new Button(grpRegion, SWT.NONE);
		btnDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					Region selectedRegion = getSelectedRegion();
					// int index = regions.indexOf(selectedRegion);
					if (selectedRegion != null) {
						editingDomain.getCommandStack().execute(
								RemoveCommand.create(editingDomain,
										regionDefinitionResourceUtil
												.getSequence(),
										RegiondefinitionPackage.eINSTANCE
												.getSequence_Region(),
										selectedRegion));
						// if (index == regions.size()) {
						// sequenceTableViewer.setSelection(
						// new StructuredSelection(sequenceTableViewer
						// .getElementAt(index - 1)), true);
						// } else {
						// sequenceTableViewer.setSelection(
						// new StructuredSelection(sequenceTableViewer
						// .getElementAt(index)), true);
						// }
					} else {
						MessageDialog msgd = new MessageDialog(parent
								.getShell(), "No region selected", null,
								"You have not selected a region to delete.",
								MessageDialog.ERROR, new String[] { "OK" }, 0);
						msgd.open();
					}
				} catch (Exception e1) {
					logger.error("Cannot not get Editing Domain object.", e1);
				}
			}
		});
		btnDelete.setText("Delete");

		Button btnUndo = new Button(grpRegion, SWT.NONE);
		btnUndo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					// Region lastAffectedRegion = null;
					// Collection<?> affectedObjects = editingDomain
					// .getCommandStack().getMostRecentCommand()
					// .getAffectedObjects();
					// if (!affectedObjects.isEmpty()) {
					// Object object = affectedObjects.toArray()[0];
					// if (object instanceof Region) {
					// lastAffectedRegion = (Region) object;
					// }
					// }
					// int index = -1;
					// if (lastAffectedRegion != null) {
					// index = regions.indexOf(lastAffectedRegion);
					// if (index == regions.size() - 1) {
					// index--;
					// }
					// }
					editingDomain.getCommandStack().undo();
					// if (index != -1) {
					// sequenceTableViewer.setSelection(
					// new StructuredSelection(sequenceTableViewer
					// .getElementAt(index)), true);
					// }
				} catch (Exception e1) {
					logger.error("Cannot not get Editing Domain object.", e1);
				}
			}
		});
		btnUndo.setText("Undo");

		Button btnRedo = new Button(grpRegion, SWT.NONE);
		btnRedo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					editingDomain.getCommandStack().redo();
				} catch (Exception e1) {
					logger.error("Cannot not get Editing Domain object.", e1);
				}
			}
		});
		btnRedo.setText("Redo");

		Group grpActiveRegions = new Group(leftArea, SWT.NONE);
		GridData gd_grpActiveRegions = new GridData(GridData.FILL_HORIZONTAL);
		gd_grpActiveRegions.grabExcessHorizontalSpace = false;
		grpActiveRegions.setLayoutData(gd_grpActiveRegions);
		grpActiveRegions.setText("Active regions");
		grpActiveRegions.setLayout(new RowLayout());

		txtNumberActives = new Text(grpActiveRegions, SWT.BORDER | SWT.RIGHT);
		txtNumberActives.setLayoutData(new RowData(66, SWT.DEFAULT));
		txtNumberActives.setEditable(false);

		Group grpTotalTime = new Group(leftArea, SWT.None);
		GridData gd_grpTotalTime = new GridData(GridData.FILL_HORIZONTAL);
		gd_grpTotalTime.grabExcessHorizontalSpace = false;
		grpTotalTime.setLayoutData(gd_grpTotalTime);
		grpTotalTime.setText("Estimated Time");
		grpTotalTime.setLayout(new RowLayout());

		txtEstimatedTime = new Text(grpTotalTime, SWT.BORDER | SWT.RIGHT);
		txtEstimatedTime.setLayoutData(new RowData(77, SWT.DEFAULT));
		txtEstimatedTime.setEditable(false);

		new Label(leftArea, SWT.NONE);

		Group grpInfo = new Group(leftArea, SWT.NONE);
		GridData layoutData1 = new GridData(GridData.FILL_HORIZONTAL);
		layoutData1.horizontalSpan = 4;
		grpInfo.setLayoutData(layoutData1);
		grpInfo.setText("Info");
		grpInfo.setLayout(new GridLayout(3, false));

		Label lblLocation = new Label(grpInfo, SWT.NONE);
		lblLocation.setText("Location");

		txtLocation = new Text(grpInfo, SWT.BORDER);
		txtLocation.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false, 1, 1));
		txtLocation.setText("Location");
		txtLocation.setEditable(false);

		Label lblComments = new Label(grpInfo, SWT.NONE);
		lblComments.setText("Add comments below:");

		Label lblUser = new Label(grpInfo, SWT.NONE);
		lblUser.setText("User");

		txtUser = new Text(grpInfo, SWT.BORDER);
		txtUser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1,
				1));
		txtUser.setText("User");
		txtUser.setEditable(false);

		txtComments = new Text(grpInfo, SWT.BORDER | SWT.MULTI | SWT.WRAP);
		txtComments.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				if (e.getSource().equals(txtComments)) {
					try {
						String[] comments = txtComments.getText().split("\n");
						List<String> commentList = new ArrayList<String>();
						for (String string : comments) {
							commentList.add(string);
						}
						Spectrum spectrum = regionDefinitionResourceUtil
								.getSpectrum();
						updateFeature(spectrum,
								RegiondefinitionPackage.eINSTANCE
										.getSpectrum_Comments(), commentList);
						updateFeature(spectrum,
								RegiondefinitionPackage.eINSTANCE
										.getSpectrum_NumberOfComments(),
								spectrum.getComments().size());
					} catch (Exception e1) {
						logger.error(
								"Cannot get the spectrum from this sequence.",
								e1);
					}
				}
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (e.getSource().equals(txtComments)) {
					try {
						Spectrum spectrum = regionDefinitionResourceUtil
								.getSpectrum();
						updateFeature(spectrum,
								RegiondefinitionPackage.eINSTANCE
										.getSpectrum_Comments(), txtComments
										.getText());
						updateFeature(spectrum,
								RegiondefinitionPackage.eINSTANCE
										.getSpectrum_NumberOfComments(),
								spectrum.getComments().size());
					} catch (Exception e1) {
						logger.error(
								"Cannot get the spectrum from this sequence.",
								e1);
					}
				}
			}
		});
		txtComments.setText("comments");
		GridData gd_txtComments = new GridData(SWT.FILL, SWT.FILL, true, false,
				1, 1);
		gd_txtComments.verticalSpan = 3;
		txtComments.setLayoutData(gd_txtComments);

		Label lblSample = new Label(grpInfo, SWT.NONE);
		lblSample.setText("Sample");

		txtSample = new Text(grpInfo, SWT.BORDER);
		txtSample.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				if (e.getSource().equals(txtSample)) {
					try {
						updateFeature(regionDefinitionResourceUtil
								.getSpectrum(),
								RegiondefinitionPackage.eINSTANCE
										.getSpectrum_SampleName(), txtSample
										.getText());
					} catch (Exception e1) {
						logger.error(
								"Cannot get the spectrum from this sequence.",
								e1);
					}
				}
			}
		});
		txtSample.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		txtSample.setText("Sample");

		Label lblFileName = new Label(grpInfo, SWT.NONE);
		GridData gd_lblFileName = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd_lblFileName.widthHint = 59;
		lblFileName.setLayoutData(gd_lblFileName);
		lblFileName.setText("File Prefix");

		txtFilename = new Text(grpInfo, SWT.BORDER);
		txtFilename.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				if (e.getSource().equals(txtFilename)) {
					try {
						updateFeature(regionDefinitionResourceUtil
								.getSpectrum(),
								RegiondefinitionPackage.eINSTANCE
										.getSpectrum_FilenamePrefix(),
								txtFilename.getText());
					} catch (Exception e1) {
						logger.error(
								"Cannot get the spectrum from this sequence.",
								e1);
					}
				}
			}
		});
		GridData gd_txtFilename = new GridData(SWT.FILL, SWT.FILL, false,
				false, 1, 1);
		gd_txtFilename.widthHint = 104;
		txtFilename.setLayoutData(gd_txtFilename);
		txtFilename.setText("Filename Prefix");

		Group grpSequnceRunMode = new Group(rightArea, SWT.NONE);
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.widthHint = 220;
		layoutData.verticalSpan = 2;
		grpSequnceRunMode.setLayoutData(layoutData);
		grpSequnceRunMode.setLayout(new GridLayout(2, false));
		grpSequnceRunMode.setText("Sequence Run Mode");

		runMode = new Combo(grpSequnceRunMode, SWT.READ_ONLY);
		runMode.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (e.getSource().equals(runMode) && runMode.isFocusControl()) {
					try {
						updateFeature(regionDefinitionResourceUtil
								.getSequence(),
								RegiondefinitionPackage.eINSTANCE
										.getSequence_RunModeIndex(), runMode
										.getSelectionIndex());
						updateFeature(regionDefinitionResourceUtil
								.getSequence(),
								RegiondefinitionPackage.eINSTANCE
										.getSequence_RunMode(), runMode
										.getText());
					} catch (Exception e1) {
						logger.error("Cannot get the sequence", e);
					}
				}
			}
		});
		runMode.setItems(new String[] { "Normal", "Add Dimension" });
		runMode.setToolTipText("List of available sequence run modes");
		runMode.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		runMode.setText(runMode.getItem(0));

		new Label(grpSequnceRunMode, SWT.NONE);

		btnNumberOfIterations = new Button(grpSequnceRunMode, SWT.RADIO);
		btnNumberOfIterations.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (e.getSource().equals(btnNumberOfIterations)
						&& btnNumberOfIterations.getSelection()) {
					try {
						updateFeature(regionDefinitionResourceUtil
								.getSequence(),
								RegiondefinitionPackage.eINSTANCE
										.getSequence_NumInterationOption(),
								true);
						updateFeature(regionDefinitionResourceUtil
								.getSequence(),
								RegiondefinitionPackage.eINSTANCE
										.getSequence_RepeatUntilStopped(),
								false);
					} catch (Exception e1) {
						logger.error("Cannot get the sequence", e);
					}
				}
			}
		});
		btnNumberOfIterations.setText("Number of iterations");

		spinner = new Spinner(grpSequnceRunMode, SWT.BORDER);
		spinner.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (e.getSource().equals(spinner) && spinner.isFocusControl()) {
					try {
						updateFeature(regionDefinitionResourceUtil
								.getSequence(),
								RegiondefinitionPackage.eINSTANCE
										.getSequence_NumIterations(), spinner
										.getSelection());
					} catch (Exception e1) {
						logger.error("Cannot get the sequence", e);
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				if (e.getSource().equals(spinner) && spinner.isFocusControl()) {
					try {
						updateFeature(regionDefinitionResourceUtil
								.getSequence(),
								RegiondefinitionPackage.eINSTANCE
										.getSequence_NumIterations(), spinner
										.getSelection());
					} catch (Exception e1) {
						logger.error("Cannot get the sequence", e);
					}
				}
			}
		});
		spinner.setMinimum(1);
		spinner.setToolTipText("Set number of iterations required");

		btnRepeatuntilStopped = new Button(grpSequnceRunMode, SWT.RADIO);
		btnRepeatuntilStopped.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (e.getSource().equals(btnRepeatuntilStopped)
						&& btnRepeatuntilStopped.getSelection()) {
					try {
						updateFeature(regionDefinitionResourceUtil
								.getSequence(),
								RegiondefinitionPackage.eINSTANCE
										.getSequence_NumInterationOption(),
								false);
						updateFeature(regionDefinitionResourceUtil
								.getSequence(),
								RegiondefinitionPackage.eINSTANCE
										.getSequence_RepeatUntilStopped(), true);
					} catch (Exception e1) {
						logger.error("Cannot get the sequence", e);
					}
				}
			}
		});
		btnRepeatuntilStopped.setText("Repeat until stopped");

		new Label(grpSequnceRunMode, SWT.NONE);

		btnConfirmAfterEachInteration = new Button(grpSequnceRunMode, SWT.CHECK);
		btnConfirmAfterEachInteration
				.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						if (e.getSource().equals(btnConfirmAfterEachInteration)
								&& btnConfirmAfterEachInteration
										.isFocusControl()) {
							try {
								updateFeature(
										regionDefinitionResourceUtil
												.getSequence(),
										RegiondefinitionPackage.eINSTANCE
												.getSequence_ConfirmAfterEachIteration(),
										btnConfirmAfterEachInteration
												.getSelection());
							} catch (Exception e1) {
								logger.error("Cannot get the sequence", e);
							}
						}
					}
				});
		btnConfirmAfterEachInteration.setLayoutData(new GridData(
				GridData.FILL_HORIZONTAL));
		btnConfirmAfterEachInteration.setText("Confirm after each iteration");

		new Label(grpSequnceRunMode, SWT.NONE);

		Composite actionArea = new Composite(rootComposite, SWT.None);
		// Contains region editing, sequence parameters, file saving info and
		// comments.
		actionArea.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false,
				false, 1, 1));
		actionArea.setLayout(new RowLayout());

		Button btnStart = new Button(actionArea, SWT.NONE);
		btnStart.setText("Start");
		btnStart.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					// resource.save(null);
					doSave(new NullProgressMonitor());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				// TODO the following need to run on a work thread, not on GUI
				// thread??
				for (Region region : regions) {
					if (region.isEnabled()) {
						// send region parameters to EPICS driver
						// set a region running status before start collection
						// in EPICS for this region
						// status should be reset by monitor EPICS State PV
						// wait for EPICS collection to finish i.e. status is
						// not RUNNING before start next
						// TODO using QUEUE here?
					}
				}
			}
		});
		btnStart.setToolTipText("Save the sequence data to file, then start collection");
		initialisation();
		// register as selection provider to the SelectionService
		getViewSite().setSelectionProvider(this);
		getViewSite()
				.getWorkbenchWindow()
				.getSelectionService()
				.addSelectionListener(RegionViewExtensionFactory.ID,
						selectionListener);

	}

	protected List<String> getRegionNames() {
		List<String> regionNames = new ArrayList<String>();
		for (Region region : regions) {
			regionNames.add(region.getName());
		}
		return regionNames;
	}

	private ISelectionListener selectionListener = new INullSelectionListener() {
		@Override
		public void selectionChanged(IWorkbenchPart part, ISelection selection) {
			if (selection instanceof IStructuredSelection) {
				IStructuredSelection sel = (IStructuredSelection) selection;
				Object firstElement = sel.getFirstElement();
				if (firstElement instanceof Region) {
					sequenceTableViewer.setSelection(sel);
				}
			}
		}
	};

	private EditingDomain editingDomain;

	private Region getSelectedRegion() {
		ISelection selection = getSelection();
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSel = (IStructuredSelection) selection;
			Object firstElement = structuredSel.getFirstElement();
			if (firstElement instanceof Region) {
				Region region = (Region) firstElement;
				return region;
			}
		}
		return null;
	}

	private void initialisation() {
		try {
			editingDomain = regionDefinitionResourceUtil.getEditingDomain();
		} catch (Exception e) {
			logger.error("Cannot get editing domain object.", e);
		}
		if (editingDomain == null) {
			throw new RuntimeException("Cannot get editing domain object.");
		}

		if (regionDefinitionResourceUtil != null) {
			try {
				sequence = regionDefinitionResourceUtil.getSequence();
			} catch (Exception e) {
				logger.error("Cannot get sequence from resource.", e);
			}
		}
		if (sequence != null) {
			spectrum = sequence.getSpectrum();
			runMode.setText(runMode.getItem(sequence.getRunMode().getValue()));
			btnNumberOfIterations
					.setSelection(!sequence.isRepeatUntilStopped());
			btnRepeatuntilStopped.setSelection(sequence.isRepeatUntilStopped());
			btnConfirmAfterEachInteration.setSelection(sequence
					.isConfirmAfterEachIteration());
			spinner.setSelection(sequence.getNumIterations());

			if (spectrum != null) {
				if (getLocation() != null) {
					txtLocation.setText(getLocation());
				} else {
					txtLocation.setText("Diamond Beamline");
				}
				if (!spectrum.getLocation().equals(txtLocation.getText())) {
					updateFeature(spectrum,
							RegiondefinitionPackage.eINSTANCE
									.getSpectrum_Location(),
							txtLocation.getText());
				}
				if (getVisit() != null) {
					// Obtain visit proposal from GDA property RCP_APP_VISIT
					txtUser.setText(getVisit());
				} else if (getUser() != null) {
					// set by Spring configuration
					txtUser.setText(getUser());
				} else {
					// default to user home folder
					txtUser.setText(System.getProperty("user.name"));
				}
				if (!spectrum.getUser().equals(txtUser.getText())) {
					updateFeature(spectrum,
							RegiondefinitionPackage.eINSTANCE
									.getSpectrum_User(), txtUser.getText());
				}
				txtSample.setText(spectrum.getSampleName());
				txtFilename.setText(spectrum.getFilenamePrefix());
				String comments = "";
				for (String comment : spectrum.getComments()) {
					comments += comment + "\n";
				}
				txtComments.setText(comments);
				System.out.println(txtComments.getText());
			}
			regions = sequence.getRegion();
		} else {
			// start a new sequence
			if (regionDefinitionResourceUtil != null) {
				try {
					sequence = regionDefinitionResourceUtil.createSequence();
				} catch (Exception e) {
					logger.error("Cannot create new sequence file", e);
				}
			}
		}
		sequenceTableViewer.addDragSupport(DND.DROP_COPY | DND.DROP_MOVE
				| DND.DROP_LINK,
				new Transfer[] { LocalTransfer.getInstance() },
				new ViewerDragAdapter(sequenceTableViewer));

		sequenceTableViewer.addDropSupport(DND.DROP_COPY | DND.DROP_MOVE
				| DND.DROP_LINK,
				new Transfer[] { LocalTransfer.getInstance() },
				new EditingDomainViewerDropAdapter(editingDomain,
						sequenceTableViewer));

//		sequenceTableViewer.setSelection(new StructuredSelection(
//				sequenceTableViewer.getElementAt(0)), true);
		updateCalculatedData();
	}

	private void updateCalculatedData() {
		int numActives = 0;
		double totalTimes = 0.0;
		if (!regions.isEmpty()) {
			for (Region region : regions) {
				if (region.isEnabled()) {
					numActives++;
					totalTimes += region.getStepTime()
							* RegionStepsTimeEstimation
									.calculateTotalSteps(
											(region.getHighEnergy() - region
													.getLowEnergy()),
											region.getEnergyStep(),
											camera.getEnergyResolution()
													* region.getPassEnergy()
													* (region.getLastXChannel()
															- region.getFirstXChannel() + 1));
				}
			}
		}
		txtNumberActives.setText(String.format("%d", numActives));
		txtEstimatedTime.setText(String.format("%.3f", totalTimes));
	}

	@Override
	public void setFocus() {
		sequenceTableViewer.getTable().setFocus();
	}

	public void setViewPartName(String viewPartName) {
		setPartName(viewPartName);
	}

	public void setRegionDefinitionResourceUtil(
			RegionDefinitionResourceUtil regionDefinition) {
		this.regionDefinitionResourceUtil = regionDefinition;
	}

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		selectionChangedListeners.add(listener);
	}

	@Override
	public ISelection getSelection() {
		return sequenceTableViewer.getSelection();
	}

	@Override
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		selectionChangedListeners.remove(listener);
	}

	@Override
	public void setSelection(ISelection selection) {

	}

	private void fireSelectionChanged(Region region) {
		ISelection sel = StructuredSelection.EMPTY;
		if (region != null) {
			sel = new StructuredSelection(region);
		}
		SelectionChangedEvent event = new SelectionChangedEvent(this, sel);
		for (ISelectionChangedListener listener : selectionChangedListeners) {
			listener.selectionChanged(event);
		}

	}

	private class SequenceColumnEditingSupport extends EditingSupport {

		private String columnIdentifier;
		private Table table;

		public SequenceColumnEditingSupport(ColumnViewer viewer,
				TableViewerColumn tableViewerColumn) {
			super(viewer);
			table = ((TableViewer) viewer).getTable();
			columnIdentifier = tableViewerColumn.getColumn().getText();
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			if (SequenceTableConstants.ENABLED.equals(columnIdentifier)) {
				return new CheckboxCellEditor(table);
			}
			return null;
		}

		@Override
		protected boolean canEdit(Object element) {
			if (SequenceTableConstants.ENABLED.equals(columnIdentifier)) {
				return true;
			}
			return false;
		}

		@Override
		protected Object getValue(Object element) {
			if (element instanceof Region) {
				Region region = (Region) element;
				if (SequenceTableConstants.ENABLED.equals(columnIdentifier)) {
					return region.isEnabled();
				}
			}
			return null;
		}

		@Override
		protected void setValue(Object element, Object value) {
			if (SequenceTableConstants.ENABLED.equals(columnIdentifier)) {
				if (value instanceof Boolean) {
					try {
						runCommand(SetCommand.create(editingDomain, element,
								RegiondefinitionPackage.eINSTANCE
										.getRegion_Enabled(), value));
						if (element instanceof Region) {
							Region region = (Region) element;
							fireSelectionChanged(region);
							updateCalculatedData();
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	protected void runCommand(final Command rmCommand) throws Exception {
		editingDomain.getCommandStack().execute(rmCommand);
	}

	public Camera getCamera() {
		return camera;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}
	/**
	 * refresh the table viewer with the sequence file name provided. 
	 * If it is a new file, an empty sequence will be created.
	 */
	public void refreshTable(String seqFileName, boolean newFile) {
		try {
			resource.eAdapters().remove(notifyListener);
			regionDefinitionResourceUtil.setFileName(seqFileName);
			if (newFile) {
				regionDefinitionResourceUtil.createSequence();
			}
			regionDefinitionResourceUtil.setFileChanged(true);
			Resource sequenceRes = regionDefinitionResourceUtil.getResource();
			sequenceTableViewer.setInput(sequenceRes);
			//update the resource in this view.
			resource=sequenceRes;
			resource.eAdapters().add(notifyListener);
			
			// if the sequence is empty - then fire null
			// replace existing regions list
			regions = regionDefinitionResourceUtil.getRegions();
			if (regions.isEmpty()) {
				fireSelectionChanged(null);
			} else {
				// otherwise fire the first region
				fireSelectionChanged(regions.get(0));
			}
		} catch (Exception e) {
			logger.error("Cannot refresh table.", e);
		}
	}

	@Override
	public RegionDefinitionResourceUtil getRegionDefinitionResourceUtil() {
		return regionDefinitionResourceUtil;
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		try {
			regionDefinitionResourceUtil.getResource().save(null);
			isDirty = false;
			firePropertyChange(PROP_DIRTY);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub
		SaveAsDialog sad = new SaveAsDialog(this.getViewSite().getShell());
		sad.setBlockOnOpen(isDirty());
		sad.open();

	}

	@Override
	public boolean isDirty() {
		return isDirty;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	@Override
	public boolean isSaveOnCloseNeeded() {
		return true;
	}

	private Adapter notifyListener = new EContentAdapter() {

		@Override
		public void notifyChanged(Notification notification) {
			super.notifyChanged(notification);
			if (notification.getFeature() !=null && !notification.getFeature().equals("null") && notification.getNotifier() != null) {
				isDirty = true;
				firePropertyChange(PROP_DIRTY);
			}
		}

	};

	public void dispose() {
		try {
			regionDefinitionResourceUtil.getResource().eAdapters()
					.remove(notifyListener);
			getViewSite()
					.getWorkbenchWindow()
					.getSelectionService()
					.removeSelectionListener(RegionViewExtensionFactory.ID,
							selectionListener);
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.dispose();
	}

	// Update features when it changes in Region Editor
	private void updateFeature(EObject region, Object feature, Object value) {
		if (region != null) {
			if (editingDomain != null) {
				Command setNameCmd = SetCommand.create(editingDomain, region,
						feature, value);
				editingDomain.getCommandStack().execute(setNameCmd);
			}
		}
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getVisit() {
		if (visit != null) {
			visit = LocalProperties.get(LocalProperties.RCP_APP_VISIT);
		}
		return visit;
	}

	public void setVisit(String visit) {
		this.visit = visit;
	}

}
