package com.recalot.common.interfaces.template;

import com.recalot.common.communication.TemplateResult;
import com.recalot.common.interfaces.communication.RecommendationResult;

import java.io.InputStream;

/**
 * @author Matthaeus.schmedding
 */
public interface RecommendationTemplate {
    public TemplateResult transform(RecommendationResult result);
}
