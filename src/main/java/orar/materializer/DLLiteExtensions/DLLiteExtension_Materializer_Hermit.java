package orar.materializer.DLLiteExtensions;

import org.semanticweb.owlapi.model.OWLOntology;

import orar.innerreasoner.InnerReasoner;
import orar.innerreasoner.HornSHOIF.Hermit_HornSHOIF_InnerReasoner;
import orar.modeling.ontology.OrarOntology;

public class DLLiteExtension_Materializer_Hermit extends DLLiteExtension_Materializer {

	public DLLiteExtension_Materializer_Hermit(OrarOntology normalizedOrarOntology) {
		super(normalizedOrarOntology);

	}

	@Override
	protected InnerReasoner getInnerReasoner(OWLOntology abstraction) {
		InnerReasoner reasoner = new Hermit_HornSHOIF_InnerReasoner(abstraction);
		return reasoner;
	}

}
