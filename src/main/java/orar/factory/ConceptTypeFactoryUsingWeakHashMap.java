package orar.factory;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.semanticweb.owlapi.model.OWLClass;

public class ConceptTypeFactoryUsingWeakHashMap implements ConceptTypeFactory {
	private final Map<Set<OWLClass>, WeakReference<Set<OWLClass>>> cache;
	private static ConceptTypeFactoryUsingWeakHashMap instance;

	private ConceptTypeFactoryUsingWeakHashMap() {
		this.cache = new WeakHashMap<Set<OWLClass>, WeakReference<Set<OWLClass>>>();

	}

	public static ConceptTypeFactory getInstance() {
		if (instance == null) {
			instance = new ConceptTypeFactoryUsingWeakHashMap();
		}
		return instance;

	}

	@Override
	public Set<OWLClass> getConceptType(Set<OWLClass> conceptType) {
		WeakReference<Set<OWLClass>> valueOfNewType = this.cache.get(conceptType);
		if (valueOfNewType == null) {
			this.cache.put(conceptType, new WeakReference<Set<OWLClass>>(conceptType));
			return conceptType;
		}

		return valueOfNewType.get();

	}

	@Override
	public void clear() {
		this.cache.clear();

	}

	@Override
	public int getSize() {

		return this.cache.size();
	}

}
