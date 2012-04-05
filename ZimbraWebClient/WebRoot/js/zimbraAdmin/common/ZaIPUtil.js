/**
 * Created by IntelliJ IDEA.
 * User: mingzhang
 * Date: 3/28/12
 * Time: 4:26 AM
 * To change this template use File | Settings | File Templates.
 */

ZaIPUtil = function () {

}

ZaIPUtil.MASK_RE = /^\d{1,3}$/;
ZaIPUtil.IP4_CHUNK_RE = ZaIPUtil.MASK_RE;
ZaIPUtil.IP6_CHUNK_RE = /^([A-Fa-f0-9]{1,4})$/;
ZaIPUtil.IPMIX_PREFIX_RE = /^::(f|F){4}:$/;
// The following doesn't need localization
ZaIPUtil.ERROR_CIDR_NULL = "ERROR_CIDR_NULL";
ZaIPUtil.ERROR_MASK_NULL = "ERROR_MASK_NULL";
ZaIPUtil.ERROR_MASK_FORMAT = "ERROR_MASK_FORMAT";
ZaIPUtil.ERROR_IP_NULL = "ERROR_IP_NULL";
ZaIPUtil.ERROR_IP_FORMAT = "ERROR_IP_FORMAT";
ZaIPUtil.ERROR_IP_VER_DISMATCH = "ERROR_IP_VER_DISMATCH";
ZaIPUtil.isValidCIDR = function (cidrStr) {
    if (!cidrStr)
        throw ZaIPUtil.ERROR_CIDR_NULL;

    var maskIndex = cidrStr.indexOf("/");
    if (maskIndex == -1) {
        throw ZaIPUtil.ERROR_MASK_NULL;
    }
    var ipPart = cidrStr.substring(0, maskIndex);
    var maskPart = cidrStr.substring(maskIndex + 1);
    if (!ZaIPUtil.MASK_RE.test(maskPart)) {
        throw ZaIPUtil.ERROR_MASK_FORMAT;
    }
    var maskData = parseInt (maskPart, 10);
    var ipData = ZaIPUtil.isValidIP (ipPart);
    if (!ipData)
        return "";

    if (ipData.ver == ZaIPData.v4) {
        if (maskData > 32)
            throw ZaIPUtil.ERROR_MASK_FORMAT;
    } else {
        if (maskData > 128)
            throw ZaIPUtil.ERROR_MASK_FORMAT;
    }

    return new ZaCIDRData(ipData, maskData, cidrStr);

}


// TODO  Add more check for parameter
// for nifs data we get from server and can't be modified in admin console.
// We will assume the input is valid here
ZaIPUtil.getNetworkAddr = function (ipData, maskData) {
    var totalBit;
    var bitPerChunk;
    if (ipData.ver == ZaIPData.v4) {
        totalBit = 32;
        bitPerChunk = 8;
    } else if (ipData.ver == ZaIPData.v6){
        totalBit = 128;
        bitPerChunk = 16;
    }

    var allOneLength = Math.floor(maskData / bitPerChunk);
    var highOneBitNum = maskData % bitPerChunk;
    var nonZeroChunk =  ZaIPUtil.getNonZeroNum(highOneBitNum, bitPerChunk);
    var netIPData = [];
    for (var i = 0; i < ipData.fmtArr.length; i++) {
        if (allOneLength > 0) {
            netIPData.push(ipData.fmtArr[i]);
            allOneLength --;
        } else if (nonZeroChunk != 0) {
            var tmp = ipData.fmtArr[i] & nonZeroChunk;
            netIPData.push(tmp);
            nonZeroChunk = 0;
        } else {
            netIPData.push(0);
        }
    }

    return new ZaCIDRData(new ZaIPData(netIPData, ipData.ver, undefined, ipData.src), maskData);
}

ZaIPUtil.getNonZeroNum = function (sobn, chunkSize) {
    if (sobn == 0)
        return 0;
    var ret = 0;
    var baseValue = Math.pow(2, chunkSize - sobn);
    for (var i = 0; i < sobn; i++) {
        ret = ret + baseValue;
        baseValue = baseValue * 2;
    }
    return ret;
}

