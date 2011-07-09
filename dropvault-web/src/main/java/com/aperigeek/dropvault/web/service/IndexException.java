/*  
 * This file is part of dropvault.
 *
 * dropvault is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * dropvault is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with dropvault.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aperigeek.dropvault.web.service;

/**
 *
 * @author Vivien Barousse
 */
public class IndexException extends Exception {

    public IndexException(Throwable cause) {
        super(cause);
    }

    public IndexException(String message, Throwable cause) {
        super(message, cause);
    }

    public IndexException(String message) {
        super(message);
    }

    public IndexException() {
    }
    
}
