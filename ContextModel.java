package com.cmid.context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Iterator;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelChangedListener;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.rulesys.RDFSRuleReasonerFactory;
import com.hp.hpl.jena.util.ModelLoader;

public class ContextModel {

	
	final public String ex = "http://liris.cnrs.fr/smartspace.owl#";
	
	private ModelChangedListener forAdding;

	/** The model that stores context ontology information */
	private OntModel contextOntology;
	/** The model that stores static context data */
	private OntModel staticContexts;
	/** The model that stores historical sensed context data */
	private OntModel storedSensedContexts;
	/** The model that stores sensed context data */
	private OntModel sensedContexts;
	/** The model that stores all context data */
	private OntModel contextData;
	/**
	 * The inference model containinig all context data, with further RDFS
	 * reasoning performed on it
	 */
	private InfModel baseModel;

	/** For answering incoming subscribed queries */
	// private MessageReceiver smr;

	/**
	 * Construct the ContextModel
	 * 1. Initial Context Model(basicOntModel+scenarioOntModel)
	 * 2. Initial Context Data()
	 * @throws FileNotFoundException 
	 * @parameter ModelChangedListener
	 *  
	 */
	public ContextModel(ModelChangedListener modelListener) throws FileNotFoundException
	// ContextPeer contextPeer)
	{
		
		
		OntModel basicOntModel = ModelFactory.createOntologyModel();
		basicOntModel.read(new FileInputStream(new File("smartspace.owl")), "");
		
		OntModel scenarioOntModel = ModelFactory.createOntologyModel();
		basicOntModel.read(new FileInputStream(new File("smartspace_scenario.owl")), "");
		//Initial context model
		contextOntology = (OntModel) basicOntModel.add(scenarioOntModel);
		
		System.out.println("[ContextModel] - Ontology model created");
		
		//Initial context data for the specific scenario
		staticContexts = ModelFactory.createOntologyModel();
		basicOntModel.read(new FileInputStream(new File("smartspace_static.owl")), "");
		
		storedSensedContexts = ModelFactory.createOntologyModel();
		basicOntModel.read(new FileInputStream(new File("smartspace_sensed.owl")), "");

		System.out.println("[ContextModel] - Historical sensed contexts loaded");
		// Dynamically updated sensed context data (we should register a
		// ModelChangedListener to this model)
		sensedContexts = ModelFactory.createOntologyModel();
		sensedContexts.add(storedSensedContexts);
		sensedContexts.register(forAdding);

		// Create the base model
		Reasoner RDFSReasoner = RDFSRuleReasonerFactory.theInstance().create(
				null);
		// Reasoner OWLReasoner =
		// OWLFBRuleReasonerFactory.theInstance().create(null);
		contextData = ModelFactory.createOntologyModel();;
		contextData.add(contextOntology);
		contextData.add(staticContexts);
		contextData.add(storedSensedContexts);
		contextData.add(sensedContexts);
		baseModel = ModelFactory.createInfModel(RDFSReasoner, contextData);
		baseModel.prepare();
		// baseModel = ModelFactory.createInfModel(OWLReasoner, baseModel);
		// baseModel.prepare();
	}
	
	public Model getContextOntology()
	{
		return contextOntology;
	}
	
	public Model getSensedContexts()
	{
		return sensedContexts;
	}
	
	public Model getStoredSensedContexts()
	{
		return storedSensedContexts;
	}
	
	public Model getStaticContexts()
	{
		return staticContexts;
	}
	
	public Model getContextData()
	{
		return contextData;
	}
	
	public Model getStaticBaseModel()
	{
		// Create the static base model
		Reasoner RDFSReasoner = RDFSRuleReasonerFactory.theInstance().create(null);
		Model staticContextData = ModelFactory.createOntologyModel();
		staticContextData.add(contextOntology);
		staticContextData.add(staticContexts);
		InfModel staticBaseModel = ModelFactory.createInfModel(RDFSReasoner, staticContextData);
	    staticBaseModel.prepare();
	    
	    return staticBaseModel;
	}
	

}
