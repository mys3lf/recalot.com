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

package com.recalot.common.communication;

import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Default template result.
 *
 * @author Matthäus Schmedding (info@recalot.com)
 */
public class TemplateResult {

    private int status;
    private String contentType;
    private InputStream inputStream;
    private Charset charset;

    /**
     * Default constructor with status, contentType, inputStream and charset as parameters
     * @param status result status
     * @param contentType result content type
     * @param inputStream result stream
     * @param charset result charset
     */
    public TemplateResult(int status, String contentType, InputStream inputStream, Charset charset) {
        this.status = status;
        this.contentType = contentType;
        this.inputStream = inputStream;
        this.charset = charset;
    }

    /**
     * @see <a href="https://en.wikipedia.org/wiki/List_of_HTTP_status_codes">List_of_HTTP_status_codes</a>
     * @return result status @see <a href="https://en.wikipedia.org/wiki/List_of_HTTP_status_codes">List_of_HTTP_status_codes</a>
     */
    public int getStatus() {
        return status;
    }

    /**
     * @see <a href="https://tools.ietf.org/html/rfc6838">Media Type Specifications and Registration Procedures</a> and <a href="https://tools.ietf.org/html/rfc6839">Additional Media Type Structured Syntax Suffixes</a>
     * @return content type
     * */
    public String getContentType() {
        return contentType;
    }

    /**
     * result as stream
     * @return result as stream
     */
    public InputStream getResult() {
        return inputStream;
    }

    /**
     * charset @see <a href="https://en.wikipedia.org/wiki/Character_encoding">Character Encoding</a>
     * @return charset
     */
    public Charset getCharset() {
        return charset;
    }
}
