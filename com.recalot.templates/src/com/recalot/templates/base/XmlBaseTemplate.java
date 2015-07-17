package com.recalot.templates.base;

import com.recalot.common.communication.Message;
import com.recalot.common.communication.TemplateResult;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.interfaces.template.BaseTemplate;

import java.io.IOException;

/**
 * @author matthaeus.schmedding
 */
public class XmlBaseTemplate implements BaseTemplate {

    @Override
    public String getKey() {
        return "xml";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public TemplateResult transform(BaseException ex) {
        return null;
    }

    @Override
    public TemplateResult transform(Message message) {
        return null;
    }

    @Override
    public void close() throws IOException {

    }
}