ZaIPUtil.countOneBit = function (num) {
    var ret = 0;
    while (num) {
        ret ++;
        num = num & (num -1);
    }
    return ret;
}

ZaIPUtil.getNetBit = function (netMask) {
    var isDot = (netMask.indexOf(".") != -1) ? true: false;
    var isColon = (netMask.indexOf(":") != -1) ? true: false;
    if ( !isDot && !isColon) {
        var netValue = parseInt(netMask, 16);
        return ZaIPUtil.countOneBit(netValue);
    }

    var ipData = ZaIPUtil.isValidIP(netMask);
    if (!ipData) {
        return 0;
    }

    var netBit = 0;
    for(var i = 0; i < ipData.fmtArr.length; i++) {
        if (ipData.fmtArr[i] == 0)
            break;
        netBit = netBit + ZaIPUtil.countOneBit(ipData.fmtArr[i]);
    }

    return netBit;
}

ZaIPUtil.isInSubNet = function (cidrData, ipData) {
    var cidrIP = cidrData.ipData;
    var mask = cidrData.mask;
    if (cidrIP.ver != ipData.ver) {
        return false;
    }

    var leftBitNum = mask;
    var currentCmpBit, crtFirstValue, crtSecondValue;
    var chunkSize = (cidrIP.ver == ZaIPData.v4) ? 8 : 16;
    for (var i = 0 ;i < cidrIP.fmtArr.length; i++) {
        if (leftBitNum == 0) {
            break;
        }

        if (leftBitNum >= chunkSize) {
            currentCmpBit = chunkSize;
        } else {
            currentCmpBit = leftBitNum;
        }

        leftBitNum = leftBitNum - chunkSize;

        crtFirstValue = cidrIP.fmtArr[i] >> (chunkSize - currentCmpBit);
        crtSecondValue = ipData.fmtArr[i] >> (chunkSize - currentCmpBit);
        if (crtFirstValue !=  crtSecondValue)  {
            return false;
        }
    }
    return true;
}

ZaIPUtil.compareIP = function(first, second) {
    if (!first || !second) {
        throw ZaIPUtil.ERROR_IP_NULL;
    }
    if (first.ver != second.ver) {
        throw ZaIPUtil.ERROR_IP_VER_DISMATCH;
    }

    if (first.fmtArr.length != second.fmtArr.length) {
        throw ZaIPUtil.ERROR_IP_VER_DISMATCH;
    }

    for (var i = 0; i < first.fmtArr.length; i++) {
        if (first.fmtArr[i] >  second.fmtArr[i])  {
            return 1;
        } else if (first.fmtArr[i] <  second.fmtArr[i]) {
            return -1;
        }
    }
    return 0;
}

ZaIPUtil.isValidIP = function (ipStr) {
    if (!ipStr) {
        throw ZaIPUtil.ERROR_IP_NULL;
    }

    var isDot = (ipStr.indexOf (".") == -1) ? false: true;
    var isColon = (ipStr.indexOf (":") == -1) ? false: true;
    if (!isColon && isDot) {
        return ZaIPUtil.isIPV4(ipStr);
    } else if (isColon && !isDot) {
        return ZaIPUtil.isIPV6(ipStr);
    } else if (isColon && isDot) {
        return ZaIPUtil.isIPVMix(ipStr);
    } else {
        throw ZaIPUtil.ERROR_IP_FORMAT;
    }
}

ZaIPUtil.isIPV4 = function (ipV4Str) {
    if (!ipV4Str) {
        throw ZaIPUtil.ERROR_IP_NULL;
    }
    var chunks = ipV4Str.split(".");
    if (chunks.length != 4) {
        throw ZaIPUtil.ERROR_IP_FORMAT;
    }

    var longValue;
    for (var i = 0; i < chunks.length; i++) {
        if (!chunks[i])
            throw ZaIPUtil.ERROR_IP_FORMAT;
        if (!ZaIPUtil.IP4_CHUNK_RE.test(chunks[i]))
            throw ZaIPUtil.ERROR_IP_FORMAT;

        // Must add 10 here to avoid leading 0.
        longValue = parseInt(chunks[i], 10);
        if (longValue > 255)
            throw ZaIPUtil.ERROR_IP_FORMAT;

        chunks[i] = longValue;
    }

    return new ZaIPData(chunks, ZaIPData.v4, undefined, ipV4Str);
}

