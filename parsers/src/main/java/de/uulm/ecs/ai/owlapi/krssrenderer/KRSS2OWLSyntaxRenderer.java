/*
 * This file is part of the OWL API.
 *
 * The contents of this file are subject to the LGPL License, Version 3.0.
 *
 * Copyright (C) 2011, Ulm University
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
 * Copyright 2011, Ulm University
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
package de.uulm.ecs.ai.owlapi.krssrenderer;

import java.io.IOException;
import java.io.Writer;

import org.semanticweb.owlapi.io.AbstractOWLRenderer;
import org.semanticweb.owlapi.io.OWLRendererException;
import org.semanticweb.owlapi.io.OWLRendererIOException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLRuntimeException;

/** @author Olaf Noppens */
public class KRSS2OWLSyntaxRenderer extends AbstractOWLRenderer {
    @SuppressWarnings({ "javadoc", "unused" })
    @Deprecated
    public KRSS2OWLSyntaxRenderer(OWLOntologyManager owlOntologyManager) {
}

    @SuppressWarnings("javadoc")
    public KRSS2OWLSyntaxRenderer() {}

    @Override
    public void render(OWLOntology ontology, Writer writer) throws OWLRendererException {
        try {
            KRSS2OWLObjectRenderer ren = new KRSS2OWLObjectRenderer(ontology, writer);
            ontology.accept(ren);
            writer.flush();
        } catch (IOException io) {
            throw new OWLRendererIOException(io);
        } catch (OWLRuntimeException e) {
            throw new OWLRendererException(e);
        }
    }
}
