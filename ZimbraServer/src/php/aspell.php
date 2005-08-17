<?php

require_once("Liquid/ServerResponse.php");

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
