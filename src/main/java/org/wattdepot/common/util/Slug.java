/**
 * Slug.java This file is part of WattDepot.
 *
 * Copyright (C) 2013  Cam Moore
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.wattdepot.common.util;

/**
 * Slug - Utility class to slugify strings.
 * 
 * @author Cam Moore
 * 
 */
public class Slug {

  /**
   * Converts to lowercase, removes non-word characters (alphanumerics and
   * underscores) and converts spaces to hyphens. Also strips leading and
   * trailing whitespace.
   * 
   * @param str
   *          The string to slugify.
   * @return The slugified String.
   */
  public static String slugify(String str) {
    String ret = null;
    ret = str.toLowerCase();
    ret = ret.replace(" ", "-");
    ret = ret.replace("!", "");
    ret = ret.replace("@", "");
    ret = ret.replace("#", "");
    ret = ret.replace("$", "");
    ret = ret.replace("%", "");
    ret = ret.replace("^", "");
    ret = ret.replace("&", "");
    ret = ret.replace("*", "");
    ret = ret.replace("(", "");
    ret = ret.replace(")", "");
    ret = ret.replace("=", "");
    ret = ret.replace("+", "");
    ret = ret.replace("`", "");
    ret = ret.replace("~", "");
    ret = ret.replace(",", "");
    ret = ret.replace("<", "");
    ret = ret.replace(".", "");
    ret = ret.replace(">", "");
    ret = ret.replace("/", "");
    ret = ret.replace("?", "");
    ret = ret.replace(";", "");
    ret = ret.replace(":", "");
    ret = ret.replace("[", "");
    ret = ret.replace("{", "");
    ret = ret.replace("]", "");
    ret = ret.replace("}", "");
    ret = ret.replace("\\", "");
    ret = ret.replace("|", "");
    return ret;
  }

  /**
   * Checks to see if a slug is valid.
   * 
   * @param slug
   *          the slug to check.
   * @return True if it is valid, false otherwise.
   */
  public static boolean validateSlug(String slug) {
    if (slug != null) {
      return slug.equals(slugify(slug));
    }
    return false;
  }
}
