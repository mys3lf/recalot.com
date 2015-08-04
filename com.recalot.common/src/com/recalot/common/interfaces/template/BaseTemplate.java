package com.recalot.common.interfaces.template;

import com.recalot.common.communication.Message;
import com.recalot.common.communication.Service;
import com.recalot.common.communication.TemplateResult;
import com.recalot.common.exceptions.BaseException;

/**
 *
 * @author Matthaeus.schmedding
 */
public interface BaseTemplate extends Service {
    public TemplateResult transform(Message message);
    public TemplateResult transform(BaseException ex);
}
