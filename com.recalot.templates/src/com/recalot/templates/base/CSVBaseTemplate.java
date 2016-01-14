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

package com.recalot.templates.base;

import com.recalot.common.communication.Message;
import com.recalot.common.communication.TemplateResult;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.interfaces.template.BaseTemplate;

import java.io.IOException;

/**
 * @author matthaeus.schmedding
 */
public class CSVBaseTemplate implements BaseTemplate {

    @Override
    public TemplateResult transform(BaseException result) {
        return null;
    }

    @Override
    public String getKey() {
        return "csv";
    }

    @Override
    public String getDescription() {
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
