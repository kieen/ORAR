package orar.factory;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;

public interface ConceptTypeFactory {

	/**
	 * @param conceptType:
	 *            a set of OWLClass/concept names
	 * @return reused conceptType, which means if that conceptType equals to
	 *         some conceptType in the factory before, then reuse the existing
	 *         one.
	 */
	public Set<OWLClass> getConceptType(Set<OWLClass> conceptType);
	
	public int getSize();
	public void clear();
}
