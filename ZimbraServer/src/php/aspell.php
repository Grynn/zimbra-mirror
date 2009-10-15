<?php
#
# ***** BEGIN LICENSE BLOCK *****
# Zimbra Collaboration Suite Server
# Copyright (C) 2005, 2006, 2007, 2008, 2009 Zimbra, Inc.
# 
# The contents of this file are subject to the Yahoo! Public License
# Version 1.0 ("License"); you may not use this file except in
# compliance with the License.  You may obtain a copy of the License at
# http://www.zimbra.com/license.
# 
# Software distributed under the License is distributed on an "AS IS"
# basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
# ***** END LICENSE BLOCK *****
#

$filename = "";
$text = "";
$dictionary = "en_EN";

if (isset($_FILES["text"])) {
    $text = file_get_contents($_FILES["text"]);
} else if (isset($_REQUEST["text"])){
    $text = $_REQUEST["text"];
}

if (isset($_REQUEST["dictionary"])) {
    $dictionary = $_REQUEST["dictionary"];
}
   
if (get_magic_quotes_gpc()) {
    $text = stripslashes($text);
}

if ($text != NULL) {
    header("Content-Type: text/plain; charset=UTF-8");

    setlocale(LC_ALL, $dictionary);

    // Convert to ISO-8859-1
    $text = iconv("UTF-8", "iso-8859-1//IGNORE", $text);

	// Set error handler after the call to iconv, in case iconv
	// complains about unexpected characters (bug 41760). 
    set_error_handler("returnError");

    // Get rid of double-dashes, since we ignore dashes
    // when splitting words
    $text = preg_replace('/--+/', ' ', $text);

    // Split on anything that's not a word character, quote or dash
    $words = preg_split('/[^\w\xc0-\xfd-\']+/', $text);
	
    // Load dictionary
    $dictionary = pspell_new($dictionary);
    if ($dictionary == 0) {
        returnError("Unable to open dictionary " . $dictionary);
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
        if (preg_match('/[0-9\-]+/', $word)) {
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
            $utfw = utf8_encode($word);
            $misspelled .= "$utfw:$suggestions\n";
        }
    }

    echo $misspelled;
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
    <p>Dictionary:<input type="text" name="dictionary" value="<?php print $dictionary; ?>" size="8"/></p>
    <p><input type="submit" /></p>
</form>

</body>
</html>

<?php
    }

function returnError($errno, $message) {
    header("HTTP/1.1 500 Internal Server Error");
    error_log("Error $errno: " . $message);
    exit($message);
}

?>
