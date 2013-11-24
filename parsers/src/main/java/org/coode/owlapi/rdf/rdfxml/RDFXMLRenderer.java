/*
 * This file is part of the OWL API.
 *
 * The contents of this file are subject to the LGPL License, Version 3.0.
 *
 * Copyright (C) 2011, The University of Manchester
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
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 *
 *
 * Alternatively, the contents of this file may be used under the terms of the Apache License, Version 2.0
 * in which case, the provisions of the Apache License Version 2.0 are applicable instead of those above.
 *
 * Copyright 2011, University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.coode.owlapi.rdf.rdfxml;

import static org.semanticweb.owlapi.vocab.OWLRDFVocabulary.RDF_DESCRIPTION;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.coode.owlapi.rdf.model.RDFLiteralNode;
import org.coode.owlapi.rdf.model.RDFNode;
import org.coode.owlapi.rdf.model.RDFResourceNode;
import org.coode.owlapi.rdf.model.RDFTriple;
import org.coode.owlapi.rdf.renderer.RDFRendererBase;
import org.coode.xml.XMLWriterFactory;
import org.semanticweb.owlapi.io.RDFOntologyFormat;
import org.semanticweb.owlapi.io.XMLUtils;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.VersionInfo;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

/** Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 06-Dec-2006<br>
 * <br> */
public class RDFXMLRenderer extends RDFRendererBase {
    private RDFXMLWriter writer;
    private Set<RDFResourceNode> pending = new HashSet<RDFResourceNode>();
    private RDFXMLNamespaceManager qnameManager;
    private OWLOntologyFormat format;

    @SuppressWarnings("javadoc")
    @Deprecated
    public RDFXMLRenderer(OWLOntologyManager manager, OWLOntology ontology, Writer w) {
        this(ontology, w, ontology.getOWLOntologyManager().getOntologyFormat(ontology));
    }

    /** @param ontology
     * @param w */
    public RDFXMLRenderer(OWLOntology ontology, Writer w) {
        this(ontology, w, ontology.getOWLOntologyManager().getOntologyFormat(ontology));
    }

    @SuppressWarnings("javadoc")
    @Deprecated
    public RDFXMLRenderer(OWLOntologyManager manager, OWLOntology ontology, Writer w,
            OWLOntologyFormat format) {
        this(ontology, w, format);
    }

    /** @param ontology
     * @param w
     * @param format */
    public RDFXMLRenderer(OWLOntology ontology, Writer w, OWLOntologyFormat format) {
        super(ontology, format);
        this.format = format;
        qnameManager = new RDFXMLNamespaceManager(ontology, format);
        String defaultNamespace = qnameManager.getDefaultNamespace();
        String base;
        if (defaultNamespace.endsWith("#")) {
            base = defaultNamespace.substring(0, defaultNamespace.length() - 1);
        } else {
            base = defaultNamespace;
        }
        writer = new RDFXMLWriter(XMLWriterFactory.getInstance().createXMLWriter(w,
                qnameManager, base));
        prettyPrintedTypes = new HashSet<IRI>();
        prettyPrintedTypes.add(OWLRDFVocabulary.OWL_CLASS.getIRI());
        prettyPrintedTypes.add(OWLRDFVocabulary.OWL_OBJECT_PROPERTY.getIRI());
        prettyPrintedTypes.add(OWLRDFVocabulary.OWL_DATA_PROPERTY.getIRI());
        prettyPrintedTypes.add(OWLRDFVocabulary.OWL_ANNOTATION_PROPERTY.getIRI());
        prettyPrintedTypes.add(OWLRDFVocabulary.OWL_RESTRICTION.getIRI());
        prettyPrintedTypes.add(OWLRDFVocabulary.OWL_THING.getIRI());
        prettyPrintedTypes.add(OWLRDFVocabulary.OWL_NOTHING.getIRI());
        prettyPrintedTypes.add(OWLRDFVocabulary.OWL_ONTOLOGY.getIRI());
        prettyPrintedTypes.add(OWLRDFVocabulary.OWL_ANNOTATION_PROPERTY.getIRI());
        prettyPrintedTypes.add(OWLRDFVocabulary.OWL_NAMED_INDIVIDUAL.getIRI());
        prettyPrintedTypes.add(OWLRDFVocabulary.RDFS_DATATYPE.getIRI());
        prettyPrintedTypes.add(OWLRDFVocabulary.OWL_AXIOM.getIRI());
        prettyPrintedTypes.add(OWLRDFVocabulary.OWL_ANNOTATION.getIRI());
    }

    /** @return unserializable entities */
    public Set<OWLEntity> getUnserialisableEntities() {
        return qnameManager.getEntitiesWithInvalidQNames();
    }

    @Override
    protected void beginDocument() throws IOException {
        writer.startDocument();
    }

    @Override
    protected void endDocument() throws IOException {
        writer.endDocument();
        writer.writeComment(VersionInfo.getVersionInfo().getGeneratedByMessage());
        if (format instanceof RDFOntologyFormat
                && !((RDFOntologyFormat) format).isAddMissingTypes()) {
            // missing type declarations could have been omitted, adding a
            // comment to document it
            writer.writeComment("Warning: type declarations were not added automatically.");
        }
    }

