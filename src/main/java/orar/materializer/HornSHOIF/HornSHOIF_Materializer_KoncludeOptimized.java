package orar.materializer.HornSHOIF;

import org.semanticweb.owlapi.model.OWLOntology;

import orar.innerreasoner.InnerReasoner;
import orar.innerreasoner.HornSHOIF.Konclude_HornSHOIF_InnerReasoner2;
import orar.modeling.ontology.OrarOntology;

public class HornSHOIF_Materializer_KoncludeOptimized extends HornSHOIF_MaterializerOptimized {
	private int port = 8080;

	public HornSHOIF_Materializer_KoncludeOptimized(OrarOntology normalizedOrarOntology) {
		super(normalizedOrarOntology);

	}

	public HornSHOIF_Materializer_KoncludeOptimized(OrarOntology normalizedOrarOntology, int port) {
		super(normalizedOrarOntology);
		this.port = port;
	}

	@Override
	protected InnerReasoner getInnerReasoner(OWLOntology abstraction) {
		InnerReasoner reasoner;
		if (this.port == 8080) {
			reasoner = new Konclude_HornSHOIF_InnerReasoner2(abstraction);
		} else {
			reasoner = new Konclude_HornSHOIF_InnerReasoner2(abstraction, port);
		}

		return reasoner;
	}

}
