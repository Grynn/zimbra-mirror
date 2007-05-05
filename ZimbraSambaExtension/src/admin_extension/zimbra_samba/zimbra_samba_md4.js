/**
 * A JavaScript implementation of the RSA Data Security, Inc. MD4 Message
 * Digest Algorithm, as defined in RFC 1320.
 * Version 2.1 Copyright (C) Jerrad Pierce, Paul Johnston 1999 - 2002.
 * Other contributors: Greg Holt, Andrew Kepert, Ydnar, Lostinet
 * Distributed under the BSD License
 * See http://pajhome.org.uk/crypt/md5 for more info.
**/

/**
 * Modified by Greg Solovyev @ Zimbra (c) (2007)
 */
function ZaSambaUtil () {
	
}

/*
 * Configurable variables. You may need to tweak these to be compatible with
 * the server-side, but the defaults work in most cases.
 */
ZaSambaUtil.hexcase = 0;  /* hex output format. 0 - lowercase; 1 - uppercase        */
ZaSambaUtil.b64pad  = ""; /* base-64 pad character. "=" for strict RFC compliance   */
ZaSambaUtil.chrsz   = 16;  /* bits per input character. 8 - ASCII; 16 - Unicode      */

/*
 * These are the functions you'll usually want to call
 */
ZaSambaUtil.hex_md4 = function (s){ return ZaSambaUtil.binl2hex(ZaSambaUtil.core_md4(ZaSambaUtil.str2binl(s), s.length * ZaSambaUtil.chrsz));}
ZaSambaUtil.b64_md4 = function (s){ return ZaSambaUtil.binl2b64(ZaSambaUtil.core_md4(ZaSambaUtil.str2binl(s), s.length * ZaSambaUtil.chrsz));}
ZaSambaUtil.str_md4 = function (s){ return ZaSambaUtil.binl2str(ZaSambaUtil.core_md4(ZaSambaUtil.str2binl(s), s.length * ZaSambaUtil.chrsz));}
ZaSambaUtil.hex_hmac_md4 = function (key, data) { return ZaSambaUtil.binl2hex(ZaSambaUtil.core_hmac_md4(key, data)); }
ZaSambaUtil.b64_hmac_md4 = function (key, data) { return ZaSambaUtil.binl2b64(ZaSambaUtil.core_hmac_md4(key, data)); }
ZaSambaUtil.str_hmac_md4 = function (key, data) { return ZaSambaUtil.binl2str(ZaSambaUtil.core_hmac_md4(key, data)); }

/*
 * Calculate the MD4 of an array of little-endian words, and a bit length
 */
