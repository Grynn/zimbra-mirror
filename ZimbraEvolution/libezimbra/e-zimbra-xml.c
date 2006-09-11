#include "e-zimbra-xml.h"
#include <string.h>


char*
e_zimbra_xml_find_attribute
	(
	xmlNode		* node,
	const char	* key
	)
{
	xmlAttr *	attr	=	NULL;
	xmlChar	*	val		=	NULL;
	char	*	ret		=	NULL;

	for ( attr = node->properties; attr; attr = attr->next )
	{
		if ( strcmp( ( const char* ) attr->name, key ) == 0 )
		{
			val = attr->children->content;
			break;
		}
	}

	if ( val )
	{
		ret = ( char* ) g_strdup( ( const char* ) val );
	}

	return ret;
}


gboolean
e_zimbra_xml_check_attribute_exists
	(
	xmlNode		*	node,
	const char	*	key
	)
{
	xmlAttr *	attr	=	NULL;
	gboolean	ret		=	FALSE;

	for ( attr = node->properties; attr; attr = attr->next )
	{
		if ( strcmp( ( const char* ) attr->name, key ) == 0 )
		{
			ret = TRUE;
			break;
		}
	}

	return ret;
}


int
e_zimbra_xml_check_attribute
	(
	xmlNode		*	node,
	const char	*	key,
	const char	*	val
	)
{
	xmlAttr *	attr	=	NULL;
	int			ret		=	0;

	for ( attr = node->properties; attr; attr = attr->next )
	{
		if ( ( strcmp( ( const char* ) attr->name, key ) == 0 ) && ( strcmp( attr->children->content, val ) == 0 ) )
		{
			ret = 1;
			break;
		}
	}

	return ret;
}


xmlNode*
e_zimbra_xml_find_child_by_name
	(
	xmlNode		*	node,
	const char	*	key
	)
{
	xmlNode * child	= NULL;

	for ( child = node->children; child; child = child->next )
	{
		if ( strcmp( ( const char* ) child->name, key ) == 0 )
		{
			return child;
		}
	}

	return NULL;
}


char*
e_zimbra_xml_find_child_value
	(
	xmlNode		* node,
	const char	* key
	)
{
	xmlNode *	child	=	NULL;
	xmlAttr	*	attr	=	NULL;
	xmlChar	*	val		=	NULL;
	char	*	ret 	=	NULL;

	for ( child = node->children; child; child = child->next )
	{
		if ( strcmp( ( const char* ) child->name, "a" ) == 0 )
		{
			for ( attr = child->properties; attr; attr = attr->next )
			{
				if ( strcmp( ( const char* ) attr->name, "n" ) == 0 )
				{
					if ( strcmp( ( const char* ) attr->children->content, key ) == 0 )
					{
						if ( child->children )
						{
							val = child->children->content;
						}
						else
						{
							val = NULL;
						}

						goto exit;
					}
				}
			}
		}
	}

exit:

	if ( val )
	{
		ret = ( char* ) g_strdup( ( const char* ) val );
	}

	return ret;
}
