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
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/1.gif",
    "text" : ":)",
    "regexp" : ":\\)",
    "height" : 18
  },
  ":(" : {
    "width" : 18,
    "alt" : "sad",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/2.gif",
    "text" : ":(",
    "regexp" : ":\\(",
    "height" : 18
  },
  "(~~)" : {
    "width" : 17,
    "alt" : "pumpkin",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/56.gif",
    "text" : "(~~)",
    "regexp" : "\\(~~\\)",
    "height" : 18
  },
  "~o)" : {
    "width" : 18,
    "alt" : "coffee",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/57.gif",
    "text" : "~O)",
    "regexp" : "~O\\)",
    "height" : 18
  },
  ":\">" : {
    "width" : 18,
    "alt" : "blushing",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/9.gif",
    "text" : ":\">",
    "regexp" : ":\\x22>",
    "height" : 18
  },
  "[-(" : {
    "width" : 18,
    "alt" : "not talking",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/33.gif",
    "text" : "[-(",
    "regexp" : "\\[-\\(",
    "height" : 18
  },
  ">:d<" : {
    "width" : 42,
    "alt" : "big hug",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/6.gif",
    "text" : ">:D<",
    "regexp" : ">:D<",
    "height" : 18
  },
  "#-o" : {
    "width" : 24,
    "alt" : "d'oh",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/40.gif",
    "text" : "#-o",
    "regexp" : "#-o",
    "height" : 18
  },
  "[-x" : {
    "width" : 22,
    "alt" : "shame on you",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/68.gif",
    "text" : "[-X",
    "regexp" : "\\[-X",
    "height" : 18
  },
  ":-t" : {
    "width" : 30,
    "alt" : "time out",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/104.gif",
    "text" : ":-t",
    "regexp" : ":-t",
    "height" : 18
  },
  ":(|)" : {
    "width" : 21,
    "alt" : "monkey",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/51.gif",
    "text" : ":(|)",
    "regexp" : ":\\(\\|\\)",
    "height" : 18
  },
  ":o)" : {
    "width" : 28,
    "alt" : "clown",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/34.gif",
    "text" : ":O)",
    "regexp" : ":O\\)",
    "height" : 18
  },
  "i-)" : {
    "width" : 21,
    "alt" : "sleepy",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/28.gif",
    "text" : "I-)",
    "regexp" : "I-\\)",
    "height" : 18
  },
  ";;)" : {
    "width" : 18,
    "alt" : "batting eyelashes",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/5.gif",
    "text" : ";;)",
    "regexp" : ";;\\)",
    "height" : 18
  },
  ":^o" : {
    "width" : 18,
    "alt" : "liar",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/44.gif",
    "text" : ":^o",
    "regexp" : ":\\^o",
    "height" : 18
  },
  "<:-p" : {
    "width" : 38,
    "alt" : "party",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/36.gif",
    "text" : "<:-P",
    "regexp" : "<:-P",
    "height" : 18
  },
  "x(" : {
    "width" : 34,
    "alt" : "angry",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/14.gif",
    "text" : "X(",
    "regexp" : "X\\(",
    "height" : 18
  },
  ":-/" : {
    "width" : 20,
    "alt" : "confused",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/7.gif",
    "text" : ":-/",
    "regexp" : ":-\\x2f",
    "height" : 18
  },
  "#:-s" : {
    "width" : 34,
    "alt" : "whew!",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/18.gif",
    "text" : "#:-S",
    "regexp" : "#:-S",
    "height" : 18
  },
  "8->" : {
    "width" : 23,
    "alt" : "daydreaming",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/105.gif",
    "text" : "8->",
    "regexp" : "8->",
    "height" : 18
  },
  ":d" : {
    "width" : 18,
    "alt" : "big grin",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/4.gif",
    "text" : ":D",
    "regexp" : ":D",
    "height" : 18
  },
  "\\:d/" : {
    "width" : 26,
    "alt" : "dancing",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/69.gif",
    "text" : "\\:D/",
    "regexp" : "\\\\:D\\x2f",
    "height" : 18
  },
  ":-b" : {
    "width" : 24,
    "alt" : "nerd",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/26.gif",
    "text" : ":-B",
    "regexp" : ":-B",
    "height" : 18
  },
  ":-@" : {
    "width" : 36,
    "alt" : "chatterbox",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/76.gif",
    "text" : ":-@",
    "regexp" : ":-@",
    "height" : 18
  },
  ":-h" : {
    "width" : 28,
    "alt" : "wave",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/103.gif",
    "text" : ":-h",
    "regexp" : ":-h",
    "height" : 18
  },
  ":-c" : {
    "width" : 28,
    "alt" : "call me",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/101.gif",
    "text" : ":-c",
    "regexp" : ":-c",
    "height" : 18
  },
  "=p~" : {
    "width" : 18,
    "alt" : "drooling",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/38.gif",
    "text" : "=P~",
    "regexp" : "=P~",
    "height" : 18
  },
  "(:|" : {
    "width" : 18,
    "alt" : "yawn",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/37.gif",
    "text" : "(:|",
    "regexp" : "\\(:\\|",
    "height" : 18
  },
  ":-o" : {
    "width" : 18,
    "alt" : "surprise",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/13.gif",
    "text" : ":-O",
    "regexp" : ":-O",
    "height" : 18
  },
  "o->" : {
    "width" : 18,
    "alt" : "hiro",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/72.gif",
    "text" : "o->",
    "regexp" : "o->",
    "height" : 18
  },
  ":))" : {
    "width" : 18,
    "alt" : "laughing",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/21.gif",
    "text" : ":))",
    "regexp" : ":\\)\\)",
    "height" : 18
  },
  "/:)" : {
    "width" : 18,
    "alt" : "raised eyebrow",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/23.gif",
    "text" : "/:)",
    "regexp" : "\\x2f:\\)",
    "height" : 18
  },
  "*-:)" : {
    "width" : 30,
    "alt" : "idea",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/58.gif",
    "text" : "*-:)",
    "regexp" : "\\*-:\\)",
    "height" : 18
  },
  ":)]" : {
    "width" : 31,
    "alt" : "on the phone",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/100.gif",
    "text" : ":)]",
    "regexp" : ":\\)\\]",
    "height" : 18
  },
  ":-ss" : {
    "width" : 36,
    "alt" : "nailbiting",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/42.gif",
    "text" : ":-SS",
    "regexp" : ":-SS",
    "height" : 18
  },
  "(%)" : {
    "width" : 18,
    "alt" : "yin yang",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/75.gif",
    "text" : "(%)",
    "regexp" : "\\(%\\)",
    "height" : 18
  },
  ":-*" : {
    "width" : 18,
    "alt" : "kiss",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/11.gif",
    "text" : ":-*",
    "regexp" : ":-\\*",
    "height" : 18
  },
  "~x(" : {
    "width" : 44,
    "alt" : "at wits' end",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/102.gif",
    "text" : "~X(",
    "regexp" : "~X\\(",
    "height" : 18
  },
  "o=>" : {
    "width" : 18,
    "alt" : "billy",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/73.gif",
    "text" : "o=>",
    "regexp" : "o=>",
    "height" : 18
  },
  ":-??" : {
    "width" : 40,
    "alt" : "I don't know",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/106.gif",
    "text" : ":-??",
    "regexp" : ":-\\?\\?",
    "height" : 18
  },
  "@-)" : {
    "width" : 18,
    "alt" : "hypnotized",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/43.gif",
    "text" : "@-)",
    "regexp" : "@-\\)",
    "height" : 18
  },
  "3:-o" : {
    "width" : 18,
    "alt" : "cow",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/50.gif",
    "text" : "3:-O",
    "regexp" : "3:-O",
    "height" : 18
  },
  "=d>" : {
    "width" : 18,
    "alt" : "applause",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/41.gif",
    "text" : "=D>",
    "regexp" : "=D>",
    "height" : 18
  },
  ":-w" : {
    "width" : 23,
    "alt" : "waiting",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/45.gif",
    "text" : ":-w",
    "regexp" : ":-w",
    "height" : 18
  },
  ":x" : {
    "width" : 18,
    "alt" : "love struck",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/8.gif",
    "text" : ":x",
    "regexp" : ":x",
    "height" : 18
  },
  ":-$" : {
    "width" : 18,
    "alt" : "don't tell anyone",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/32.gif",
    "text" : ":-$",
    "regexp" : ":-\\$",
    "height" : 18
  },
  "~:>" : {
    "width" : 18,
    "alt" : "chicken",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/52.gif",
    "text" : "~:>",
    "regexp" : "~:>",
    "height" : 18
  },
  "=:)" : {
    "width" : 20,
    "alt" : "bug",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/60.gif",
    "text" : "=:)",
    "regexp" : "=:\\)",
    "height" : 18
  },
  "(*)" : {
    "width" : 18,
    "alt" : "star",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/79.gif",
    "text" : "(*)",
    "regexp" : "\\(\\*\\)",
    "height" : 18
  },
  ":|" : {
    "width" : 18,
    "alt" : "straight face",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/22.gif",
    "text" : ":|",
    "regexp" : ":\\|",
    "height" : 18
  },
  ":((" : {
    "width" : 22,
    "alt" : "crying",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/20.gif",
    "text" : ":((",
    "regexp" : ":\\(\\(",
    "height" : 18
  },
  "8-x" : {
    "width" : 18,
    "alt" : "skull",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/59.gif",
    "text" : "8-X",
    "regexp" : "8-X",
    "height" : 18
  },
  "o:-)" : {
    "width" : 30,
    "alt" : "angel",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/25.gif",
    "text" : "O:-)",
    "regexp" : "O:-\\)",
    "height" : 18
  },
  ">:p" : {
    "width" : 18,
    "alt" : "phbbbbt",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/47.gif",
    "text" : ">:P",
    "regexp" : ">:P",
    "height" : 18
  },
  ">-)" : {
    "width" : 18,
    "alt" : "alien",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/61.gif",
    "text" : ">-)",
    "regexp" : ">-\\)",
    "height" : 18
  },
  "=((" : {
    "width" : 18,
    "alt" : "broken heart",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/12.gif",
    "text" : "=((",
    "regexp" : "=\\(\\(",
    "height" : 18
  },
  "l-)" : {
    "width" : 24,
    "alt" : "loser",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/30.gif",
    "text" : "L-)",
    "regexp" : "L-\\)",
    "height" : 18
  },
  ":@)" : {
    "width" : 18,
    "alt" : "pig",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/49.gif",
    "text" : ":@)",
    "regexp" : ":@\\)",
    "height" : 18
  },
  ">:/" : {
    "width" : 23,
    "alt" : "bring it on",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/70.gif",
    "text" : ">:/",
    "regexp" : ">:\\x2f",
    "height" : 18
  },
  "b-(" : {
    "width" : 18,
    "alt" : "feeling beat up",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/66.gif",
    "text" : "b-(",
    "regexp" : "b-\\(",
    "height" : 18
  },
  "$-)" : {
    "width" : 18,
    "alt" : "money eyes",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/64.gif",
    "text" : "$-)",
    "regexp" : "\\$-\\)",
    "height" : 18
  },
  ":-?" : {
    "width" : 18,
    "alt" : "thinking",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/39.gif",
    "text" : ":-?",
    "regexp" : ":-\\?",
    "height" : 18
  },
  ":)>-" : {
    "width" : 22,
    "alt" : "peace sign",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/67.gif",
    "text" : ":)>-",
    "regexp" : ":\\)>-",
    "height" : 18
  },
  ":-j" : {
    "width" : 26,
    "alt" : "oh go on",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/78.gif",
    "text" : ":-j",
    "regexp" : ":-j",
    "height" : 18
  },
  "%%-" : {
    "width" : 18,
    "alt" : "good luck",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/54.gif",
    "text" : "%%-",
    "regexp" : "%%-",
    "height" : 18
  },
  "%-(" : {
    "width" : 52,
    "alt" : "not listening",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/107.gif",
    "text" : "%-(",
    "regexp" : "%-\\(",
    "height" : 18
  },
  ":p" : {
    "width" : 18,
    "alt" : "tongue",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/10.gif",
    "text" : ":P",
    "regexp" : ":P",
    "height" : 18
  },
  "^:)^" : {
    "width" : 32,
    "alt" : "not worthy",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/77.gif",
    "text" : "^:)^",
    "regexp" : "\\^:\\)\\^",
    "height" : 18
  },
  ":-\"" : {
    "width" : 22,
    "alt" : "whistling",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/65.gif",
    "text" : ":-\"",
    "regexp" : ":-\\x22",
    "height" : 18
  },
  ":-<" : {
    "width" : 24,
    "alt" : "sigh",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/46.gif",
    "text" : ":-<",
    "regexp" : ":-<",
    "height" : 18
  },
  ":o3" : {
    "width" : 31,
    "alt" : "puppy dog eyes",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/108.gif",
    "text" : ":o3",
    "regexp" : ":o3",
    "height" : 18
  },
  ">:)" : {
    "width" : 18,
    "alt" : "devil",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/19.gif",
    "text" : ">:)",
    "regexp" : ">:\\)",
    "height" : 18
  },
  "=;" : {
    "width" : 18,
    "alt" : "talk to the hand",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/27.gif",
    "text" : "=;",
    "regexp" : "=;",
    "height" : 18
  },
  "8-|" : {
    "width" : 18,
    "alt" : "rolling eyes",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/29.gif",
    "text" : "8-|",
    "regexp" : "8-\\|",
    "height" : 18
  },
  "**==" : {
    "width" : 25,
    "alt" : "flag",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/55.gif",
    "text" : "**==",
    "regexp" : "\\*\\*==",
    "height" : 18
  },
  "o-+" : {
    "width" : 18,
    "alt" : "april",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/74.gif",
    "text" : "o-+",
    "regexp" : "o-+",
    "height" : 18
  },
  "8-}" : {
    "width" : 24,
    "alt" : "silly",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/35.gif",
    "text" : "8-}",
    "regexp" : "8-}",
    "height" : 18
  },
  "=))" : {
    "width" : 30,
    "alt" : "rolling on the floor",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/24.gif",
    "text" : "=))",
    "regexp" : "=\\)\\)",
    "height" : 18
  },
  ":-l" : {
    "width" : 18,
    "alt" : "frustrated",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/62.gif",
    "text" : ":-L",
    "regexp" : ":-L",
    "height" : 18
  },
  "b-)" : {
    "width" : 18,
    "alt" : "cool",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/16.gif",
    "text" : "B-)",
    "regexp" : "B-\\)",
    "height" : 18
  },
  ";)" : {
    "width" : 18,
    "alt" : "winking",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/3.gif",
    "text" : ";)",
    "regexp" : ";\\)",
    "height" : 18
  },
  ":>" : {
    "width" : 18,
    "alt" : "smug",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/15.gif",
    "text" : ":>",
    "regexp" : ":>",
    "height" : 18
  },
  ":-&" : {
    "width" : 18,
    "alt" : "sick",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/31.gif",
    "text" : ":-&",
    "regexp" : ":-&",
    "height" : 18
  },
  "<):)" : {
    "width" : 18,
    "alt" : "cowboy",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/48.gif",
    "text" : "<):)",
    "regexp" : "<\\):\\)",
    "height" : 18
  },
  ":-s" : {
    "width" : 18,
    "alt" : "worried",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/17.gif",
    "text" : ":-S",
    "regexp" : ":-S",
    "height" : 18
  },
  ";))" : {
    "width" : 18,
    "alt" : "hee hee",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/71.gif",
    "text" : ";))",
    "regexp" : ";\\)\\)",
    "height" : 18
  },
  "[-o<" : {
    "width" : 18,
    "alt" : "praying",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/63.gif",
    "text" : "[-O<",
    "regexp" : "\\[-O<",
    "height" : 18
  },
  "@};-" : {
    "width" : 18,
    "alt" : "rose",
    "src" : "//us.i1.yimg.com/us.yimg.com/i/mesg/emoticons7/53.gif",
    "text" : "@};-",
    "regexp" : "@};-",
    "height" : 18
  }
};