ZaSambaUtil.core_md4 = function (x, len)
{
  /* append padding */
  x[len >> 5] |= 0x80 << (len % 32);
  x[(((len + 64) >>> 9) << 4) + 14] = len;
  
  var a =  1732584193;
  var b = -271733879;
  var c = -1732584194;
  var d =  271733878;

  for(var i = 0; i < x.length; i += 16)
  {
    var olda = a;
    var oldb = b;
    var oldc = c;
    var oldd = d;

    a = ZaSambaUtil.md4_ff(a, b, c, d, x[i+ 0], 3 );
    d = ZaSambaUtil.md4_ff(d, a, b, c, x[i+ 1], 7 );
    c = ZaSambaUtil.md4_ff(c, d, a, b, x[i+ 2], 11);
    b = ZaSambaUtil.md4_ff(b, c, d, a, x[i+ 3], 19);
    a = ZaSambaUtil.md4_ff(a, b, c, d, x[i+ 4], 3 );
    d = ZaSambaUtil.md4_ff(d, a, b, c, x[i+ 5], 7 );
    c = ZaSambaUtil.md4_ff(c, d, a, b, x[i+ 6], 11);
    b = ZaSambaUtil.md4_ff(b, c, d, a, x[i+ 7], 19);
    a = ZaSambaUtil.md4_ff(a, b, c, d, x[i+ 8], 3 );
    d = ZaSambaUtil.md4_ff(d, a, b, c, x[i+ 9], 7 );
    c = ZaSambaUtil.md4_ff(c, d, a, b, x[i+10], 11);
    b = ZaSambaUtil.md4_ff(b, c, d, a, x[i+11], 19);
    a = ZaSambaUtil.md4_ff(a, b, c, d, x[i+12], 3 );
    d = ZaSambaUtil.md4_ff(d, a, b, c, x[i+13], 7 );
    c = ZaSambaUtil.md4_ff(c, d, a, b, x[i+14], 11);
    b = ZaSambaUtil.md4_ff(b, c, d, a, x[i+15], 19);

    a = ZaSambaUtil.md4_gg(a, b, c, d, x[i+ 0], 3 );
    d = ZaSambaUtil.md4_gg(d, a, b, c, x[i+ 4], 5 );
    c = ZaSambaUtil.md4_gg(c, d, a, b, x[i+ 8], 9 );
    b = ZaSambaUtil.md4_gg(b, c, d, a, x[i+12], 13);
    a = ZaSambaUtil.md4_gg(a, b, c, d, x[i+ 1], 3 );
    d = ZaSambaUtil.md4_gg(d, a, b, c, x[i+ 5], 5 );
    c = ZaSambaUtil.md4_gg(c, d, a, b, x[i+ 9], 9 );
    b = ZaSambaUtil.md4_gg(b, c, d, a, x[i+13], 13);
    a = ZaSambaUtil.md4_gg(a, b, c, d, x[i+ 2], 3 );
    d = ZaSambaUtil.md4_gg(d, a, b, c, x[i+ 6], 5 );
    c = ZaSambaUtil.md4_gg(c, d, a, b, x[i+10], 9 );
    b = ZaSambaUtil.md4_gg(b, c, d, a, x[i+14], 13);
    a = ZaSambaUtil.md4_gg(a, b, c, d, x[i+ 3], 3 );
    d = ZaSambaUtil.md4_gg(d, a, b, c, x[i+ 7], 5 );
    c = ZaSambaUtil.md4_gg(c, d, a, b, x[i+11], 9 );
    b = ZaSambaUtil.md4_gg(b, c, d, a, x[i+15], 13);

    a = ZaSambaUtil.md4_hh(a, b, c, d, x[i+ 0], 3 );
    d = ZaSambaUtil.md4_hh(d, a, b, c, x[i+ 8], 9 );
    c = ZaSambaUtil.md4_hh(c, d, a, b, x[i+ 4], 11);
    b = ZaSambaUtil.md4_hh(b, c, d, a, x[i+12], 15);
    a = ZaSambaUtil.md4_hh(a, b, c, d, x[i+ 2], 3 );
    d = ZaSambaUtil.md4_hh(d, a, b, c, x[i+10], 9 );
    c = ZaSambaUtil.md4_hh(c, d, a, b, x[i+ 6], 11);
    b = ZaSambaUtil.md4_hh(b, c, d, a, x[i+14], 15);
    a = ZaSambaUtil.md4_hh(a, b, c, d, x[i+ 1], 3 );
    d = ZaSambaUtil.md4_hh(d, a, b, c, x[i+ 9], 9 );
    c = ZaSambaUtil.md4_hh(c, d, a, b, x[i+ 5], 11);
    b = ZaSambaUtil.md4_hh(b, c, d, a, x[i+13], 15);
    a = ZaSambaUtil.md4_hh(a, b, c, d, x[i+ 3], 3 );
    d = ZaSambaUtil.md4_hh(d, a, b, c, x[i+11], 9 );
    c = ZaSambaUtil.md4_hh(c, d, a, b, x[i+ 7], 11);
    b = ZaSambaUtil.md4_hh(b, c, d, a, x[i+15], 15);

    a = ZaSambaUtil.safe_add(a, olda);
    b = ZaSambaUtil.safe_add(b, oldb);
    c = ZaSambaUtil.safe_add(c, oldc);
    d = ZaSambaUtil.safe_add(d, oldd);

  }
  return Array(a, b, c, d);

}

/*
 * These functions implement the basic operation for each round of the
 * algorithm.
 */
ZaSambaUtil.md4_cmn=function(q, a, b, x, s, t)
{
  return ZaSambaUtil.safe_add(ZaSambaUtil.rol(ZaSambaUtil.safe_add(ZaSambaUtil.safe_add(a, q), ZaSambaUtil.safe_add(x, t)), s), b);
}
ZaSambaUtil.md4_ff=function(a, b, c, d, x, s)
{
  return ZaSambaUtil.md4_cmn((b & c) | ((~b) & d), a, 0, x, s, 0);
}
ZaSambaUtil.md4_gg=function(a, b, c, d, x, s)
{
  return ZaSambaUtil.md4_cmn((b & c) | (b & d) | (c & d), a, 0, x, s, 1518500249);
}
ZaSambaUtil.md4_hh=function(a, b, c, d, x, s)
{
  return ZaSambaUtil.md4_cmn(b ^ c ^ d, a, 0, x, s, 1859775393);
}

