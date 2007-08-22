<?php
#
# ***** BEGIN LICENSE BLOCK *****
# Version: MPL 1.1
#
# The contents of this file are subject to the Mozilla Public License
# Version 1.1 ("License"); you may not use this file except in
# compliance with the License. You may obtain a copy of the License at
# http://www.zimbra.com/license
#
# Software distributed under the License is distributed on an "AS IS"
# basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
# the License for the specific language governing rights and limitations
# under the License.
#
# The Original Code is: Zimbra Collaboration Suite Server.
#
# The Initial Developer of the Original Code is Zimbra, Inc.
# Portions created by Zimbra are Copyright (C) 2005, 2006 Zimbra, Inc.
# All Rights Reserved.
#
# Contributor(s):
#
# ***** END LICENSE BLOCK *****
#

require_once("Zimbra/ServerResponse.php");

$filename = "";
$text = "";
$locale = "en_EN";

if (isset($_FILES["text"])) {
    $text = file_get_contents($_FILES["text"]);
} else if (isset($_REQUEST["text"])){
    $text = $_REQUEST["text"];
}

if (get_magic_quotes_gpc()) {
    $text = stripslashes($text);
}

if ($text != NULL) {
    setlocale(LC_ALL, $locale);

    // Get rid of double-dashes, since we ignore dashes
    // when splitting words
    $text = preg_replace('/--+/', ' ', $text);

    // Split on anything that's not a word character, quote or dash
    $words = preg_split('/[^\w\'-]+/', $text);

    // Load dictionary
    $dictionary = pspell_new($locale);
    if ($dictionary == 0) {
        $msg = "Unable to open Aspell dictionary for locale " . $locale;
        error_log($msg);
        $response = new ServerResponse();
        $response->addParameter("error", $msg);
        $response->writeContent();
        return;
    }

    $skip = FALSE;
    $checked_words = array();
    $misspelled = "";

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
        if (preg_match('/^[0-9\-]+$/', $word)) {
            continue;
        }
        
        // Skip duplicates
        if (array_key_exists($word, $checked_words)) {
            continue;
        } else {
            $checked_words[$word] = 1;
        }

        // Check spelling
        if (!pspell_check($dictionary, $word)) {
            $suggestions = implode(",", pspell_suggest($dictionary, $word));
            $suggestions = utf8_encode($suggestions);
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
