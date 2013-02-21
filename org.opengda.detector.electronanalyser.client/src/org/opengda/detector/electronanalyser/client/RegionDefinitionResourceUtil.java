package org.opengda.detector.electronanalyser.client;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.opengda.detector.electronanalyser.model.regiondefinition.api.DocumentRoot;
import org.opengda.detector.electronanalyser.model.regiondefinition.api.Region;
import org.opengda.detector.electronanalyser.model.regiondefinition.api.RegiondefinitionFactory;
import org.opengda.detector.electronanalyser.model.regiondefinition.api.RegiondefinitionPackage;
import org.opengda.detector.electronanalyser.model.regiondefinition.api.Sequence;
import org.opengda.detector.electronanalyser.model.regiondefinition.api.Spectrum;
import org.opengda.detector.electronanalyser.model.regiondefinition.util.RegiondefinitionResourceFactoryImpl;

public class RegionDefinitionResourceUtil {

	private String fileName;

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Resource getResource() throws Exception {
		ResourceSet resourceSet = getResourceSet();
		URI fileURI = URI.createFileURI(fileName);
		return resourceSet.getResource(fileURI, true);
	}

	public List<Region> getRegions(boolean shouldCreate) throws Exception {
		
		 Sequence sequence = getSequence(shouldCreate);
		 if(sequence != null) {
			 return sequence.getRegion();
		 }
		 return Collections.emptyList();
	}

	public Sequence getSequence(boolean shouldCreate) throws Exception {
		ResourceSet resourceSet = getResourceSet();

		// Register the appropriate resource factory to handle all file
		// extensions.
		//

		Resource res = getResource();
		if (res == null && shouldCreate) {
			Resource resource = resourceSet.createResource(URI
					.createURI(fileName));
			DocumentRoot root = RegiondefinitionFactory.eINSTANCE
					.createDocumentRoot();
			resource.getContents().add(root);
			try {
				resource.save(null);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (res != null) {
			List<EObject> contents = res.getContents();
			EObject eobj = contents.get(0);
			if (eobj instanceof DocumentRoot) {
				DocumentRoot root = (DocumentRoot) eobj;
				return root.getSequence();
			}

		}

		return null;
	}

	public Spectrum getSpectrum(boolean shouldCreate) throws Exception {
		return getSequence(shouldCreate).getSpectrum();
	}
	
	private ResourceSet getResourceSet() throws Exception {
		EditingDomain sequenceEditingDomain = ElectronAnalyserClientPlugin
				.getDefault().getSequenceEditingDomain();
		ResourceSet resourceSet = sequenceEditingDomain.getResourceSet();
		resourceSet
				.getResourceFactoryRegistry()
				.getExtensionToFactoryMap()
				.put(Resource.Factory.Registry.DEFAULT_EXTENSION,
						new RegiondefinitionResourceFactoryImpl());

		// Register the package to ensure it is available during loading.
		//
		resourceSet.getPackageRegistry().put(RegiondefinitionPackage.eNS_URI,
				RegiondefinitionPackage.eINSTANCE);
		return resourceSet;
	}

	public void save(Resource res) throws IOException {
		res.save(null);
	}

	public EditingDomain getEditingDomain() throws Exception {
		return ElectronAnalyserClientPlugin.getDefault()
				.getSequenceEditingDomain();
	}
	private boolean sourceSelectable;

	public boolean isSourceSelectable() {
		return sourceSelectable;
	}

	public void setSourceSelectable(boolean sourceSelectable) {
		this.sourceSelectable = sourceSelectable;
	}

	public double getXRaySourceEnergyLimit() {
		return xRaySourceEnergyLimit;
	}

	public void setXRaySourceEnergyLimit(double xRaySourceEnergyLimit) {
		this.xRaySourceEnergyLimit = xRaySourceEnergyLimit;
	}

	private double xRaySourceEnergyLimit;

}
