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
package com.aperigeek.dropvault.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author Vivien Barousse
 */
public class HashService {

    public String hash(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(password.getBytes());
            byte[] hash = digest.digest();
            return toHexString(hash);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("SHA-1 unsupported", ex);
        }
    }

    protected String toHexString(byte[] data) {
        StringBuilder builder = new StringBuilder(data.length * 2);
        for (byte b : data) {
            int val = (b >= 0 ? b : b + 256);
            if (val < 0x10) {
                builder.append('0');
            }
            builder.append(Integer.toHexString(val));
        }
        return builder.toString();
    }
}
