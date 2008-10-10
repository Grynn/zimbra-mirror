/**
 * Copyright 2006-2008 Yahoo! Inc. All rights reserved.
 */

// ----------
//
// YML utility functions - chiefly for converting from YML to HTML and back
//
// YML here refers to messenger markup, not anything YOS-related.
//
// Dependencies: YMSGR core (_ymsgr.js)
//
// ----------


YMSGR.YMLUtil = {

	// NOTE: These numbers correspond to Candygram's UI for the emoticon picker.
	// They do not match up with the YML spec.  I am nevertheless placing this
	// here because it is closely related. -- kevykev
	//
	// TODO: The picker UI could just send a string instead of a number, no?
	//
	emoticonIDToText : {
		"01":":)",		"02":":(",		"03":";)",		"04":":d",		
		"05":";;)",		"06":":-/",		"07":":x",		"08":':">',		
		"09":":p",		"10":":-*",		"11":":O",		"12":"x(",		
		"13":":>",		"14":"b-)",		"15":":-s",		"16":">:)",		
		"17":":((",		"18":":))",		"19":":|",		"20":"/:)",		
		"21":"o:-)",	"22":":b",		"23":"=;",		"24":"i-)",		
		"25":"8-|",		"26":":-&",		"27":":-$",		"28":"[-(",		
		"29":":o)",		"30":"8-}",		"31":"(:|",		"32":"=p~",		
		"33":":-?",		"34":"#-o",		"35":"=d>",		"36":":@)",		
		"37":"3:-o",	"38":":(|)",	"39":"~:>",		"40":"@};-",		
		"41":"%%-",		"42":"**==",	"43":"(~~)",	"44":"~o)",		
		"45":"*-:)",	"46":"8-x",		"47":"=:)",		"48":">-)",		
		"49":":-l",		"50":"<):)",	"51":"[-o<",	"52":"@-)",		
		"53":"$-)",		"54":':-"',		"55":":^o",		"56":"b-(",		
		"57":":)>-",	"58":"[-x",		"59":"\:d/",	"60":">:d<",		
		"61":"o->",		"62":"o=>",		"63":"o-+",		"64":"(%)" 
	},


	// Convert a YML string to HTML, with handling for FONT tag wrappers.
	// For specific reasons unknown, we wrap messages in FONT tags
	// to resolve some webclient/deskclient interop issues. -- kevykev
	//
	ymlToHtml : function ( msg, forSelf ) {
		
		// Even when processing our own text from a send (forSelf==true),
		// we are given YML not HTML
		
		// PERF: TODO: Even though the message is expected to be YML at this point,
		// this first addClosingTags call is not pointless -- there are FONT tags
		// added by our client at send.  Not sure why... yes, because we repost
		// sent messages to ourselves as if received (to post it into the
		// conversation), but I don't know why we wrap outgoing messages this way.
		// -- kevykev
		
		msg = YMSGR.YMLUtil.addClosingTags( msg );
		msg = YMSGR.YMLUtil.ymlToHtmlRaw ( msg, true );
		
		if ( !forSelf )
		{
			msg = YMSGR.YMLUtil.addDefaultFont( msg, false );
			msg = YMSGR.YMLUtil.addClosingTags( msg );
		}		

		return msg;
	},


	// ymlToHtmlRaw - based on HTMLize borrowed from the messenger team a while back.
	// We wrap this with some handling for font tags that is Mail-specific. -- kevykev
	//
	// DO NOT USE THIS CODE ON A PRODUCTION WEB PAGE!@#!@#!
	// run it though jsob first. please. ping me to get a copy. henrit@yahoo-inc.com
	//
	// htmlize. with a big of stuff from global.js, this chunk is pretty
	// self-contained.
	// 
	// XXX put a preprocessing layer that:
	// - eat spam
	// - convert yahoo msg into html ( url, tags, smileys )
	//
	// ok. this guy doesn't eat spam, but hopefully convert most stuff correctly
	// spam is handled in chat.html I think (except for smiley limit)
	// those should be regular expressions, not string. I could use \w. ph34r.
	// but if I do that, I lose my cool hash lookup. could be slower.. grumf..

	ymlToHtmlRaw : function ( str, allowCR, allowSpc, noFormat ) {
		'parent:nomunge,parent:nochildmunge';
	
		/* USE THIS PATH TO LOAD IMAGES FROM A WEB SERVER */
		// var SMILEY_PATH = "http://img1.dcx.yahoo.com/i/chat/dhtml/us/v101/sm/";
		/* USE THIS PATH TO LOAD IMAGES LOCALLY */
		// var SMILEY_PATH = "http://mail.yimg.com/us.yimg.com/i/mesg/tsmileys2/"; /* easier for testing */
	
		var SMILEY_PATH = "http://mail.yimg.com/us.yimg.com/i/mesg/emoticons7/";
		
		/* USE THIS PATH TO PASS REGRESSION TESTS */
		//var SMILEY_PATH = "";
		
		
		var MAX_SMILEYS = 6;
		var HTMLIZE_MIFS = 6;
		var HTMLIZE_MAFS = 32; /* too high, imho */
	
		var count= MAX_SMILEYS;
			
		var table_smileys = {
		    ":)":"1", ":-)":"1",
		    ":(":"2", ":-(":"2",
		    ";)":"3", ";-)":"3",
		    ":d":"4", ":-d":"4",
		    ";;)":"5", ";;-)":"5",
		    ">:d<":"6",
		    ":-/":"7", ':-\\':"7",
		    ":x":"8", ":-x":"8", 
		    ':">':"9",
		    ":p":"10", ":-p":"10",
		    ":-*":"11", ":*":"11", "=*":"11",
		    "=((":"12",
		    ":o":"13", ":-o":"13",
		    "x-(":'14', "x(":"14",
		    ":>":"15", ":->":"15",
		    "b-)":"16",
		    ":-s":"17",
		    "#:-s":"18",
		    ">:)":"19",
		    ":((":"20", ":-((":"20",
		    ":))":"21", ":-))":"21",
		    ":|":"22", ":-|":"22",
		    "/:)":"23", "/:-)":"23",
		    "=))":"24",
		    "o:)":"25", "0:)":"25", "o:-)":"25",
		    ":b":"26", ":-b":"26",
		    "=;":"27",
		    "i-)":"28", "|-)":"28",
		    "8-|":"29",
		    "l-)":"30",
		    ":-&":"31",
		    ":-$":"32",
		    "[-(":"33",
		    ":o)":"34", ":0)":"34",
		    "8-}":"35",
		    "<:-p":"36",
		    "(:|":"37",
		    "=p~":"38",
		    ":-?":"39",
		    "#-o":"40",
		    "=d>":"41",
		    ":-ss":"42",
		    "@-)":"43",
		    ":^o":"44",
		    ":-w":"45",
		    ":-<":"46",
		    ">:p":"47",
		    "<):)":"48",   	
		    /* hidden smileys */
		    ":@)":"49",
		    "3:-o":"50", "3:-0":"50",
		    ":(|)":"51",
		    "~:>":"52",
		    "@};-":"53",
		    "%%-":"54",
		    "**==":"55",
		    "(~~)":"56",
		    "~o)":"57",
		    "*-:)":"58",    
		    "8-x":"59",
		    "=:)":"60", "=:-)":"60",
		    ">-)":"61",
		    ":-l":"62",
		    "[-o<":"63",
		    "$-)":"64",
		    ':-"':"65", 
			"b-(":"66",
			":)>-":"67",
			"[-x":"68",
			"\\:d/":"69",
			">:/":"70",
			";))":"71",
			"o->":"72",
			"o=>":"73",
			"o-+":"74",
			"(%)":"75",
			":-@":"76",
			"^:)^":"77",
			":-j":"78",
			"(*)":"79",
		    ":)]":"100",
		    ":-c":"101",
			"~x(":"102",
			":-h":"103",
		    ":-t":"104",
		    "8->":"105",
		    ":-??":"106",
		    "%-(":"107",
		    ":o3":"108",
		
			// extended smileys
			">:o":"81",
			"%-}":"91",
			"<^>":"92",
			"(|)":"93",
			"/\\/*":"94",
			">:#":"95",
			":)~*":"96",
			":~)":"97",
		 	"->xo":"98",
			"|:d|":"99",
		 	
			// [fix bug 1595072]
		 	"x_x":"109", //- Don't want to see
		 	":!!":"110", //- Hurry up
		 	"\\m/":"111", //- Rock on
		 	":-q":"112", //- Thumbs down
		 	":-bd":"113", //- Thumbs up
		 	"^#(^":"114", // - Wasn't me
		 	":bz":"115" //- Bee
		};
		
		var table_colors = {
		  'black' :"#000000",
		  'red'   :"#ff0000",
		  'green' :"#008200",
		  'yellow':"#848200",
		  'blue'  :"#0000ff",
		  'purple':"#840084",
		  'cyan'  :"#008284",
		  'orange':"#ff8000",
		  'pink'  :"#ff0084",
		  'gray'  :"#848284"
		};
	
		// The bare minimum for safe message displaying. never get caught without one.
		//
		function entitize (str) {
		  str = str.replace(/&/g,"&"+"amp;");
		  str = str.replace(/</g,"&"+"lt;");
		  str = str.replace(/>/g,"&"+"gt;");
		  str = str.replace(/'/g,"&"+"#x27;");
		  str = str.replace(/"/g,"&"+"quot;");
		  return str;
		}
		
		function StringBuffer() {
			'parent:nomunge,parent:nochildmunge';
			var s=this.s=[];
			for (var i=0;i<arguments.length;i++)
				s.push(arguments[i]);
			if (s.length==0)
				s.push("");
			
			this.$ = function (str) {
			    s.push(str);
			    return this;
			};
			
			this.toString = function () {
			    if (s.length>1)
			        s=[s.join("")];
			    return s[0];
			};
		}
	
		function smileyize(str) {
			var sb = new StringBuffer;
					
			if(str.length < 2)
				return sb.$(chew(str));
					
			for (var i=0;i<str.length;i++) {
				var j=4,k=0;
				if (count<=0) j=0;
				for (;(!k)&&(j>=2);j--)
					if(str.substring(i, i+j).toLowerCase() != "eval")
						k = table_smileys[str.substring(i,i+j).toLowerCase()];
				if (k) {
					sb.$('<img border=0 src="' + SMILEY_PATH + k + '.gif">');
					i+=j;
					count--;
				} else {
					sb.$(chew(entitize(str.charAt(i))));
				}
			}
	
			return sb.toString();
		}
		
		function URLize (str) {
			var tmp = new StringBuffer;
			
			if (str.toLowerCase().indexOf("www.")==0)
				tmp.$("http://");
			
			for (var i=0;i<str.length;i++) {
				var d=str.charAt(i);
				var c=d.toLowerCase();
				if (((c<'a')||(c>'z'))&&((c<'0')||(c>'9'))&&(";/?:@&=+$,-_.!~*'()#%".indexOf(c)==-1)) {
					tmp.$(escape(d));
				} else {
					tmp.$(d);
				}
			}

			return tmp;
		}
		
		function chew(str) {
			if (allowCR)
			{
				str = str.replace(/\r\n/g,"<br>");
				str = str.replace(/\r/g,"<br>");
				str = str.replace(/\n/g,"<br>");
			}
			if (allowSpc)
				str = str.replace(/\s/g,"&nbsp;");
			return str;
		}
		
		function lookForURL(str) {
			var r = str.match(/(http:\/\/|https:\/\/|www\.|ftp:\/\/|mailto:\/\/)\S+/i);
			if (r) {
				var s='';
				if (r.index>0)
					s+=lookForURL(str.substring(0,r.index));
				// do we need URLHack here?
				s+= '<a target=_blank href="' + URLize(r[0]) + '">' + entitize(r[0]) + '</a>';
				if ( (r.index + r[0].length) < str.length ) 
					s+=lookForURL(str.substring( r.index + r[0].length ));
			
				return s;
			} else {
				return smileyize(str);
			}
		}
		 
		function split(str,match) {
			var a=[];
			var j = str.indexOf(match);
			while (j>-1) {
				a.push(str.substring(0,j));
				str=str.substring(j,1e5);
				j=str.indexOf(match,match.length);
			}
			a.push(str);
			return a;
		}
		
		
		function lookForEsc( str ) {
			
			var a1=split(str,"\033[");
			if (a1.length==1) {
				// ok. good time to look for lost URLs:			
				return lookForURL(str); // and for smileys.
			} else {
				// so we have an escape sequence
				var a2=new StringBuffer;
				for (var i=0;i<a1.length;i++) {
					var s=a1[i];
		 			if (s.indexOf('\033[')==-1) {
		 				// should this be lookForURL ? since this doesn't have any escape sequences or tags
		 				if (s)a2.$(lookForURL(s));//,allowCR,allowSpc));
					} else {
						var j = s.indexOf('m');
						if (j==-1) {
							// something wrong here ... ? someone not playing nice ? \033["xxxx"m
							// processText the rest of the string ?
							a2.$(lookForURL(s.substring(1)));//,allowCR,allowSpc));
						} else {
							if (!noFormat) {
						var t = new YahooTag(convertEsc2Tag(s.substring(0,j+1)));
						a2.$(t);
							}
							s=s.substring(j+1);
							if(s)a2.$(lookForURL(s));//,allowCR,allowSpc));
						}
					}
				}
				return a2;
			}	
		}
		
		function processText(str, allowCR, allowSpc, noFormat) {
		
			// split into pieces. can't use RegExp because Opera breaks on it.
			if (str.indexOf("<") == -1) {
				return lookForEsc(str);
			} else {
				var a2=new StringBuffer;
				var tag_free_start=0, tag_free_end=str.indexOf('<');
				var backup;
				var specialTag;
				var i=str.indexOf('<');
				while (i!=-1) {
					var end = str.indexOf('>',i);
					if (end==-1) {
						tag_free_end=str.length;
						i=-1;
					}else {
						if (!noFormat) {
			
							var t = new YahooTag(str.substring(i,end+1));
							if (t.notatag) {
								t = null;
								tag_free_end = str.indexOf('<', tag_free_end + 1);
								if (tag_free_end==-1) {
									tag_free_end = end + 1;
								} else {
									end = tag_free_end - 1;
								}
							} else {
			
							    // we know we have a tag, grab the tag-free zone, and lookForURL it.
						        // no. call processText to catch any embedded esc[ sequences instead.
								// that call to processText will eventually call lookForURL.
								tag_free_end=i;
								if (tag_free_start<tag_free_end) {
									var tmp = str.substring(tag_free_start,tag_free_end);
									a2.$(processText(tmp,allowCR,allowSpc, noFormat));
								}
								tag_free_start=end+1;
								if (t.special) {
									if (t.endTag) {
										if (backup) {
											backup.$(applySpecialTag(specialTag, a2));
											a2=backup;
											backup=null;
										}
									} else {
										if (backup) {
											backup.$(a2);
											a2=backup;
										}
										backup=a2;
										a2=new StringBuffer();
										specialTag = t;
									}
								} else {
									a2.$(t);
								}
							}
						}
						end++;
						i = str.indexOf('<', end);
					}
				}
				tag_free_end=str.length;
				if (tag_free_start<tag_free_end) {
					var tmp = str.substring(tag_free_start,tag_free_end);
					a2.$(lookForEsc(tmp));//, allowCR, allowSpc));
				}
	
				if (backup) {
					backup.$(a2);
					a2=backup;
				}
			}
			return a2;
		}
		
		function convertEsc2Tag(esc) {
			var c=esc.substring(2,esc.length-1);
			switch (c) {
			case '0': return "</font>";
			case '1': return "<b>";
			case '2': return "<i>";
			case '30':return "<black>";
			case '31':return "<blue>";
			case '32':return "<cyan>";
			case '33':return "<gray>";
			case '34':return "<green>";
			case '35':return "<pink>";
			case '36':return "<purple>";
			case '37':return "<orange>";
			case '38':return "<red>";
			case '39':return "<yellow>";
			case '4': return "<u>";
			case 'l': return "<url>";
			case 'x1':return "</b>";
			case 'x2':return "</i>";
			case 'x4':return "</u>";
			case 'xc':return "</font>";
			case 'xl':return "</url>";
			default:
				return "<"+c+">";
			}
		}
		
		/*
			<color>	</color>
			<#abcdef> </#abcdef>
			<b> <i> <u> </b> </i> </u>
			<url=...>
			<url> </url>
			<font face="..." size=".."> </font>
			<fade #abcdef,#abcdef,#abcdef> </fade>
			<alt #abcdef,#abcdef> </alt>
		*/
		
		function Color(r,g,b) {
		 	'parent:nomunge,parent:nochildmunge';
			if (typeof g=="undefined") {
				if (r.charAt(0)=='#')
						r=r.substring(1);
				var i = parseInt(r,16);
				this.red=(i&0xFF0000)>>16;
				this.green=(i&0xFF00)>>8;
				this.blue=(i&0xFF);
			} else {
				this.red=r;
				this.green=g;
				this.blue=b;
			}
			
			this.toString = function () {
				function hex(i) {var a="00"+i.toString(16);return a.substring(a.length-2)}
				return "#"+hex(this.red)+hex(this.green)+hex(this.blue);
			};
		}
		
		var mifs = HTMLIZE_MIFS;
		var mafs = HTMLIZE_MAFS;
		
		function YahooTag(tag) {
		 	'parent:nomunge,parent:nochildmunge';
			
			// cut < and >, isolate tagName, split args into an object[name=value]
			// Opera regexp support is wonderfully broken. improvise.
			// yes, this code could be simpler. but then Opera would choke on the more complex regexp.
			tag = tag.replace(/\s+/g,' ');
	
			var r=tag.match(/<(.*)>/);
			if (r==null) {
				this.notatag = true;
				this.str="";
				this.toString=function(){return this.str};
				this.length = this.str.length;
				return;
			}
			var i=r[1].indexOf(' '); 
			if (i!=-1) {
				r[2]=r[1].substring(i+1,1e4);
				r[1]=r[1].substring(0,i);
			} else {
				r[2]='';
			}
			this.tagName=this.tag=r[1].toLowerCase();	
			this.extra=r[2];
			this.endTag=false;
			if (this.tagName.charAt(0)=='/') {
				this.endTag=true;
				this.tagName=this.tagName.substring(1);
			}
			this.special=false;
			this.notatag=false;//innocent until proven bogus
			var s='';
			switch (this.tagName) {
			case 'alt':
			case 'fade': /* the next line is part of why I like javascript like I do. */
				if (this.endTag) {
					this.special=true;
				} else {
					this.colors = this.extra.split(",");
					var len = this.colors.length;
					for(var i = 0; i < len; i++) {
						this.colors[i] = new Color(this.colors[i]);
	//					.apply(function(s){return new Color(s)});
					}
					
					if (this.colors.length>1) {
						this.special=true;
					} else {
						if (this.colors.length==1) {
							// (?) N does not appear to exist.
							// This is present in old versions of this file,
							// so uncovering the original intent will be fun.
							// It appears to be handling the special case
							// where the fade command has one color.
							// I am replacing it with the line after.
							// Is it supposed to be a call to applySpecialTag?
							// -- kevykev 2008.07.30
							//
							// s=N('<font color="#1">', this.colors[0]);
							s= '<font color="' + this.colors[0] + '">';
						}
					}
				}
				break;
			case 'font':
				if (this.endTag) {
					s="</font>";
				} else {
					var size='';
					var face='';
					r = this.extra.match(/size="([^"]*)"/);
					if (r!=null) {
						// if the size contains non-numeric chars/ leave as is
						var s;
						if (r[1].match(/[^0-9]/))
						{
								s = entitize(r[1]);
						}
						else
						{
							s=r[1]-0;
							if (s<mifs) s=mifs;
							if (s>mafs) s=mafs;
							
							s = entitize(s+'')+'pt';
						}
							
						size=' style="font-size:'+s+'"';
					}
					r = this.extra.match(/face="([^"]*)"/);
					if (r!=null) {
						face=' face="'+entitize(r[1])+'"';
					}
					if (size+face)
						s="<font"+size+face+">";
					else
						s=""; // empty font tags are both pointless and annoying.
				}
				break;
			case 'b':
			case 'u':
			case 'i':
				s="<"+this.tag+">"; 
				break;
			case 'url':
				break;
			case 'black':
			case 'blue':
			case 'cyan':
			case 'gray':
			case 'green':
			case 'pink':
			case 'purple':
			case 'orange':
			case 'red':
			case 'yellow':
				if (this.endTag)
					s="</font>";
				else
					s='<font color="'+table_colors[this.tagName]+'">';
				break;
			default:
				if (this.tagName.indexOf("url=")==0) {
					s=lookForURL(this.tagName.substring(4));
				} else if (this.tagName.charAt(0)=='#') {
					s='<font color="'+entitize(this.tagName)+'">';
				} else {
					// don't be smart here, or you break smiley processing.
					this.notatag=true;
					s=''; //"&"+"lt;"+processText(tag.substring(1,1e3), allowCR, allowSpc, noFormat);
				}
			}
			this.str=s;
			// now would be a good time to compute a string for it. (what about fade and alt though?) 
			//this.toString=function(){return "["+this.tagName+"("+this.endTag+"){"+this.extra+"}]"}
			this.toString=function(){return this.str};
			this.length = this.str.length;
		}
		
		
		var FADE_COLORS = 64;
		var cached_list=[];
		var output_list=[];
		function fade(list, index, length) {
			if ((list.length<2)||(length<2))
				return list[0];
			if (list!=cached_list) {
				cached_list = list;
				var cached_i = 0;
				var list_i = list[0];
				var list_i_1 = list[1];
				var ri = list_i.red;
				var gi = list_i.green;
				var bi = list_i.blue;
				var ri_1 = list_i_1.red;
				var gi_1 = list_i_1.green;
				var bi_1 = list_i_1.blue;
				var coef=(list.length-1)/FADE_COLORS;
				for (var ind=0; ind<FADE_COLORS;ind++) {
					var ff = ind*coef;
					var i = Math.floor(ff);
					if (i!=cached_i) {
						ri = ri_1;
						gi = gi_1;
						bi = bi_1;
						list_i_1 = list[i+1];
						ri_1 = list_i_1.red;
						gi_1 = list_i_1.green;
						bi_1 = list_i_1.blue;
						cached_i = i;
					}
					var f = ff-i;
					var f2 = 1-f;
					var red	 = Math.floor(ri*f2 + ri_1*f);
					var green = Math.floor(gi*f2 + gi_1*f);
					var blue	= Math.floor(bi*f2 + bi_1*f);
					output_list[ind] = new Color(red,green,blue);
				}
			}
			var l = length-1;
			if (index>l)
				index=l;
			var r = Math.floor(index*(FADE_COLORS-1)/l);
			return output_list[r];
		}
		
		function applySpecialTag(tag, str) {
			str=str.toString();
			var i=0, j=0;
			for (;i<str.length;i++) {
				if (str.charAt(i)=='<') {
					i=str.indexOf('>',i);
				} else if (str.charAt(i)=='&') {
					i=str.indexOf(';',i);
					j++;
				} else
					j++;
			}
			var len=j;
			var out=new StringBuffer();
			for (i=0,j=0;i<str.length;i++) {
				if (str.charAt(i)=='<') {
					var t=str.indexOf('>',i);
					out.$(str.substring(i,t+1));
					i=t;			
				} else {
					var txt;
					if (str.charAt(i)=='&') {
						var t=str.indexOf(';',i);
						txt = str.substring(i,t+1);
						i=t;
					} else {
						txt=str.charAt(i);
					}
					var tmp;
					if (tag.tagName=="fade")
						tmp = fade(tag.colors,j,len);
					else
						tmp = tag.colors[j%tag.colors.length];
					out.$('<font color="').$(tmp).$('">').$(txt).$("</font>");
					j++;
				}
			}
			
			return out;
		}
		
		return (processText(str, allowCR, allowSpc, noFormat)).toString();
	},


	// This wrapper to the raw Dom->YML convertor does some special font handling.
	//
	// See comments above for the YML->HTML wrapper.
	//
	domToYml : function ( body, forIE ) {

		if ( !forIE )
			// PERF: TODO: surely we can do this without re-rendering the body... -- kevykev
			body.innerHTML = YMSGR.YMLUtil.removeTrailingBR ( body.innerHTML );

		var msg = YMSGR.YMLUtil.domToYmlRaw( body, forIE );

		// stupid hack because messenger/cg default fonts are different
		// so we have to force inclusion of a default font tag.  LAME!
		// get first <font tag> or first non-tag text... whichever comes first

		// No idea why the \u00a0 stuff is in here -- kevykev

		return YMSGR.YMLUtil.addDefaultFont(msg, true, body.style).replace( /\u00a0/g, ' ' );
	},


	// Convert and HTML DOM to an YML string.
	//
	// the approach being walking through the dom and evaluating each node
	// if its a text node .. append it to the output ... otherwise
	// recursively evaluate the special nodes ... start from the end coz some nodes
	// might be merged during processing and you might end up trying to access
	// elements that dont exist anymore ...
	domToYmlRaw : function ( msg, forIE ) {
		
		// the sizeMap maps from the size returned by Candygram
		// to the size expected by YML
	   var sizeMap = ['8', '10', '12', '14', '18', '24', '36'];
		
		
		var output="";
		var children = null;
		var n = msg.childNodes.length;
		if(n > 0)
			children = msg.childNodes;
		
		if ( msg.nodeType == 3 ) {
			// text node : just return the value
			return msg.nodeValue;
		} else if ( msg.nodeType == 1 ) { 
			// element node : process it !
			switch ( msg.tagName ) {
			
			case "BR" :
				output += "\n";
				break;

			case "B" :
			case "STRONG" :	
				output += "<b>";
				for(var i = 0; i < n; i++)
					output += YMSGR.YMLUtil.domToYmlRaw(children[i], forIE);
				output += "</b>";
				break;
							
			case "I":
			case "EM" :	
				output += "<i>";
				for(var i = 0; i < n; i++)
					output += YMSGR.YMLUtil.domToYmlRaw(children[i], forIE);
				output += "</i>";
				break;
						
			case "U" :	
				output += "<u>";
				for(var i = 0; i < n; i++)
					output += YMSGR.YMLUtil.domToYmlRaw(children[i], forIE);
				output += "</u>";
				break;
			
			case "DIV" :
			case "SPAN" :
			case "FONT" : 
				/* UPDATE: 8/9/06 seems to require both actually.  might need to 
				 * retest with POSTMAN
				 */

				// so here's the deal : you can have either font or size
				// but not both .. go figure ..
				// all this should go into a static YML library finally !
				var fntface;
				var fntsize;
				var fntclr;
				var fntbld = false;

				// style tag overrides everything else
				var style = msg.getAttribute("style");
				var styleFont = false;
				
				fntface = msg.getAttribute("face");
				fntsize= msg.getAttribute("size");
				fntclr = msg.getAttribute("color");

				if (style && msg.style.fontFamily && msg.style.fontFamily.length > 0)
					fntface = msg.style.fontFamily;
					
				if (style && msg.style.fontSize && msg.style.fontSize.length > 0)
				{
					styleFont = true;
					fntsize = msg.style.fontSize;
				}

				if (style && msg.style.color && msg.style.color.length > 0)
					fntclr = msg.style.color;
					
				if (style && msg.style.fontWeight == "bold")
					fntbld = true;

				if(fntface && fntsize) {
					output += "<font face=\"" + fntface + "\" size=\"" + (styleFont ? fntsize : sizeMap[fntsize - 1]) + "\">";
				}
				else if (fntface && !fntsize) {
					output += "<font face=\"" + fntface + "\">";								
				}
				else if(!fntface && fntsize) {
					// only do the next step if fntsize is numeric only
					if (fntsize.match(/\D/))
						output += "<font size=\"" + fntsize + "\">";
					else
						output += "<font size=\"" + sizeMap[fntsize - 1] + "\">";
				}

				if(fntclr) {
					// handle rgb case
					fntclr = fntclr.replace(/\s/g, '');
					var rgbclr;
					if (rgbclr = fntclr.match(/rgb\((\d*),(\d*),(\d*)\)/i))
					{
						fntclr = "#";
						var hexArr = new Array("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F");
						var tmp = Math.floor((rgbclr[1]-0)/16);
						fntclr += hexArr[tmp] + hexArr[(rgbclr[1]-0)-(16*tmp)];
															
						tmp = Math.floor((rgbclr[2]-0)/16);
						fntclr += hexArr[tmp] + hexArr[(rgbclr[2]-0)-(16*tmp)];
						
						tmp = Math.floor((rgbclr[3]-0)/16);
						fntclr += hexArr[tmp] + hexArr[(rgbclr[3]-0)-(16*tmp)];
					}
					
					output += "\033[" + fntclr + "m";
				}
					
				if(fntbld) {
					output += "<b>";
				}

				for(var i = 0; i < n; i++) {
					// we're not closing out the color tags to support 3rd party clients
					if( (children[i].nodeType == 3) && fntclr)
						output += "\033[" + fntclr + "m";								

					output += YMSGR.YMLUtil.domToYmlRaw(children[i], forIE);									
				}
				
				if(fntbld) {
					output += "</b>";
				}

				// we used to close font tags here so html is well-formed--
				// we stopped because it breaks third-party clients
				
				if(fntsize || fntface) {
					output += "</font>";
				}
				
				// IE uses DIVs to create line breaks.  However, if the current DIV is the 
				// last child in its parent container, we don't want to add an extra newline at
				// the end
				if (forIE && msg.tagName == "DIV" && (msg !== msg.parentNode.lastChild))
					output += "\n";

				break;
							
				
			default :
				for(var i = 0; i < n; i++)
					output += YMSGR.YMLUtil.domToYmlRaw(children[i], forIE);
				break;
			}
			
			return output;
	
		} else {
			// some random node ( a comment probably ?)
			return "";
		}
	},


	// Not sure if this is relevant anymore -RC 11.14.07
	// add closing tags since messenger client seems to never 
	// provide them - grr
	addClosingTags : function ( str ) {
		var fin = str;
	
		var tmpArray = str.split("<font");
		var openCount = tmpArray.length - 1;		
		tmpArray = str.split("</font>");
		var closeCount = tmpArray.length - 1;
		
		for (var i=0; i<(openCount-closeCount); i++)
		{
			fin += "</font>";
		}
		
		return fin;
	},


	// another lame hack function because in the input pane
	// firefox seems to append a <br> inside the font tags
	// if the user inputs nothing or at least two words
	//
	removeTrailingBR : function ( str ) {
		var matchArr;
		var retStr = str;
		if ( matchArr = str.match( /^(.*)<br>(.*)$/ ) )
		{
			if ( matchArr[2].match(/>?[^<>]+?</) )
				retStr = matchArr[1] + "<br>" + matchArr[2];
			else
				retStr = matchArr[1] + matchArr[2];
		}
		
		return retStr;
	},


	// lame hack function to insert the default font tag
	// to deal with messenger/cg default font incompatibilities
	// and the fact that execCommand assumes font size 3 as 
	// the default for everyone
	//
	// pass in styles if available
	addDefaultFont : function ( str, toYML, style ) {
	
		// ghetto string conversion
		if (!str.match)
			str = "" + str;
		
        // add styles if passed in
        if (style)
        {
        	var sizeStr = "";
        	var famStr = "";
        	
        	if (style.fontFamily && style.fontFamily != "")
        		famStr = 'face=\"' + style.fontFamily + '\"';
        	
        	if (style.fontSize && style.fontSize != "")
        		sizeStr = 'size=\"' + style.fontSize.replace(/pt/, '') + '\"';
        	
        	if (sizeStr.length > 0 || famStr.length > 0)
        		str = '<font ' + famStr + ' ' + sizeStr + '>' + str + '</font>';
        	        	
        	return str;
        }
        else
        {	
            var fin = "";
            var pre = "";
            var tmp = str;
            
	        while (tmp.match && tmp.match( /^<(.*?)>/ ))
	        {
	            var tag = RegExp.$1;
	        	tmp = tmp.replace( /^<(.*?)>/, "" );            
	
	            if ((tag.indexOf("font") == 0) && (tag.indexOf("size") < 0))
	            {
	                fin = pre + "<" + tag + " size=\"" + (toYML ? "12" : "2") + "\">" + tmp;
	                break;
	            }
	            else if (tag.indexOf("font") == 0)
	            {
	                fin = pre + "<" + tag + ">" + tmp;
	                break;
	            }
	            else
	                pre += "<" + tag + ">";
	        }
	
	        // Vipul : we dont need to close the font tag ... to comply with the desktop client
	        if (fin.length == 0)
	            fin = "<font size=\"" + (toYML ? "12" : "2") + "\">" + pre + tmp + "</font>";
	
	        return fin;
        }
	}

};
