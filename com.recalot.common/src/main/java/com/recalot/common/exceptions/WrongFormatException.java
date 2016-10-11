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

package com.recalot.common.exceptions;

/**
 * @author Matthäus Schmedding (info@recalot.com)
 */
public class WrongFormatException extends BaseException {

    public WrongFormatException() {
        super();
    }

    public WrongFormatException(String message) {
        super(message);
    }

    public WrongFormatException(String format, String arg) {
        super(format, arg);
    }

    public WrongFormatException(String format, String arg1, String arg2) {
        super(format, arg1, arg2);
    }

    public WrongFormatException(String format, String arg1, String arg2, String arg3) {
        super(format, arg1, arg2);
    }

    public WrongFormatException(String format, String arg1, String arg2, String arg3, String arg4) {
        super(format, arg1, arg2, arg3, arg4);
    }


    public WrongFormatException(Throwable cause) {
        super(cause);
    }
}
