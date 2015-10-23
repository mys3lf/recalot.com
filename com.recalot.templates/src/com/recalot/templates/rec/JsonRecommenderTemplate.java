// Copyright (C) 2015 Matthäus Schmedding
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
import com.recalot.templates.base.JsonBaseTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * @author matthaeus.schmedding
 */
public class JsonRecommenderTemplate extends JsonBaseTemplate implements RecommenderTemplate  {
    @Override
    public TemplateResult transform(RecommenderInformation[] recommenders) {
        String result = getSerializer().include("id", "key", "state").exclude("*").serialize(recommenders);

        return new TemplateResult(200, MimeType, new ByteArrayInputStream(result.getBytes(charset)), charset);
    }

    @Override
    public TemplateResult transform(Recommender recommender) {
        String result = getSerializer().exclude("class").include("dataSet", "key", "state", "id", "configuration", "configuration.*").exclude("*").serialize(recommender);

        return new TemplateResult(200, MimeType, new ByteArrayInputStream(result.getBytes(charset)), charset);
    }

    @Override
    public TemplateResult transform(RecommenderBuilder recommender) {
        String result = getSerializer().exclude("class").include("configuration", "configuration.*").serialize(recommender);

        return new TemplateResult(200, MimeType, new ByteArrayInputStream(result.getBytes(charset)), charset);
    }

    @Override
    public TemplateResult transform(RecommendationResult recommend) {
        String result = getSerializer().include("items").exclude("*.class", "class", "dataset").serialize(recommend);

        return new TemplateResult(200, MimeType, new ByteArrayInputStream(result.getBytes(charset)), charset);
    }

    @Override
    public void close() throws IOException {

    }
}