    @Override
    protected void writeIndividualComments(OWLNamedIndividual ind) throws IOException {
        writer.writeComment(XMLUtils.escapeXML(ind.getIRI().toString()));
    }

    @Override
    protected void writeClassComment(OWLClass cls) throws IOException {
        writer.writeComment(XMLUtils.escapeXML(cls.getIRI().toString()));
    }

    @Override
    protected void writeDataPropertyComment(OWLDataProperty prop) throws IOException {
        writer.writeComment(XMLUtils.escapeXML(prop.getIRI().toString()));
    }

    @Override
    protected void writeObjectPropertyComment(OWLObjectProperty prop) throws IOException {
        writer.writeComment(XMLUtils.escapeXML(prop.getIRI().toString()));
    }

    @Override
    protected void writeAnnotationPropertyComment(OWLAnnotationProperty prop)
            throws IOException {
        writer.writeComment(XMLUtils.escapeXML(prop.getIRI().toString()));
    }

    @Override
    protected void writeDatatypeComment(OWLDatatype datatype) throws IOException {
        writer.writeComment(XMLUtils.escapeXML(datatype.getIRI().toString()));
    }

    @Override
    protected void writeBanner(String name) throws IOException {
        writer.writeComment("\n///////////////////////////////////////////////////////////////////////////////////////\n"
                + "//\n"
                + "// "
                + name
                + "\n"
                + "//\n"
                + "///////////////////////////////////////////////////////////////////////////////////////\n");
    }

    @Override
    public void render(RDFResourceNode node) throws IOException {
        if (pending.contains(node)) {
            return;
        }
        pending.add(node);
        RDFTriple candidatePrettyPrintTypeTriple = null;
        final List<RDFTriple> triples = getGraph().getSortedTriplesForSubject(node, true);
        for (RDFTriple triple : triples) {
            IRI propertyIRI = triple.getProperty().getIRI();
            if (propertyIRI.equals(OWLRDFVocabulary.RDF_TYPE.getIRI())
                    && !triple.getObject().isAnonymous()) {
                if (OWLRDFVocabulary.BUILT_IN_VOCABULARY_IRIS.contains(triple.getObject()
                        .getIRI())) {
                    if (prettyPrintedTypes.contains(triple.getObject().getIRI())) {
                        candidatePrettyPrintTypeTriple = triple;
                    }
                }
            }
        }
        if (candidatePrettyPrintTypeTriple == null) {
            writer.writeStartElement(RDF_DESCRIPTION.getIRI());
        } else {
            writer.writeStartElement(candidatePrettyPrintTypeTriple.getObject().getIRI());
        }
        if (!node.isAnonymous()) {
            writer.writeAboutAttribute(node.getIRI());
        }
        // XXX this call looks like it should be made, but only when the node id
        // is necessary
        // else {
        // writer.writeNodeIDAttribute(node);
        // }
        for (RDFTriple triple : triples) {
            if (candidatePrettyPrintTypeTriple != null
                    && candidatePrettyPrintTypeTriple.equals(triple)) {
                continue;
            }
            writer.writeStartElement(triple.getProperty().getIRI());
            RDFNode objectNode = triple.getObject();
            if (!objectNode.isLiteral()) {
                RDFResourceNode objectRes = (RDFResourceNode) objectNode;
                if (objectRes.isAnonymous()) {
                    // Special rendering for lists
                    if (isObjectList(objectRes)) {
                        writer.writeParseTypeAttribute();
                        List<RDFNode> list = new ArrayList<RDFNode>();
                        toJavaList(objectRes, list);
                        for (RDFNode n : list) {
                            if (n.isAnonymous()) {
                                render((RDFResourceNode) n);
                            } else {
                                if (n.isLiteral()) {
                                    RDFLiteralNode litNode = (RDFLiteralNode) n;
                                    writer.writeStartElement(OWLRDFVocabulary.RDFS_LITERAL
                                            .getIRI());
                                    if (litNode.getDatatype() != null) {
                                        writer.writeDatatypeAttribute(litNode
                                                .getDatatype());
                                    } else if (litNode.getLang() != null) {
                                        writer.writeLangAttribute(litNode.getLang());
                                    }
                                    writer.writeTextContent(litNode.getLiteral());
                                    writer.writeEndElement();
                                } else {
                                    writer.writeStartElement(RDF_DESCRIPTION.getIRI());
                                    writer.writeAboutAttribute(n.getIRI());
                                    writer.writeEndElement();
                                }
                            }
                        }
                    } else {
                        render(objectRes);
                    }
                } else {
                    writer.writeResourceAttribute(objectRes.getIRI());
                }
            } else {
                RDFLiteralNode rdfLiteralNode = (RDFLiteralNode) objectNode;
                if (rdfLiteralNode.getDatatype() != null) {
                    writer.writeDatatypeAttribute(rdfLiteralNode.getDatatype());
                } else if (rdfLiteralNode.getLang() != null) {
                    writer.writeLangAttribute(rdfLiteralNode.getLang());
                }
                writer.writeTextContent(rdfLiteralNode.getLiteral());
            }
            writer.writeEndElement();
        }
        writer.writeEndElement();
        pending.remove(node);
    }
}
