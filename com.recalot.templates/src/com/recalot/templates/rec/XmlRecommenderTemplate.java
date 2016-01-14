// Copyright (C) 2016 Matthäus Schmedding
//
// This file is part of recalot.com.
//
// recalot.com is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// recalot.com is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with recalot.com. If not, see <http://www.gnu.org/licenses/>.

package com.recalot.templates.rec;

import com.recalot.common.builder.RecommenderBuilder;
import com.recalot.common.communication.RecommendationResult;
import com.recalot.common.communication.TemplateResult;
import com.recalot.common.interfaces.model.rec.Recommender;
import com.recalot.common.interfaces.model.rec.RecommenderInformation;
import com.recalot.common.interfaces.template.RecommenderTemplate;
import com.recalot.templates.base.XmlBaseTemplate;

import java.io.IOException;

/**
 * @author matthaeus.schmedding
 */
public class XmlRecommenderTemplate extends XmlBaseTemplate implements RecommenderTemplate  {

    @Override
    public TemplateResult transform(RecommenderInformation[] recommenders) {
        return null;
    }

    @Override
    public TemplateResult transform(Recommender recommender) {
        return null;
    }

    @Override
    public TemplateResult transform(RecommenderBuilder builder) {
        return null;
    }

    @Override
    public TemplateResult transform(RecommendationResult recommend) {
        return null;
    }

    @Override
    public void close() throws IOException {

    }
}
