/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2007 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */

Com_Zimbra_YMEmoticons.REGEXP = /(>:D<|#:-S|O:-\)|<:-P|:-SS|<\):\)|:-\?\?|3:-O|:\(\|\)|@};-|\*\*==|\(~~\)|\*-:\)|\[-O<|:\)>-|\\:D\x2f|\^:\)\^|;;\)|:-\x2f|:\x22>|:-\*|=\(\(|:-O|B-\)|:-S|>:\)|:\(\(|:\)\)|\x2f:\)|=\)\)|:-B|:-c|:\)\]|~X\(|:-h|:-t|8->|I-\)|8-\||L-\)|:-&|:-\$|\[-\(|:O\)|8-}|\(:\||=P~|:-\?|#-o|=D>|@-\)|:\^o|:-w|:-<|>:P|:o3|%-\(|:@\)|~:>|%%-|~O\)|8-X|=:\)|>-\)|:-L|\$-\)|:-\x22|b-\(|\[-X|>:\x2f|;\)\)|:-@|:-j|\(\*\)|o->|o=>|o-\+|\(%\)|:\)|:\(|;\)|:D|:x|:P|X\(|:>|:\||=;)/ig;

Com_Zimbra_YMEmoticons.SMILEYS = {
  
  ":)" : {
    "width" : 18,
    "alt" : "happy",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/1.gif",
    "text" : ":)",
    "regexp" : ":\\)",
    "height" : 18
  },
  ":(" : {
    "width" : 18,
    "alt" : "sad",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/2.gif",
    "text" : ":(",
    "regexp" : ":\\(",
    "height" : 18
  },
  "(~~)" : {
    "width" : 17,
    "alt" : "pumpkin",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/56.gif",
    "text" : "(~~)",
    "regexp" : "\\(~~\\)",
    "height" : 18
  },
  "~o)" : {
    "width" : 18,
    "alt" : "coffee",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/57.gif",
    "text" : "~O)",
    "regexp" : "~O\\)",
    "height" : 18
  },
  ":\">" : {
    "width" : 18,
    "alt" : "blushing",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/9.gif",
    "text" : ":\">",
    "regexp" : ":\\x22>",
    "height" : 18
  },
  "[-(" : {
    "width" : 18,
    "alt" : "not talking",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/33.gif",
    "text" : "[-(",
    "regexp" : "\\[-\\(",
    "height" : 18
  },
  ">:d<" : {
    "width" : 42,
    "alt" : "big hug",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/6.gif",
    "text" : ">:D<",
    "regexp" : ">:D<",
    "height" : 18
  },
  "#-o" : {
    "width" : 24,
    "alt" : "d'oh",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/40.gif",
    "text" : "#-o",
    "regexp" : "#-o",
    "height" : 18
  },
  "[-x" : {
    "width" : 22,
    "alt" : "shame on you",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/68.gif",
    "text" : "[-X",
    "regexp" : "\\[-X",
    "height" : 18
  },
  ":-t" : {
    "width" : 30,
    "alt" : "time out",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/104.gif",
    "text" : ":-t",
    "regexp" : ":-t",
    "height" : 18
  },
  ":(|)" : {
    "width" : 21,
    "alt" : "monkey",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/51.gif",
    "text" : ":(|)",
    "regexp" : ":\\(\\|\\)",
    "height" : 18
  },
  ":o)" : {
    "width" : 28,
    "alt" : "clown",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/34.gif",
    "text" : ":O)",
    "regexp" : ":O\\)",
    "height" : 18
  },
  "i-)" : {
    "width" : 21,
    "alt" : "sleepy",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/28.gif",
    "text" : "I-)",
    "regexp" : "I-\\)",
    "height" : 18
  },
  ";;)" : {
    "width" : 18,
    "alt" : "batting eyelashes",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/5.gif",
    "text" : ";;)",
    "regexp" : ";;\\)",
    "height" : 18
  },
  ":^o" : {
    "width" : 18,
    "alt" : "liar",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/44.gif",
    "text" : ":^o",
    "regexp" : ":\\^o",
    "height" : 18
  },
  "<:-p" : {
    "width" : 38,
    "alt" : "party",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/36.gif",
    "text" : "<:-P",
    "regexp" : "<:-P",
    "height" : 18
  },
  "x(" : {
    "width" : 34,
    "alt" : "angry",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/14.gif",
    "text" : "X(",
    "regexp" : "X\\(",
    "height" : 18
  },
  ":-/" : {
    "width" : 20,
    "alt" : "confused",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/7.gif",
    "text" : ":-/",
    "regexp" : ":-\\x2f",
    "height" : 18
  },
  "#:-s" : {
    "width" : 34,
    "alt" : "whew!",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/18.gif",
    "text" : "#:-S",
    "regexp" : "#:-S",
    "height" : 18
  },
  "8->" : {
    "width" : 23,
    "alt" : "daydreaming",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/105.gif",
    "text" : "8->",
    "regexp" : "8->",
    "height" : 18
  },
  ":d" : {
    "width" : 18,
    "alt" : "big grin",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/4.gif",
    "text" : ":D",
    "regexp" : ":D",
    "height" : 18
  },
  "\\:d/" : {
    "width" : 26,
    "alt" : "dancing",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/69.gif",
    "text" : "\\:D/",
    "regexp" : "\\\\:D\\x2f",
    "height" : 18
  },
  ":-b" : {
    "width" : 24,
    "alt" : "nerd",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/26.gif",
    "text" : ":-B",
    "regexp" : ":-B",
    "height" : 18
  },
  ":-@" : {
    "width" : 36,
    "alt" : "chatterbox",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/76.gif",
    "text" : ":-@",
    "regexp" : ":-@",
    "height" : 18
  },
  ":-h" : {
    "width" : 28,
    "alt" : "wave",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/103.gif",
    "text" : ":-h",
    "regexp" : ":-h",
    "height" : 18
  },
  ":-c" : {
    "width" : 28,
    "alt" : "call me",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/101.gif",
    "text" : ":-c",
    "regexp" : ":-c",
    "height" : 18
  },
  "=p~" : {
    "width" : 18,
    "alt" : "drooling",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/38.gif",
    "text" : "=P~",
    "regexp" : "=P~",
    "height" : 18
  },
  "(:|" : {
    "width" : 18,
    "alt" : "yawn",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/37.gif",
    "text" : "(:|",
    "regexp" : "\\(:\\|",
    "height" : 18
  },
  ":-o" : {
    "width" : 18,
    "alt" : "surprise",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/13.gif",
    "text" : ":-O",
    "regexp" : ":-O",
    "height" : 18
  },
  "o->" : {
    "width" : 18,
    "alt" : "hiro",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/72.gif",
    "text" : "o->",
    "regexp" : "o->",
    "height" : 18
  },
  ":))" : {
    "width" : 18,
    "alt" : "laughing",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/21.gif",
    "text" : ":))",
    "regexp" : ":\\)\\)",
    "height" : 18
  },
  "/:)" : {
    "width" : 18,
    "alt" : "raised eyebrow",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/23.gif",
    "text" : "/:)",
    "regexp" : "\\x2f:\\)",
    "height" : 18
  },
  "*-:)" : {
    "width" : 30,
    "alt" : "idea",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/58.gif",
    "text" : "*-:)",
    "regexp" : "\\*-:\\)",
    "height" : 18
  },
  ":)]" : {
    "width" : 31,
    "alt" : "on the phone",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/100.gif",
    "text" : ":)]",
    "regexp" : ":\\)\\]",
    "height" : 18
  },
  ":-ss" : {
    "width" : 36,
    "alt" : "nailbiting",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/42.gif",
    "text" : ":-SS",
    "regexp" : ":-SS",
    "height" : 18
  },
  "(%)" : {
    "width" : 18,
    "alt" : "yin yang",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/75.gif",
    "text" : "(%)",
    "regexp" : "\\(%\\)",
    "height" : 18
  },
  ":-*" : {
    "width" : 18,
    "alt" : "kiss",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/11.gif",
    "text" : ":-*",
    "regexp" : ":-\\*",
    "height" : 18
  },
  "~x(" : {
    "width" : 44,
    "alt" : "at wits' end",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/102.gif",
    "text" : "~X(",
    "regexp" : "~X\\(",
    "height" : 18
  },
  "o=>" : {
    "width" : 18,
    "alt" : "billy",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/73.gif",
    "text" : "o=>",
    "regexp" : "o=>",
    "height" : 18
  },
  ":-??" : {
    "width" : 40,
    "alt" : "I don't know",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/106.gif",
    "text" : ":-??",
    "regexp" : ":-\\?\\?",
    "height" : 18
  },
  "@-)" : {
    "width" : 18,
    "alt" : "hypnotized",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/43.gif",
    "text" : "@-)",
    "regexp" : "@-\\)",
    "height" : 18
  },
  "3:-o" : {
    "width" : 18,
    "alt" : "cow",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/50.gif",
    "text" : "3:-O",
    "regexp" : "3:-O",
    "height" : 18
  },
  "=d>" : {
    "width" : 18,
    "alt" : "applause",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/41.gif",
    "text" : "=D>",
    "regexp" : "=D>",
    "height" : 18
  },
  ":-w" : {
    "width" : 23,
    "alt" : "waiting",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/45.gif",
    "text" : ":-w",
    "regexp" : ":-w",
    "height" : 18
  },
  ":x" : {
    "width" : 18,
    "alt" : "love struck",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/8.gif",
    "text" : ":x",
    "regexp" : ":x",
    "height" : 18
  },
  ":-$" : {
    "width" : 18,
    "alt" : "don't tell anyone",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/32.gif",
    "text" : ":-$",
    "regexp" : ":-\\$",
    "height" : 18
  },
  "~:>" : {
    "width" : 18,
    "alt" : "chicken",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/52.gif",
    "text" : "~:>",
    "regexp" : "~:>",
    "height" : 18
  },
  "=:)" : {
    "width" : 20,
    "alt" : "bug",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/60.gif",
    "text" : "=:)",
    "regexp" : "=:\\)",
    "height" : 18
  },
  "(*)" : {
    "width" : 18,
    "alt" : "star",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/79.gif",
    "text" : "(*)",
    "regexp" : "\\(\\*\\)",
    "height" : 18
  },
  ":|" : {
    "width" : 18,
    "alt" : "straight face",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/22.gif",
    "text" : ":|",
    "regexp" : ":\\|",
    "height" : 18
  },
  ":((" : {
    "width" : 22,
    "alt" : "crying",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/20.gif",
    "text" : ":((",
    "regexp" : ":\\(\\(",
    "height" : 18
  },
  "8-x" : {
    "width" : 18,
    "alt" : "skull",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/59.gif",
    "text" : "8-X",
    "regexp" : "8-X",
    "height" : 18
  },
  "o:-)" : {
    "width" : 30,
    "alt" : "angel",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/25.gif",
    "text" : "O:-)",
    "regexp" : "O:-\\)",
    "height" : 18
  },
  ">:p" : {
    "width" : 18,
    "alt" : "phbbbbt",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/47.gif",
    "text" : ">:P",
    "regexp" : ">:P",
    "height" : 18
  },
  ">-)" : {
    "width" : 18,
    "alt" : "alien",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/61.gif",
    "text" : ">-)",
    "regexp" : ">-\\)",
    "height" : 18
  },
  "=((" : {
    "width" : 18,
    "alt" : "broken heart",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/12.gif",
    "text" : "=((",
    "regexp" : "=\\(\\(",
    "height" : 18
  },
  "l-)" : {
    "width" : 24,
    "alt" : "loser",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/30.gif",
    "text" : "L-)",
    "regexp" : "L-\\)",
    "height" : 18
  },
  ":@)" : {
    "width" : 18,
    "alt" : "pig",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/49.gif",
    "text" : ":@)",
    "regexp" : ":@\\)",
    "height" : 18
  },
  ">:/" : {
    "width" : 23,
    "alt" : "bring it on",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/70.gif",
    "text" : ">:/",
    "regexp" : ">:\\x2f",
    "height" : 18
  },
  "b-(" : {
    "width" : 18,
    "alt" : "feeling beat up",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/66.gif",
    "text" : "b-(",
    "regexp" : "b-\\(",
    "height" : 18
  },
  "$-)" : {
    "width" : 18,
    "alt" : "money eyes",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/64.gif",
    "text" : "$-)",
    "regexp" : "\\$-\\)",
    "height" : 18
  },
  ":-?" : {
    "width" : 18,
    "alt" : "thinking",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/39.gif",
    "text" : ":-?",
    "regexp" : ":-\\?",
    "height" : 18
  },
  ":)>-" : {
    "width" : 22,
    "alt" : "peace sign",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/67.gif",
    "text" : ":)>-",
    "regexp" : ":\\)>-",
    "height" : 18
  },
  ":-j" : {
    "width" : 26,
    "alt" : "oh go on",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/78.gif",
    "text" : ":-j",
    "regexp" : ":-j",
    "height" : 18
  },
  "%%-" : {
    "width" : 18,
    "alt" : "good luck",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/54.gif",
    "text" : "%%-",
    "regexp" : "%%-",
    "height" : 18
  },
  "%-(" : {
    "width" : 52,
    "alt" : "not listening",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/107.gif",
    "text" : "%-(",
    "regexp" : "%-\\(",
    "height" : 18
  },
  ":p" : {
    "width" : 18,
    "alt" : "tongue",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/10.gif",
    "text" : ":P",
    "regexp" : ":P",
    "height" : 18
  },
  "^:)^" : {
    "width" : 32,
    "alt" : "not worthy",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/77.gif",
    "text" : "^:)^",
    "regexp" : "\\^:\\)\\^",
    "height" : 18
  },
  ":-\"" : {
    "width" : 22,
    "alt" : "whistling",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/65.gif",
    "text" : ":-\"",
    "regexp" : ":-\\x22",
    "height" : 18
  },
  ":-<" : {
    "width" : 24,
    "alt" : "sigh",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/46.gif",
    "text" : ":-<",
    "regexp" : ":-<",
    "height" : 18
  },
  ":o3" : {
    "width" : 31,
    "alt" : "puppy dog eyes",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/108.gif",
    "text" : ":o3",
    "regexp" : ":o3",
    "height" : 18
  },
  ">:)" : {
    "width" : 18,
    "alt" : "devil",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/19.gif",
    "text" : ">:)",
    "regexp" : ">:\\)",
    "height" : 18
  },
  "=;" : {
    "width" : 18,
    "alt" : "talk to the hand",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/27.gif",
    "text" : "=;",
    "regexp" : "=;",
    "height" : 18
  },
  "8-|" : {
    "width" : 18,
    "alt" : "rolling eyes",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/29.gif",
    "text" : "8-|",
    "regexp" : "8-\\|",
    "height" : 18
  },
  "**==" : {
    "width" : 25,
    "alt" : "flag",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/55.gif",
    "text" : "**==",
    "regexp" : "\\*\\*==",
    "height" : 18
  },
  "o-+" : {
    "width" : 18,
    "alt" : "april",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/74.gif",
    "text" : "o-+",
    "regexp" : "o-+",
    "height" : 18
  },
  "8-}" : {
    "width" : 24,
    "alt" : "silly",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/35.gif",
    "text" : "8-}",
    "regexp" : "8-}",
    "height" : 18
  },
  "=))" : {
    "width" : 30,
    "alt" : "rolling on the floor",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/24.gif",
    "text" : "=))",
    "regexp" : "=\\)\\)",
    "height" : 18
  },
  ":-l" : {
    "width" : 18,
    "alt" : "frustrated",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/62.gif",
    "text" : ":-L",
    "regexp" : ":-L",
    "height" : 18
  },
  "b-)" : {
    "width" : 18,
    "alt" : "cool",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/16.gif",
    "text" : "B-)",
    "regexp" : "B-\\)",
    "height" : 18
  },
  ";)" : {
    "width" : 18,
    "alt" : "winking",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/3.gif",
    "text" : ";)",
    "regexp" : ";\\)",
    "height" : 18
  },
  ":>" : {
    "width" : 18,
    "alt" : "smug",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/15.gif",
    "text" : ":>",
    "regexp" : ":>",
    "height" : 18
  },
  ":-&" : {
    "width" : 18,
    "alt" : "sick",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/31.gif",
    "text" : ":-&",
    "regexp" : ":-&",
    "height" : 18
  },
  "<):)" : {
    "width" : 18,
    "alt" : "cowboy",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/48.gif",
    "text" : "<):)",
    "regexp" : "<\\):\\)",
    "height" : 18
  },
  ":-s" : {
    "width" : 18,
    "alt" : "worried",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/17.gif",
    "text" : ":-S",
    "regexp" : ":-S",
    "height" : 18
  },
  ";))" : {
    "width" : 18,
    "alt" : "hee hee",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/71.gif",
    "text" : ";))",
    "regexp" : ";\\)\\)",
    "height" : 18
  },
  "[-o<" : {
    "width" : 18,
    "alt" : "praying",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/63.gif",
    "text" : "[-O<",
    "regexp" : "\\[-O<",
    "height" : 18
  },
  "@};-" : {
    "width" : 18,
    "alt" : "rose",
    "src" : "/service/zimlet/com_zimbra_ymemoticons/img/53.gif",
    "text" : "@};-",
    "regexp" : "@};-",
    "height" : 18
  }
};
