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

package com.recalot.common.communication;


/**
 * Default message. This class is used for all kind of information transfer.
 *
 * @author Matthäus Schmedding (info@recalot.com)
 */
public class Message  {

    /**
     * Message body
     */
    private final String body;

    /**
     * Message statue
     */
    private final Status status;

    /**
     * Message title
     */
    private final String title;

    /**
     * Default constructor with title, body and status as parameters
     * @param title message title
     * @param body message body
     * @param status message statu
     */
    public Message(String title, String body, Status status){
        this.title  = title;
        this.body = body;
        this.status = status;
    }

    /**
     * Message Status Enum
     */
    public enum Status {
        DEBUG,
        INFO,
        WARN,
        ERROR,
        FATAL
    }

    /**
     * Get the message title
     * @return message title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get the message body
     * @return message body
     */
    public String getBody() {
        return body;
    }

    /**
     * Get the message status
     * @return message status
     */
    public Status getStatus() {
        return status;
    }
}