/*
 * Calculate the HMAC-MD4, of a key and some data
 */
ZaSambaUtil.core_hmac_md4=function(key, data)
{
  var bkey = ZaSambaUtil.str2binl(key);
  if(bkey.length > 16) bkey = ZaSambaUtil.core_md4(bkey, key.length * ZaSambaUtil.chrsz);

  var ipad = Array(16), opad = Array(16);
  for(var i = 0; i < 16; i++) 
  {
    ipad[i] = bkey[i] ^ 0x36363636;
    opad[i] = bkey[i] ^ 0x5C5C5C5C;
  }

  var hash = ZaSambaUtil.core_md4(ipad.concat(ZaSambaUtil.str2binl(data)), 512 + data.length * ZaSambaUtil.chrsz);
  return ZaSambaUtil.core_md4(opad.concat(hash), 512 + 128);
}

/*
 * Add integers, wrapping at 2^32. This uses 16-bit operations internally
 * to work around bugs in some JS interpreters.
 */
ZaSambaUtil.safe_add=function(x, y)
{
  var lsw = (x & 0xFFFF) + (y & 0xFFFF);
  var msw = (x >> 16) + (y >> 16) + (lsw >> 16);
  return (msw << 16) | (lsw & 0xFFFF);
}

/*
 * Bitwise rotate a 32-bit number to the left.
 */
ZaSambaUtil.rol=function(num, cnt)
{
  return (num << cnt) | (num >>> (32 - cnt));
}

/*
 * Convert a string to an array of little-endian words
 * If ZaSambaUtil.chrsz is ASCII, characters >255 have their hi-byte silently ignored.
 */
ZaSambaUtil.str2binl=function(str)
{
  var bin = Array();
  var mask = (1 << ZaSambaUtil.chrsz) - 1;
  for(var i = 0; i < str.length * ZaSambaUtil.chrsz; i += ZaSambaUtil.chrsz)
    bin[i>>5] |= (str.charCodeAt(i / ZaSambaUtil.chrsz) & mask) << (i%32);
  return bin;
}

/*
 * Convert an array of little-endian words to a string
 */
ZaSambaUtil.binl2str=function(bin)
{
  var str = "";
  var mask = (1 << ZaSambaUtil.chrsz) - 1;
  for(var i = 0; i < bin.length * 32; i += ZaSambaUtil.chrsz)
    str += String.fromCharCode((bin[i>>5] >>> (i % 32)) & mask);
  return str;
}

/*
 * Convert an array of little-endian words to a hex string.
 */
ZaSambaUtil.binl2hex=function(binarray)
{
  var hex_tab = ZaSambaUtil.hexcase ? "0123456789ABCDEF" : "0123456789abcdef";
  var str = "";
  for(var i = 0; i < binarray.length * 4; i++)
  {
    str += hex_tab.charAt((binarray[i>>2] >> ((i%4)*8+4)) & 0xF) +
           hex_tab.charAt((binarray[i>>2] >> ((i%4)*8  )) & 0xF);
  }
  return str;
}

/*
 * Convert an array of little-endian words to a base-64 string
 */
ZaSambaUtil.binl2b64=function(binarray)
{
  var tab = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
  var str = "";
  for(var i = 0; i < binarray.length * 4; i += 3)
  {
    var triplet = (((binarray[i   >> 2] >> 8 * ( i   %4)) & 0xFF) << 16)
                | (((binarray[i+1 >> 2] >> 8 * ((i+1)%4)) & 0xFF) << 8 )
                |  ((binarray[i+2 >> 2] >> 8 * ((i+2)%4)) & 0xFF);
    for(var j = 0; j < 4; j++)
    {
      if(i * 8 + j * 6 > binarray.length * 32) str += ZaSambaUtil.b64pad;
      else str += tab.charAt((triplet >> 6*(3-j)) & 0x3F);
    }
  }
  return str;
}
