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
package com.aperigeek.dropvault.desktop.ui.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

/**
 *
 * @author Vivien Barousse
 */
public class CenteredLayout implements LayoutManager {

    @Override
    public void addLayoutComponent(String name, Component comp) {
    }

    @Override
    public void removeLayoutComponent(Component comp) {
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        int width = 0;
        int height = 0;
        for (Component c : parent.getComponents()) {
            if (c.getPreferredSize().width > width) {
                width = c.getPreferredSize().width;
            }
            if (c.getPreferredSize().height > height) {
                height = c.getPreferredSize().height;
            }
        }
        return new Dimension(width, height);
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        int width = 0;
        int height = 0;
        for (Component c : parent.getComponents()) {
            if (c.getMinimumSize().width > width) {
                width = c.getMinimumSize().width;
            }
            if (c.getMinimumSize().height > height) {
                height = c.getMinimumSize().height;
            }
        }
        return new Dimension(width, height);
    }

    @Override
    public void layoutContainer(Container parent) {
        for (Component c : parent.getComponents()) {
            c.setSize(c.getPreferredSize());
            c.setLocation(
                    (parent.getWidth() - c.getWidth()) / 2,
                    (parent.getHeight() - c.getHeight()) / 2);
        }
    }
    
}
