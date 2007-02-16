/* 
 * Copyright (C) 2006-2007 Zimbra, Inc.
 * 
 * This program is free software; you can redistribute it and/or 
 * modify it under the terms of version 2 of the GNU Lesser General Public 
 * License as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 * 
 */

#ifndef E_ZIMBRA_XML_H
#define E_ZIMBRA_XML_H

#include <glib-object.h>
#include <libxml/parser.h>


char*
e_zimbra_xml_find_attribute
	(
	xmlNode		*	node,
	const char	*	key
	);


gboolean
e_zimbra_xml_check_attribute_exists
	(
	xmlNode		*	node,
	const char	*	key
	);


int
e_zimbra_xml_check_attribute
	(
	xmlNode		*	node,
	const char	*	key,
	const char	*	val
	);


xmlNode*
e_zimbra_xml_find_child_by_name
	(
	xmlNode		*	node,
	const char	*	key
	);


char*
e_zimbra_xml_find_child_value
	(
	xmlNode		*	node,
	const char	*	key
	);


#endif