ZaIPUtil.isIPV6 = function (ipV6Str) {
    if (!ipV6Str) {
        throw ZaIPUtil.ERROR_IP_NULL;
    }

    var ipCntStr;
    if ((ipV6Str[0] == "[") &&
        (ipV6Str[ipV6Str.length -1] == "]")) {
        ipCntStr =  ipV6Str.substring (1, ipV6Str.length - 1);
    } else {
        ipCntStr = ipV6Str;
    }

    var zoneIndex = ipCntStr.indexOf("%");
    var zoneContent;
    if (zoneIndex != -1) {
        zoneContent = ipCntStr.substring(zoneIndex + 1);
        ipCntStr = ipCntStr.substring(0, zoneIndex);
    }

    if (!ipCntStr) {
        throw ZaIPUtil.ERROR_IP_FORMAT;
    }

    // Only one group zero is allowed
    var isDottedQuad = ipCntStr.indexOf("::");
    if (isDottedQuad != -1) {
        if (ipCntStr.indexOf("::", isDottedQuad + 2) != -1)
            throw ZaIPUtil.ERROR_IP_FORMAT;
    }

    var chunks = ipCntStr.split(":");
    if (chunks.length > 8)
        throw ZaIPUtil.ERROR_IP_FORMAT;

    var zeroLocation;
    for (var i = 0; i < chunks.length; i++) {
        if (!chunks[i]) {
            if (zeroLocation === undefined) {
                zeroLocation = i;
            }
            chunks[i] = 0;
            continue;
        }

        if (!ZaIPUtil.IP6_CHUNK_RE.test(chunks[i])) {
            throw ZaIPUtil.ERROR_IP_FORMAT;
        }

        chunks[i] = parseInt(chunks[i], 16);
    }

    if (zeroLocation === undefined && (chunks.length != 8)) {
        throw ZaIPUtil.ERROR_IP_FORMAT;
    }

    if (chunks.length < 8 && (zeroLocation!== undefined)) {
        var addZeroNum = 8 - chunks.length;
        for(var j = 0; j < addZeroNum; j++) {
            chunks.splice(zeroLocation, 0, 0);
        }
    }

    return  new ZaIPData(chunks, ZaIPData.v6, zoneContent, ipV6Str);
}

// Convert the compatiable IP v6 to IP v4
ZaIPUtil.isIPVMix = function (ipVMixStr) {
    if (!ipVMixStr || ipVMixStr.length < 9 ) {
        throw ZaIPUtil.ERROR_IP_FORMAT;
    }

    var ipCntStr;
    if ((ipVMixStr[0] == "[") &&
        (ipVMixStr[ipVMixStr.length -1] == "]")) {
        ipCntStr =  ipVMixStr.substring (1, ipVMixStr.length - 1);
    } else {
        ipCntStr = ipVMixStr;
    }

    var ipV6Prefix = ipCntStr.substring(0, 7);
    if (!ZaIPUtil.IPMIX_PREFIX_RE.test(ipV6Prefix)) {
        throw ZaIPUtil.ERROR_IP_FORMAT;
    }

    var ipV4Str = ipCntStr.substring(7);
    var ipData = ZaIPUtil.isIPV4(ipV4Str);
    if (!ipData) {
        throw ZaIPUtil.ERROR_IP_FORMAT;
    }
    ipData.src = ipCntStr;
    return ipData;
}

ZaCIDRData = function (ipData, mask, src) {
    this.ipData = ipData;
    this.mask = mask;
    this.src = src;
}

ZaIPData = function (fmtArr, ver, zoneSection, src) {
    this.fmtArr= fmtArr;
    this.ver = ver;
    this.zoneSection = zoneSection;
    this.src = src;
}

ZaIPData.v4 = "4";
ZaIPData.v6 = "6";
ZaIPData.vMix = "Mix";
