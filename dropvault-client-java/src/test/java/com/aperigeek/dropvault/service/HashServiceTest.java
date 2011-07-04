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

import com.aperigeek.dropvault.service.HashService;
import junit.framework.TestCase;

/**
 *
 * @author Vivien Barousse
 */
public class HashServiceTest extends TestCase {
    
    private HashService hashService;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        hashService = new HashService();
    }
    
    public void testHash() {
        assertEquals("e27f2ba7f1e3d498919293d520a1a351a11a2cc1", hashService.hash("viv"));
        assertEquals("0b9c2625dc21ef05f6ad4ddf47c5f203837aa32c", hashService.hash("toto"));
        assertEquals("90795a0ffaa8b88c0e250546d8439bc9c31e5a5e", hashService.hash("tata"));
        assertEquals("f7e79ca8eb0b31ee4d5d6c181416667ffee528ed", hashService.hash("titi"));
    }
    
}
