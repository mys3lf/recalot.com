package com.recalot.common.interfaces.controller;


import com.recalot.common.communication.TemplateResult;

import java.util.HashMap;

/**
 * @author Matthaeus.schmedding
 */
public interface RecommendationController {
    public TemplateResult recommend(String templateKey);
    public TemplateResult recommend(Object context, String templateKey);
    public TemplateResult recommend(HashMap<String, Object> configOrContext, String templateKey);
    public TemplateResult recommend(String user, String templateKey);
    public TemplateResult recommend(String user, Object context, String templateKey);
    public TemplateResult recommend(String user, HashMap<String, Object> configOrContext, String templateKey);
}
