<?php
# 
# ***** BEGIN LICENSE BLOCK *****
# Version: ZPL 1.1
# 
# The contents of this file are subject to the Zimbra Public License
# Version 1.1 ("License"); you may not use this file except in
# compliance with the License. You may obtain a copy of the License at
# http://www.zimbra.com/license
# 
# Software distributed under the License is distributed on an "AS IS"
# basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
# the License for the specific language governing rights and limitations
# under the License.
# 
# The Original Code is: Zimbra Collaboration Suite.
# 
# The Initial Developer of the Original Code is Zimbra, Inc.
# Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
# All Rights Reserved.
# 
# Contributor(s):
# 
# ***** END LICENSE BLOCK *****
# 

require_once("Zimbra/ServerResponse.php");

$filename = $_FILES["text"];
if ($filename != NULL) {
    $text = file_get_contents($filename);
} else {
    $text = $_REQUEST["text"];
}
    
    
if ($text != NULL) {
    // Get rid of double-dashes, since we ignore dashes
    // when splitting words
    $text = preg_replace('/--+/', ' ', $text);
        
    // Split on anything that's not a word character, quote or dash
    $words = preg_split('/[^\w-\']+/', $text);
        
    $dictionary = pspell_new("en_US");
    $skip = FALSE;
        
    foreach ($words as $word) {
        if ($skip) {
            $skip = FALSE;
            continue;
        }
            
        // Ignore hyphenations
        if (preg_match('/-$/', $word)) {
            // Skip the next word too
            $skip = TRUE;
            continue;
        }
            
        // Skip numbers
        if (!preg_match('/[A-z]/', $word)) {
            continue;
        }
            
        // Check spelling
        if (!pspell_check($dictionary, $word)) {
            $suggestions = implode(",", pspell_suggest($dictionary, $word));
            $misspelled .= "$word:$suggestions\n";
        }
    }

    $response = new ServerResponse();
    $response->addParameter("misspelled", $misspelled);
    $response->writeContent();
} else {
?>

<html>
 <head>
  <title>Spell Checker</title>
 </head>
 <body>

<form action="aspell.php" method="post" enctype="multipart/form-data">
    <p>Type in some words to spell check:</p>
    <textarea NAME="text" ROWS="10" COLS="80"></textarea>
    <p><input type="submit" /></p>
</form>

</body>
</html>

<?php
    }
?>
